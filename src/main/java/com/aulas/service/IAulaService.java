package com.aulas.service;

import com.aulas.dto.AulaDTO;
import com.aulas.dto.CupoAulaDTO;
import com.aulas.model.Aula;

import java.time.LocalDate;
import java.util.List;

public interface IAulaService extends ICrud<Aula, Integer>{

    CupoAulaDTO consultarCupo(Integer idAula, LocalDate fecha, Integer idHorario) throws Exception;
    List<AulaDTO> findDisponibles(LocalDate fecha, Integer idHorario, Integer idSede) throws Exception;
}
