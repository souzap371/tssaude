package com.ts.saude.service.impl;

import com.ts.saude.model.MedicalRecord;
import com.ts.saude.repository.MedicalRecordRepository;
import com.ts.saude.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;

    @Autowired
    public MedicalRecordServiceImpl(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @Override
    public MedicalRecord save(MedicalRecord record) {
        return medicalRecordRepository.save(record);
    }

    @Override
    public List<MedicalRecord> findAll() {
        return medicalRecordRepository.findAll();
    }

    @Override
    public Optional<MedicalRecord> findById(Long id) {
        return medicalRecordRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        medicalRecordRepository.deleteById(id);
    }

    @Override
    public List<MedicalRecord> findByPatientId(Long patientId) {
        return medicalRecordRepository.findByPatientIdOrderByRecordDateTimeDesc(patientId);
    }
}
