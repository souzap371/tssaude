package com.ts.saude.controller;

import com.ts.saude.model.MedicalRecord;
import com.ts.saude.model.Patient;
import com.ts.saude.model.User;
import com.ts.saude.service.MedicalRecordService;
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
 * Operações de CRUD de Evolução Médica:
 * - Listar evoluções de um paciente (GET /medicalrecords?patientId=xxx)
 * - Exibir formulário nova evolução (GET /medicalrecords/new?patientId=xxx)
 * - Salvar evolução (POST /medicalrecords)
 * - Excluir evolução (GET /medicalrecords/delete/{id})
 */
@Controller
@RequestMapping("/medicalrecords")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private UserService userService;

    // Listar evoluções de um paciente
    @GetMapping
    public String listRecords(@RequestParam("patientId") Long patientId, Model model) {
        Patient patient = patientService.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente inválido: " + patientId));
        List<MedicalRecord> records = medicalRecordService.findByPatientId(patientId);
        model.addAttribute("patient", patient);
        model.addAttribute("records", records);
        return "medicalrecords/list";
    }

    // Exibir formulário para nova evolução de um paciente
    @GetMapping("/new")
    public String showRecordForm(@RequestParam("patientId") Long patientId, Model model) {
        Patient patient = patientService.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente inválido: " + patientId));
        MedicalRecord record = new MedicalRecord();
        record.setPatient(patient);
        model.addAttribute("record", record);
        return "medicalrecords/form";
    }

    // Salvar nova evolução
    @PostMapping
    public String saveRecord(@Valid MedicalRecord record,
            BindingResult result,
            @RequestParam("recordDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime recordDateTime,
            Authentication authentication,
            Model model) {
        if (result.hasErrors()) {
            // recarregar paciente para não perder referência
            model.addAttribute("patient", record.getPatient());
            return "medicalrecords/form";
        }
        record.setRecordDateTime(recordDateTime);
        // Médico que está logado
        String username = authentication.getName();
        User doctor = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        record.setDoctor(doctor);
        medicalRecordService.save(record);
        return "redirect:/medicalrecords?patientId=" + record.getPatient().getId();
    }

    // Excluir evolução
    @GetMapping("/delete/{id}")
    public String deleteRecord(@PathVariable("id") Long id) {
        MedicalRecord record = medicalRecordService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro inválido: " + id));
        Long patientId = record.getPatient().getId();
        medicalRecordService.deleteById(id);
        return "redirect:/medicalrecords?patientId=" + patientId;
    }
}
