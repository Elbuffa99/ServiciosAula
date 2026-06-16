package com.aulas.repo;
import com.aulas.model.Clases;
import com.aulas.model.DiaSemana;

import java.util.List;


public interface IClasesRepo extends IGenericRepo<Clases, Integer> {

    // Clases regulares programadas para ese día de la semana + horario + sede
    List<Clases> findByDiaSemanaAndHorario_IdHorarioAndAula_Sede_IdSede(
            DiaSemana diaSemana,
            Integer idHorario,
            Integer idSede
    );
}