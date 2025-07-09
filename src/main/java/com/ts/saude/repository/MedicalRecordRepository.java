package com.ts.saude.repository;

import com.ts.saude.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    // Buscar histórico de evoluções de um paciente
    List<MedicalRecord> findByPatientIdOrderByRecordDateTimeDesc(Long patientId);
}
