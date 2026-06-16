package com.aulas.repo;
import com.aulas.model.Reserva;

import java.time.LocalDate;
import java.util.List;

public interface IReservaRepo extends IGenericRepo<Reserva, Integer> {

    // Reservas activas (no canceladas ni rechazadas) para esa fecha+horario+sede
    List<Reserva> findByFechaReservaAndHorario_IdHorarioAndSede_IdSedeAndEstadoReserva_DescripcionNotIn(
            LocalDate fechaReserva,
            Integer idHorario,
            Integer idSede,
            List<String> estadosExcluidos
    );

    List<Reserva> findByUsuario_CorreoInstitucionalOrderByFechaReservaDesc(String correo);
}