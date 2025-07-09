package com.ts.saude.controller;

import com.ts.saude.model.Appointment;
import com.ts.saude.model.User;
import com.ts.saude.service.AppointmentService;
import com.ts.saude.service.PatientService;
import com.ts.saude.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Operações de CRUD de Agendamentos:
 * - Listar agendamentos (GET /appointments)
 * - Exibir formulário novo agendamento (GET /appointments/new)
 * - Salvar agendamento (POST /appointments)
 * - Editar agendamento (GET /appointments/edit/{id})
 * - Atualizar agendamento (POST /appointments/{id})
 * - Excluir agendamento (GET /appointments/delete/{id})
 */
@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private UserService userService;

    // Listar todos os agendamentos
    @GetMapping
    public String listAppointments(Model model) {
        List<Appointment> appointments = appointmentService.findAll();
        model.addAttribute("appointments", appointments);
        return "appointments/list";
    }

    // Exibir formulário para novo agendamento
    @GetMapping("/new")
    public String showAppointmentForm(Appointment appointment, Model model) {
        model.addAttribute("patients", patientService.findAll());
        // Listar médicos (usuarios com ROLE_DOCTOR)
        List<User> doctors = userService.findAll().stream()
                .filter(u -> u.getRoles().stream()
                        .anyMatch(r -> r.getName().equals("ROLE_DOCTOR")))
                .toList();
        model.addAttribute("doctors", doctors);
        return "appointments/form";
    }

    // Salvar novo agendamento
    @PostMapping
    public String saveAppointment(@Valid Appointment appointment,
            BindingResult result,
            @RequestParam("appointmentDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
            Authentication authentication,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("patients", patientService.findAll());
            List<User> doctors = userService.findAll().stream()
                    .filter(u -> u.getRoles().stream()
                            .anyMatch(r -> r.getName().equals("ROLE_DOCTOR")))
                    .toList();
            model.addAttribute("doctors", doctors);
            return "appointments/form";
        }
        appointment.setAppointmentDateTime(appointmentDateTime);
        // O recepcionista que está logado (quem agenda)
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
        model.addAttribute("patients", patientService.findAll());
        List<User> doctors = userService.findAll().stream()
                .filter(u -> u.getRoles().stream()
                        .anyMatch(r -> r.getName().equals("ROLE_DOCTOR")))
                .toList();
        model.addAttribute("doctors", doctors);
        return "appointments/form";
    }

    // Atualizar agendamento
    @PostMapping("/{id}")
    public String updateAppointment(@PathVariable("id") Long id,
            @Valid Appointment appointment,
            BindingResult result,
            @RequestParam("appointmentDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
            Model model) {
        if (result.hasErrors()) {
            appointment.setId(id);
            model.addAttribute("patients", patientService.findAll());
            List<User> doctors = userService.findAll().stream()
                    .filter(u -> u.getRoles().stream()
                            .anyMatch(r -> r.getName().equals("ROLE_DOCTOR")))
                    .toList();
            model.addAttribute("doctors", doctors);
            return "appointments/form";
        }
        appointment.setId(id);
        appointment.setAppointmentDateTime(appointmentDateTime);
        appointmentService.save(appointment);
        return "redirect:/appointments";
    }

    // Excluir agendamento
    @GetMapping("/delete/{id}")
    public String deleteAppointment(@PathVariable("id") Long id) {
        appointmentService.deleteById(id);
        return "redirect:/appointments";
    }
}
