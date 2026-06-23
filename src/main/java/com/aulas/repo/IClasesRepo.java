package com.aulas.repo;

import com.aulas.model.Clases;
import com.aulas.model.DiaSemana;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface IClasesRepo extends IGenericRepo<Clases, Integer> {

    // Clases regulares programadas para ese día de la semana + horario + sede
    List<Clases> findByDiaSemanaAndHorario_IdHorarioAndAula_Sede_IdSede(
            DiaSemana diaSemana,
            Integer idHorario,
            Integer idSede
    );

    @Query("SELECT c FROM Clases c " +
            "LEFT JOIN FETCH c.aula a " +
            "LEFT JOIN FETCH a.sede " +
            "LEFT JOIN FETCH a.tipoAula " +
            "LEFT JOIN FETCH c.horario")
    List<Clases> findAllConRelaciones();

    @Query("SELECT c FROM Clases c " +
            "LEFT JOIN FETCH c.aula a " +
            "LEFT JOIN FETCH a.sede " +
            "LEFT JOIN FETCH a.tipoAula " +
            "LEFT JOIN FETCH c.horario " +
            "WHERE c.idClases = :id")
    Optional<Clases> findByIdConRelaciones(@Param("id") Integer id);
}