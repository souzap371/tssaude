package com.ts.saude.service;

import com.ts.saude.model.MedicalRecord;

import java.util.List;
import java.util.Optional;

public interface MedicalRecordService {
    MedicalRecord save(MedicalRecord record);

    List<MedicalRecord> findAll();

    Optional<MedicalRecord> findById(Long id);

    void deleteById(Long id);

    List<MedicalRecord> findByPatientId(Long patientId);
}
