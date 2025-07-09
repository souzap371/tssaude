package com.ts.saude.service;

import com.ts.saude.model.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientService {
    Patient save(Patient patient);

    List<Patient> findAll();

    Optional<Patient> findById(Long id);

    void deleteById(Long id);
}
