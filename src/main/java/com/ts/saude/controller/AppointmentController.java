package com.ts.saude.controller;

import com.ts.saude.model.Appointment;
import com.ts.saude.model.MedicoAgenda;
import com.ts.saude.model.User;
import com.ts.saude.service.AppointmentService;
import com.ts.saude.service.MedicoAgendaService;
import com.ts.saude.service.PatientService;
import com.ts.saude.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

        @Autowired
        private AppointmentService appointmentService;

        @Autowired
        private PatientService patientService;

        @Autowired
        private UserService userService;

        @Autowired
        private MedicoAgendaService medicoAgendaService;

        // Listar todos os agendamentos
        @GetMapping
        public String listAppointments(Model model) {
                List<Appointment> appointments = appointmentService.findAll();
                model.addAttribute("appointments", appointments);
                return "appointments/list";
        }

        // Exibir formulário de novo agendamento
        @GetMapping("/new")
        public String showAppointmentForm(Appointment appointment, Model model) {
                prepararFormulario(model);
                return "appointments/form";
        }

        // Salvar novo agendamento
        @PostMapping
        public String saveAppointment(@Valid Appointment appointment,
                        BindingResult result,
                        @RequestParam("appointmentDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate,
                        @RequestParam("appointmentTime") String appointmentTime,
                        Authentication authentication,
                        Model model) {

                if (result.hasErrors()) {
                        prepararFormulario(model);
                        return "appointments/form";
                }

                LocalTime time = LocalTime.parse(appointmentTime);
                LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, time);
                appointment.setAppointmentDateTime(appointmentDateTime);

                Long medicoId = appointment.getDoctor().getId();
                boolean horarioValido = medicoAgendaService.verificarHorarioValido(medicoId, appointmentDateTime);
                if (!horarioValido) {
                        result.rejectValue("appointmentDateTime", "error.appointment",
                                        "Horário inválido para este médico.");
                        prepararFormulario(model);
                        return "appointments/form";
                }

                String username = authentication.getName();
                User receptionist = userService.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
                appointment.setReceptionist(receptionist);

                appointmentService.save(appointment);
                return "redirect:/appointments";
        }

        // Exibir formulário de edição
        @GetMapping("/edit/{id}")
        public String showEditForm(@PathVariable("id") Long id, Model model) {
                Appointment appointment = appointmentService.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Agendamento inválido: " + id));
                model.addAttribute("appointment", appointment);
                prepararFormulario(model);
                return "appointments/form";
        }

        // Atualizar agendamento
        @PostMapping("/{id}")
        public String updateAppointment(@PathVariable("id") Long id,
                        @Valid Appointment appointment,
                        BindingResult result,
                        @RequestParam("appointmentDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate,
                        @RequestParam("appointmentTime") String appointmentTime,
                        Model model) {

                if (result.hasErrors()) {
                        appointment.setId(id);
                        prepararFormulario(model);
                        return "appointments/form";
                }

                LocalTime time = LocalTime.parse(appointmentTime);
                LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, time);
                appointment.setId(id);
                appointment.setAppointmentDateTime(appointmentDateTime);

                Long medicoId = appointment.getDoctor().getId();
                boolean horarioValido = medicoAgendaService.verificarHorarioValido(medicoId, appointmentDateTime);
                if (!horarioValido) {
                        result.rejectValue("appointmentDateTime", "error.appointment",
                                        "Horário inválido para este médico.");
                        prepararFormulario(model);
                        return "appointments/form";
                }

                appointmentService.save(appointment);
                return "redirect:/appointments";
        }

        // Excluir agendamento
        @GetMapping("/delete/{id}")
        public String deleteAppointment(@PathVariable("id") Long id) {
                appointmentService.deleteById(id);
                return "redirect:/appointments";
        }

        // AJAX: retorna horários disponíveis para um médico e data específica
        @GetMapping("/horarios-disponiveis")
        @ResponseBody
        public List<String> getHorariosDisponiveis(@RequestParam("medicoId") Long medicoId,
                        @RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
                return medicoAgendaService.getHorariosDisponiveis(medicoId, data);
        }

        // ✅ NOVO ENDPOINT: retorna dias da semana e horários disponíveis para um médico
        @GetMapping("/doctors/{id}/availability")
        @ResponseBody
        public Map<String, Object> getDisponibilidadeMedico(@PathVariable("id") Long medicoId) {
                Map<String, Object> disponibilidade = new HashMap<>();

                List<MedicoAgenda> agendas = medicoAgendaService.getAgendasByMedicoId(medicoId);

                Set<String> dias = agendas.stream()
                                .map(new java.util.function.Function<MedicoAgenda, String>() {
                                        @Override
                                        public String apply(MedicoAgenda a) {
                                                return traduzirDiaSemana(a.getDiaSemana());
                                        }
                                })
                                .collect(Collectors.toSet());

                Set<String> horarios = agendas.stream()
                                .flatMap(a -> a.getHorariosDisponiveis().stream()) // pega todos os horários da lista
                                .collect(Collectors.toSet());

                disponibilidade.put("availableDays", dias);
                disponibilidade.put("availableHours", horarios);

                // System.out.println("Dias disponíveis: " + dias);
                // System.out.println("Horários disponíveis: " + horarios);

                return disponibilidade;
        }

        // Utilitário
        private void prepararFormulario(Model model) {
                model.addAttribute("patients", patientService.findAll());
                List<User> doctors = userService.findAll().stream()
                                .filter(u -> u.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_DOCTOR")))
                                .toList();
                model.addAttribute("doctors", doctors);
        }

        private String traduzirDiaSemana(DayOfWeek dia) {
                return switch (dia) {
                        case MONDAY -> "Segunda";
                        case TUESDAY -> "Terça";
                        case WEDNESDAY -> "Quarta";
                        case THURSDAY -> "Quinta";
                        case FRIDAY -> "Sexta";
                        case SATURDAY -> "Sábado";
                        case SUNDAY -> "Domingo";
                };
        }

}
