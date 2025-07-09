package com.ts.saude.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "medical_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Paciente atendido
     */
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    /**
     * Médico responsável pelo registro da evolução
     */
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    /**
     * Data e hora da evolução
     */
    private LocalDateTime recordDateTime;

    /**
     * Descrição da evolução / atendimento
     */
    @NotBlank(message = "Descrição da evolução é obrigatória")
    @Column(columnDefinition = "TEXT")
    private String description;
}
