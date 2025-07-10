package com.ts.saude.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ts.saude.model.MedicoAgenda;
import com.ts.saude.model.User;
import com.ts.saude.repository.UserRepository;

@Service
public interface MedicoAgendaService {

    boolean verificarHorarioValido(Long medicoId, LocalDateTime appointmentDateTime);

    List<String> getHorariosDisponiveis(Long medicoId, LocalDate data);

    List<MedicoAgenda> getAgendasByMedicoId(Long medicoId); // ✅ novo método

}