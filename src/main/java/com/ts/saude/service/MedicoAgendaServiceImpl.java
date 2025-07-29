package com.ts.saude.service;

import java.util.List;
import com.ts.saude.model.MedicoAgenda;
import com.ts.saude.repository.MedicoAgendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicoAgendaServiceImpl implements MedicoAgendaService {

    @Autowired
    private MedicoAgendaRepository medicoAgendaRepository;

    @Override
    public boolean verificarHorarioValido(Long medicoId, LocalDateTime dataHora) {
        // Implementar validação conforme sua lógica
        return true;
    }

    // @Override
    // public List<String> getHorariosDisponiveis(Long medicoId, LocalDate data) {
    // DayOfWeek diaSemana = data.getDayOfWeek();

    // return medicoAgendaRepository
    // .findByMedico_IdAndDiaSemana(medicoId, diaSemana)
    // .stream()
    // .flatMap(a -> a.getHorariosDisponiveis().stream()) // pegar todos os horários
    // .distinct() // para evitar duplicidade
    // .collect(Collectors.toList());
    // }
    @Override
    public List<String> getHorariosDisponiveis(Long medicoId, LocalDate data) {
        DayOfWeek diaSemana = data.getDayOfWeek();

        List<MedicoAgenda> agendas = medicoAgendaRepository.findByMedico_Id(medicoId);

        return agendas.stream()
                .filter(a -> a.getDiasAtendimento().contains(diaSemana)) // filtra as agendas que tem o dia
                .flatMap(a -> a.getHorariosDisponiveis().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicoAgenda> getAgendasByMedicoId(Long medicoId) {
        return medicoAgendaRepository.findByMedico_Id(medicoId);
    }
}
