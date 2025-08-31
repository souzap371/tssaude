package com.ts.saude.service;

import com.ts.saude.model.Appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {
    Appointment save(Appointment appointment);

    List<Appointment> findAll();

    Optional<Appointment> findById(Long id);

    void deleteById(Long id);

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByDoctorAndDate(Long medicoId, LocalDate data);

}
