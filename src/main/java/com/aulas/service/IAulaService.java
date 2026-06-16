package com.aulas.service;

import com.aulas.dto.AulaDTO;
import com.aulas.model.Aula;

import java.time.LocalDate;
import java.util.List;

public interface IAulaService extends ICrud<Aula, Integer>{

    List<AulaDTO> findDisponibles(LocalDate fecha, Integer idHorario, Integer idSede) throws Exception;
}
