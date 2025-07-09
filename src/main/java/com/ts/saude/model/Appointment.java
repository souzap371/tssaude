package com.ts.saude.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Paciente agendado
     */
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    /**
     * Médico que atenderá
     */
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    /**
     * Recepcionista que agendou (opcional, mas mantemos para histórico)
     */
    @ManyToOne
    @JoinColumn(name = "receptionist_id")
    private User receptionist;

    /**
     * Data e horário agendado
     */
    @NotNull(message = "Data e hora são obrigatórios")
    private LocalDateTime appointmentDateTime;

    /**
     * Status: AGENDADO, CONFIRMADO, CANCELADO, CONCLUIDO etc.
     * 
     * 
     */

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    public enum AppointmentStatus {
        SCHEDULED,
        COMPLETED,
        CANCELED
    }
}
