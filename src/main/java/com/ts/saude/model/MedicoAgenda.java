package com.ts.saude.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class MedicoAgenda {

    @ManyToOne
    @JoinColumn(name = "medico_id")
    private User medico; // ou Medico, dependendo do seu modelo

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_da_semana")
    private DayOfWeek diaSemana;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "duracao_da_consulta")
    private Integer duracaoConsulta; // em minutos

    @ElementCollection
    @CollectionTable(name = "medico_dias_atendimento", joinColumns = @JoinColumn(name = "medico_agenda_id"))
    @Column(name = "dia")
    private List<String> diasAtendimento; // Segunda, Terça, etc.

    @ElementCollection
    @CollectionTable(name = "medico_horarios", joinColumns = @JoinColumn(name = "medico_agenda_id"))
    @Column(name = "horario")
    private List<String> horariosDisponiveis; // 08:00, 08:30, etc.

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDuracaoConsulta() {
        return duracaoConsulta;
    }

    public void setDuracaoConsulta(Integer duracaoConsulta) {
        this.duracaoConsulta = duracaoConsulta;
    }

    public List<String> getDiasAtendimento() {
        return diasAtendimento;
    }

    public void setDiasAtendimento(List<String> diasAtendimento) {
        this.diasAtendimento = diasAtendimento;
    }

    public List<String> getHorariosDisponiveis() {
        return horariosDisponiveis;
    }

    public void setHorariosDisponiveis(List<String> horariosDisponiveis) {
        this.horariosDisponiveis = horariosDisponiveis;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DayOfWeek getDiaSemana() {
        return this.diaSemana;
    }

    // public LocalDateTime getHora() {
    // // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method 'getHora'");
    // }
}
