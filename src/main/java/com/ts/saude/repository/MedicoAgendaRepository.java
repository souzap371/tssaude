package com.ts.saude.repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ts.saude.model.MedicoAgenda;
import com.ts.saude.model.User;

public interface MedicoAgendaRepository extends JpaRepository<MedicoAgenda, Long> {

    // Buscar agendas por médico e dia da semana
    List<MedicoAgenda> findByMedicoAndDiaSemana(User medico, DayOfWeek diaSemana);

    // Buscar todas as agendas de um médico
    List<MedicoAgenda> findByMedico(User medico);

    // Buscar agendas por ID do médico (usando caminho da propriedade)
    List<MedicoAgenda> findByMedico_Id(Long medicoId);

    // Buscar por ID do médico e dia da semana
    // List<MedicoAgenda> findByMedico_IdAndDiaSemana(Long medicoId, DayOfWeek
    // diaSemana);
}
