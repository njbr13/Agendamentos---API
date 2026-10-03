package com.ximed.agendamento_api.repository;

import com.ximed.agendamento_api.domain.entity.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, String> {

    List<Agendamento> findByPacienteId(String pacienteId);

    boolean existsByPacienteIdAndDataHoraBetween(String pacienteId, LocalDateTime inicio, LocalDateTime fim);
}
