package com.ts.saude.controller;

import com.ts.saude.model.Patient;
import com.ts.saude.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Operações de CRUD de Pacientes:
 * - Listar pacientes (GET /patients)
 * - Exibir formulário novo paciente (GET /patients/new)
 * - Salvar paciente (POST /patients)
 * - Editar paciente (GET /patients/edit/{id})
 * - Atualizar paciente (POST /patients/{id})
 * - Excluir paciente (GET /patients/delete/{id})
 */
@Controller
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    // Listar todos os pacientes
    @GetMapping
    public String listPatients(Model model) {
        List<Patient> patients = patientService.findAll();
        model.addAttribute("patients", patients);
        return "patients/list";
    }

    // Exibir formulário para novo paciente
    @GetMapping("/new")
    public String showPatientForm(Patient patient) {
        return "patients/form";
    }

    // Salvar novo paciente
    @PostMapping
    public String savePatient(@Valid Patient patient, BindingResult result) {
        if (result.hasErrors()) {
            return "patients/form";
        }
        patientService.save(patient);
        return "redirect:/patients";
    }

    // Exibir formulário de edição
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Patient patient = patientService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente inválido: " + id));
        model.addAttribute("patient", patient);
        return "patients/form";
    }

    // Atualizar paciente
    @PostMapping("/{id}")
    public String updatePatient(@PathVariable("id") Long id,
            @Valid Patient patient,
            BindingResult result) {
        if (result.hasErrors()) {
            patient.setId(id);
            return "patients/form";
        }
        patient.setId(id);
        patientService.save(patient);
        return "redirect:/patients";
    }

    // Excluir paciente
    @GetMapping("/delete/{id}")
    public String deletePatient(@PathVariable("id") Long id) {
        patientService.deleteById(id);
        return "redirect:/patients";
    }
}
