package com.aulas.service.impl;

import com.aulas.dto.ReservaDTO;
import com.aulas.exception.BusinessException;
import com.aulas.model.DiaSemana;
import com.aulas.model.Reserva;
import com.aulas.repo.IClasesRepo;
import com.aulas.repo.IReservaIntegranteRepo;
import com.aulas.repo.IReservaRepo;
import com.aulas.repo.IGenericRepo;
import com.aulas.service.IReservaService;
import com.aulas.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaServiceImpl extends CRUDImpl<Reserva, Integer> implements IReservaService {

    private final IReservaRepo repo;
    private final IClasesRepo clasesRepo;
    private final IReservaIntegranteRepo integranteRepo;
    private final MapperUtil mapperUtil;

    @Override
    protected IGenericRepo<Reserva, Integer> getRepo() {
        return repo;
    }

    @Override
    public List<ReservaDTO> findMisReservas(String correo) throws Exception {
        return List.of();
    }

    @Override
    public Reserva save(Reserva reserva) throws Exception {
        validarFecha(reserva);
        validarSedeAula(reserva);
        validarNoDoubleBooking(reserva);
        validarNoConflictoConClase(reserva);
        validarCapacidad(reserva);

        // Estado inicial siempre PENDIENTE — el cliente no puede mandar otro
        // (asumiendo que tienes un EstadoReserva con id=1 = PENDIENTE)
        // En real: lo buscas por descripcion
        reserva.setFechaSolicitud(java.time.LocalDateTime.now());
        return repo.save(reserva);
    }

    // ===== Validaciones funcionales =====

    private void validarFecha(Reserva r) {
        if (r.getFechaReserva().isBefore(LocalDate.now())) {
            throw new BusinessException("No se puede reservar en fecha pasada");
        }
        if (r.getFechaReserva().isAfter(LocalDate.now().plusDays(30))) {
            throw new BusinessException("No se puede reservar con más de 30 días de anticipación");
        }
    }

    private void validarSedeAula(Reserva r) {
        if (!r.getAula().getSede().getIdSede().equals(r.getSede().getIdSede())) {
            throw new BusinessException("El aula no pertenece a la sede indicada");
        }
    }

    private void validarNoDoubleBooking(Reserva r) {
        boolean ocupada = repo
                .findByFechaReservaAndHorario_IdHorarioAndSede_IdSedeAndEstadoReserva_DescripcionNotIn(
                        r.getFechaReserva(),
                        r.getHorario().getIdHorario(),
                        r.getSede().getIdSede(),
                        List.of("CANCELADA", "RECHAZADA"))
                .stream()
                .anyMatch(existente -> existente.getAula().getIdAula().equals(r.getAula().getIdAula()));

        if (ocupada) {
            throw new BusinessException("El aula ya está reservada en ese horario");
        }
    }

    private void validarNoConflictoConClase(Reserva r) {
        DiaSemana dia = mapDayOfWeek(r.getFechaReserva().getDayOfWeek());

        boolean hayClase = clasesRepo
                .findByDiaSemanaAndHorario_IdHorarioAndAula_Sede_IdSede(
                        dia, r.getHorario().getIdHorario(), r.getSede().getIdSede())
                .stream()
                .anyMatch(c -> c.getAula().getIdAula().equals(r.getAula().getIdAula()));

        if (hayClase) {
            throw new BusinessException("El aula tiene clase regular en ese horario");
        }
    }

    private void validarCapacidad(Reserva r) {
        // Si después agregas List<Integrante> al DTO de creación, validas aquí
        // Por ahora dejo el placeholder
    }

    private DiaSemana mapDayOfWeek(DayOfWeek dow) {
        return switch (dow) {
            case MONDAY -> DiaSemana.LUNES;
            case TUESDAY -> DiaSemana.MARTES;
            case WEDNESDAY -> DiaSemana.MIERCOLES;
            case THURSDAY -> DiaSemana.JUEVES;
            case FRIDAY -> DiaSemana.VIERNES;
            case SATURDAY -> DiaSemana.SABADO;
            case SUNDAY -> DiaSemana.DOMINGO;
        };
    }

}