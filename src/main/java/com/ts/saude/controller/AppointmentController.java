package com.ts.saude.controller;

import com.ts.saude.dto.CalendarUtils;
import com.ts.saude.dto.SlotDTO;
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
import java.time.format.TextStyle;
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

        @GetMapping
        public String listAppointments(Model model) {
                List<Appointment> appointments = appointmentService.findAll();
                model.addAttribute("appointments", appointments);
                return "appointments/list";
        }

        @GetMapping("/new")
        public String showAppointmentForm(Appointment appointment, Model model) {
                prepararFormulario(model);
                return "appointments/form";
        }

        @GetMapping("/agenda")
        public String carregarAgenda(Model model,
                        @RequestParam(value = "data", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
                        @RequestParam(value = "medicoId", required = false) Long medicoId,
                        @RequestParam(value = "month", required = false) Integer month,
                        @RequestParam(value = "year", required = false) Integer year) {

                LocalDate hoje = LocalDate.now();
                LocalDate dataRef;

                if (data != null) {
                        dataRef = data;
                } else if (month != null && year != null) {
                        dataRef = LocalDate.of(year, month, 1);
                } else {
                        dataRef = hoje;
                }

                // Lista de médicos
                List<User> profissionais = userService.findAll().stream()
                                .filter(u -> u.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_DOCTOR")))
                                .toList();

                // Slots disponíveis
                List<SlotDTO> slots = new ArrayList<>();
                if (medicoId != null) {
                        List<String> horarios = medicoAgendaService.getHorariosDisponiveis(medicoId, dataRef);
                        for (String horario : horarios) {
                                slots.add(new SlotDTO(
                                                horario,
                                                false,
                                                "",
                                                "",
                                                null));
                        }

                        // Slots já agendados
                        List<Appointment> agendados = appointmentService.findByDoctorAndDate(medicoId, dataRef);
                        for (Appointment a : agendados) {
                                slots.add(new SlotDTO(
                                                a.getAppointmentDateTime().toLocalTime().toString(),
                                                true,
                                                a.getPatient().getName(),
                                                a.getDoctor().getFullName(),
                                                a.getId()));
                        }

                        // Ordenar por hora
                        slots.sort(Comparator.comparing(SlotDTO::getHora));
                }

                model.addAttribute("profissionais", profissionais);
                model.addAttribute("doctors", profissionais);
                model.addAttribute("selectedDate", dataRef);
                model.addAttribute("currentMonth",
                                dataRef.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR")));
                model.addAttribute("calendarWeeks", CalendarUtils.generateCalendar(dataRef));
                model.addAttribute("slots", slots);
                model.addAttribute("medicoId", medicoId);

                return "appointments/form";
        }

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

                LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, LocalTime.parse(appointmentTime));
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
                try {
                        appointmentService.save(appointment);
                        return "redirect:/appointments";
                } catch (IllegalStateException e) {
                        result.rejectValue("appointmentDateTime", "error.appointment", e.getMessage());
                        prepararFormulario(model);
                        return "appointments/form";
                }
        }

        @GetMapping("/edit/{id}")
        public String showEditForm(@PathVariable("id") Long id, Model model) {
                Appointment appointment = appointmentService.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Agendamento inválido: " + id));
                model.addAttribute("appointment", appointment);
                prepararFormulario(model);
                return "appointments/form";
        }

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

                LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, LocalTime.parse(appointmentTime));
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

        @GetMapping("/delete/{id}")
        public String deleteAppointment(@PathVariable("id") Long id) {
                appointmentService.deleteById(id);
                return "redirect:/appointments";
        }

        @GetMapping("/horarios-disponiveis")
        @ResponseBody
        public List<String> getHorariosDisponiveis(@RequestParam("medicoId") Long medicoId,
                        @RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
                return medicoAgendaService.getHorariosDisponiveis(medicoId, data);
        }

        // ✅ Atualizado para refletir a nova estrutura da entidade MedicoAgenda
        @GetMapping("/doctors/{id}/availability")
        @ResponseBody
        public Map<String, Object> getDisponibilidadeMedico(@PathVariable("id") Long medicoId) {
                Map<String, Object> disponibilidade = new HashMap<>();

                List<MedicoAgenda> agendas = medicoAgendaService.getAgendasByMedicoId(medicoId);
                if (agendas.isEmpty()) {
                        disponibilidade.put("availableDays", Collections.emptyList());
                        disponibilidade.put("availableHours", Collections.emptyList());
                        return disponibilidade;
                }

                MedicoAgenda agenda = agendas.get(0); // como o relacionamento é 1:1, deve haver apenas uma agenda

                // Converte dias da semana em datas reais (ex.: próximos 30 dias)
                // Converte a lista de dias da semana para enums DayOfWeek
                List<DayOfWeek> diasSemana = agenda.getDiasAtendimento().stream()
                                .map(DayOfWeek::valueOf) // Se estiver em inglês (MONDAY, TUESDAY)
                                .collect(Collectors.toList());

                // Gera as próximas 30 datas que batem com esses dias da semana
                LocalDate hoje = LocalDate.now();
                List<String> datasDisponiveis = new ArrayList<>();
                for (int i = 0; i < 30; i++) {
                        LocalDate dia = hoje.plusDays(i);
                        if (diasSemana.contains(dia.getDayOfWeek())) {
                                datasDisponiveis.add(dia.toString()); // yyyy-MM-dd
                        }
                }

                disponibilidade.put("availableDays", agenda.getDiasAtendimento());
                disponibilidade.put("availableHours", agenda.getHorariosDisponiveis());

                return disponibilidade;
        }

        private void prepararFormulario(Model model) {
                model.addAttribute("patients", patientService.findAll());
                List<User> doctors = userService.findAll().stream()
                                .filter(u -> u.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_DOCTOR")))
                                .toList();
                model.addAttribute("doctors", doctors);
        }

}
