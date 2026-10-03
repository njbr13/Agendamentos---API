package com.ximed.agendamento_api.repository;

import com.ximed.agendamento_api.domain.entity.Exame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExameRepository extends JpaRepository<Exame, String> {

    List<Exame> findAllByAtivoTrue();
}
