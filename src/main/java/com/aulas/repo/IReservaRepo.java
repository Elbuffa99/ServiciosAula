package com.aulas.repo;
import com.aulas.model.Reserva;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IReservaRepo extends IGenericRepo<Reserva, Integer> {

    // Reservas activas (no canceladas ni rechazadas) para esa fecha+horario+sede
    List<Reserva> findByFechaReservaAndHorario_IdHorarioAndSede_IdSedeAndEstadoReserva_DescripcionNotIn(
            LocalDate fechaReserva,
            Integer idHorario,
            Integer idSede,
            List<String> estadosExcluidos
    );

    List<Reserva> findByUsuario_CorreoInstitucionalOrderByFechaReservaDesc(String correo);

    @Query("SELECT r FROM Reserva r " + "JOIN FETCH r.estadoReserva " + "JOIN FETCH r.usuario " + "WHERE r.idReserva = :id")
    Optional<Reserva> findByIdConEstadoYUsuario(Integer id);

    // Cuenta cuántas reservas activas hay para un aula+fecha+horario
    @Query("SELECT COUNT(r) FROM Reserva r " + "WHERE r.aula.idAula = :idAula " + "AND r.fechaReserva = :fecha " + "AND r.horario.idHorario = :idHorario " + "AND r.estadoReserva.descripcion NOT IN ('CANCELADA', 'RECHAZADA')")
    Long contarReservasActivasEnSlot(Integer idAula, LocalDate fecha, Integer idHorario);

    // ¿El usuario ya tiene reserva activa en ese fecha+horario? (regla 3)
    @Query("SELECT COUNT(r) > 0 FROM Reserva r " + "WHERE r.usuario.idUsuario = :idUsuario " + "AND r.fechaReserva = :fecha " + "AND r.horario.idHorario = :idHorario " + "AND r.estadoReserva.descripcion NOT IN ('CANCELADA', 'RECHAZADA')")
    boolean usuarioYaTieneReservaEnHorario(Integer idUsuario, LocalDate fecha, Integer idHorario);

    // ¿Cuántas reservas activas tiene el usuario ese día? (regla 4)
    @Query("SELECT COUNT(r) FROM Reserva r " + "WHERE r.usuario.idUsuario = :idUsuario " + "AND r.fechaReserva = :fecha " + "AND r.estadoReserva.descripcion NOT IN ('CANCELADA', 'RECHAZADA')")
    Long contarReservasUsuarioEnFecha(Integer idUsuario, LocalDate fecha);

    @Query("SELECT r FROM Reserva r " +
            "LEFT JOIN FETCH r.usuario u " +
            "LEFT JOIN FETCH u.rol " +
            "LEFT JOIN FETCH u.sede " +
            "LEFT JOIN FETCH u.carrera " +
            "LEFT JOIN FETCH r.aula a " +
            "LEFT JOIN FETCH a.sede " +
            "LEFT JOIN FETCH a.tipoAula " +
            "LEFT JOIN FETCH r.sede " +
            "LEFT JOIN FETCH r.horario " +
            "LEFT JOIN FETCH r.estadoReserva " +
            "LEFT JOIN FETCH r.tipoReserva")
    List<Reserva> findAllConRelaciones();

    @Query("SELECT r FROM Reserva r " +
            "LEFT JOIN FETCH r.usuario u " +
            "LEFT JOIN FETCH u.rol " +
            "LEFT JOIN FETCH u.sede " +
            "LEFT JOIN FETCH u.carrera " +
            "LEFT JOIN FETCH r.aula a " +
            "LEFT JOIN FETCH a.sede " +
            "LEFT JOIN FETCH a.tipoAula " +
            "LEFT JOIN FETCH r.sede " +
            "LEFT JOIN FETCH r.horario " +
            "LEFT JOIN FETCH r.estadoReserva " +
            "LEFT JOIN FETCH r.tipoReserva " +
            "WHERE r.idReserva = :id")
    Optional<Reserva> findByIdConRelaciones(@Param("id") Integer id);

}