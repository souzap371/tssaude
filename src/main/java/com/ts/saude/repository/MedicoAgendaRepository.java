package com.ts.saude.repository;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ts.saude.model.MedicoAgenda;
import com.ts.saude.model.User;

public interface MedicoAgendaRepository extends JpaRepository<MedicoAgenda, Long> {
    Optional<MedicoAgenda> findByUser(User user);

    List<MedicoAgenda> findByMedicoIdAndDiaSemana(Long medicoId, DayOfWeek diaSemana);

    List<MedicoAgenda> findByMedicoId(Long medicoId);

    List<MedicoAgenda> findByMedico_IdAndDiaSemana(Long medicoId, DayOfWeek diaSemana);

}
