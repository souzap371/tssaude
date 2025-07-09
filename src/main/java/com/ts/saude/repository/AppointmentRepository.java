package com.ts.saude.repository;

import com.ts.saude.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Buscar agendamentos de um paciente
    List<Appointment> findByPatientId(Long patientId);

    // Buscar agendamentos de um médico
    List<Appointment> findByDoctorId(Long doctorId);

    // Buscar agendamentos por data/hora (exemplo)
    List<Appointment> findByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);
}
