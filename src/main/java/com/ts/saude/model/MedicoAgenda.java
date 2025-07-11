package com.ts.saude.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medico_agenda")
@Getter
@Setter
@NoArgsConstructor
public class MedicoAgenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Duração padrão das consultas do médico (em minutos)
    @Column(name = "duracao_consulta")
    private Integer duracaoConsulta;

    // Dias da semana que o médico atende (Segunda, Terça, etc.)
    @ElementCollection
    @CollectionTable(name = "medico_dias_atendimento", joinColumns = @JoinColumn(name = "agenda_id"))
    @Column(name = "dia")
    private List<String> diasAtendimento;

    // Horários disponíveis por dia (08:00, 08:30, etc.)
    @ElementCollection
    @CollectionTable(name = "medico_horarios_disponiveis", joinColumns = @JoinColumn(name = "agenda_id"))
    @Column(name = "horario")
    private List<String> horariosDisponiveis = new ArrayList<>();

    // Dia da semana específico (para validação por dia)
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana")
    private DayOfWeek diaSemana;

    // Relacionamento com o usuário (médico)
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User medico;
}
