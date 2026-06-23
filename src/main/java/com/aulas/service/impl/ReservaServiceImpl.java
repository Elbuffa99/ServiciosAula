package com.aulas.service.impl;

import com.aulas.dto.ReservaDTO;
import com.aulas.exception.BusinessException;
import com.aulas.exception.ModelNotFoundException;
import com.aulas.model.*;
import com.aulas.repo.*;
import com.aulas.service.IReservaService;
import com.aulas.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaServiceImpl extends CRUDImpl<Reserva, Integer> implements IReservaService {

    private final IReservaRepo repo;
    private final IClasesRepo clasesRepo;
    private final IAulaRepo aulaRepo;
    private final ISedeRepo sedeRepo;
    private final IUsuarioRepo usuarioRepo;
    private final IHorarioRepo horarioRepo;
    private final IEstadoReservaRepo estadoReservaRepo;
    private final ITipoReservaRepo tipoReservaRepo;
    private final IReservaIntegranteRepo integranteRepo;
    private final MapperUtil mapperUtil;

    private static final int MAX_RESERVAS_POR_DIA = 2;

    @Override
    protected IGenericRepo<Reserva, Integer> getRepo() {
        return repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaDTO> findMisReservas(String correo) throws Exception {
        return repo.findByUsuario_CorreoInstitucionalOrderByFechaReservaDesc(correo)
                .stream()
                .map(r -> mapperUtil.map(r, ReservaDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaDTO> findFiltradas(LocalDate fecha, Integer idSede, String estado) throws Exception {
        return repo.findAll()
                .stream()
                .filter(r -> fecha == null || r.getFechaReserva().equals(fecha))
                .filter(r -> idSede == null || r.getSede().getIdSede().equals(idSede))
                .filter(r -> estado == null || r.getEstadoReserva().getDescripcion().equalsIgnoreCase(estado))
                .map(r -> mapperUtil.map(r, ReservaDTO.class))
                .toList();
    }

    @Override
    @Transactional
    public Reserva save(Reserva reserva) throws Exception {

        crearReserva(reserva);
        return reserva;
    }

    @Override
    @Transactional
    public ReservaDTO crearReserva(Reserva reserva) throws Exception {
        Aula aula = aulaRepo.findById(reserva.getAula().getIdAula()).orElseThrow(() -> new ModelNotFoundException("Aula no encontrada: " + reserva.getAula().getIdAula()));reserva.setAula(aula);
        Sede sede = sedeRepo.findById(reserva.getSede().getIdSede()).orElseThrow(() -> new ModelNotFoundException("Sede no encontrada: " + reserva.getSede().getIdSede()));reserva.setSede(sede);
        Usuario usuario = usuarioRepo.findById(reserva.getUsuario().getIdUsuario()).orElseThrow(() -> new ModelNotFoundException("Usuario no encontrado: " + reserva.getUsuario().getIdUsuario()));reserva.setUsuario(usuario);
        Horario horario = horarioRepo.findById(reserva.getHorario().getIdHorario()).orElseThrow(() -> new ModelNotFoundException("Horario no encontrado: " + reserva.getHorario().getIdHorario()));reserva.setHorario(horario);
        EstadoReserva estado = estadoReservaRepo.findById(reserva.getEstadoReserva().getIdEstadoReserva()).orElseThrow(() -> new ModelNotFoundException("Estado no encontrado: " + reserva.getEstadoReserva().getIdEstadoReserva()));reserva.setEstadoReserva(estado);
        TipoReserva tipo = tipoReservaRepo.findById(reserva.getTipoReserva().getIdTipoReserva()).orElseThrow(() -> new ModelNotFoundException("Tipo no encontrado: " + reserva.getTipoReserva().getIdTipoReserva()));reserva.setTipoReserva(tipo);

        validarFecha(reserva);
        validarSedeAula(reserva);
        validarMaximoReservasDiarias(reserva);
        validarUsuarioNoTieneReservaEnHorario(reserva);
        validarNoConflictoConClase(reserva);
        validarCapacidadDisponible(reserva);


        reserva.setFechaSolicitud(java.time.LocalDateTime.now());

        Reserva saved = repo.save(reserva);

        return mapperUtil.map(saved, ReservaDTO.class);
    }

    @Override
    @Transactional
    public ReservaDTO aprobar(Integer idReserva) throws Exception{
        Reserva reserva = repo.findByIdConEstadoYUsuario(idReserva)
                .orElseThrow(() -> new ModelNotFoundException("Reserva no encontrada: " + idReserva));

        validarTransicion(reserva, "PENDIENTE", "aprobar");

        EstadoReserva aprobada = buscarEstadoPorDescripcion("APROBADA");
        reserva.setEstadoReserva(aprobada);

        return mapperUtil.map(repo.save(reserva), ReservaDTO.class);
    }

    @Override
    @Transactional
    public ReservaDTO rechazar(Integer idReserva) throws Exception {
        Reserva reserva = repo.findByIdConEstadoYUsuario(idReserva)
                .orElseThrow(() -> new ModelNotFoundException("Reserva no encontrada: " + idReserva));

        validarTransicion(reserva, "PENDIENTE", "rechazar");

        EstadoReserva rechazada = buscarEstadoPorDescripcion("RECHAZADA");
        reserva.setEstadoReserva(rechazada);

        return mapperUtil.map(repo.save(reserva), ReservaDTO.class);
    }

    @Override
    @Transactional
    public ReservaDTO cancelar(Integer idReserva, String correoUsuario) throws Exception {
        Reserva reserva = repo.findByIdConEstadoYUsuario(idReserva)
                .orElseThrow(() -> new ModelNotFoundException("Reserva no encontrada: " + idReserva));

        if (!reserva.getUsuario().getCorreoInstitucional().equalsIgnoreCase(correoUsuario)) {
            throw new BusinessException("Solo puedes cancelar tus propias reservas");
        }

        String estadoActual = reserva.getEstadoReserva().getDescripcion().toUpperCase();
        if (!estadoActual.equals("PENDIENTE") && !estadoActual.equals("APROBADA")) {
            throw new BusinessException("No se puede cancelar una reserva en estado " + estadoActual);
        }

        if (reserva.getFechaReserva().isBefore(LocalDate.now())) {
            throw new BusinessException("No se puede cancelar una reserva con fecha pasada");
        }

        EstadoReserva cancelada = buscarEstadoPorDescripcion("CANCELADA");
        reserva.setEstadoReserva(cancelada);

        return mapperUtil.map(repo.save(reserva), ReservaDTO.class);
    }

    private void validarTransicion(Reserva r, String estadoEsperado, String accion) {
        String actual = r.getEstadoReserva().getDescripcion().toUpperCase();
        if (!actual.equals(estadoEsperado)) {
            throw new BusinessException("No se puede " + accion + " una reserva en estado " + actual + ". Solo se permite cuando está en estado " + estadoEsperado);
        }
    }

    private EstadoReserva buscarEstadoPorDescripcion(String descripcion) {
        return estadoReservaRepo.findAll()
                .stream()
                .filter(e -> e.getDescripcion().equalsIgnoreCase(descripcion))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Estado '" + descripcion + "' no existe en BD. Inserta los seeds."));
    }

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

    // Regla 2: el aula tiene cupo (compartido hasta llegar a capacidad)
    private void validarCapacidadDisponible(Reserva r) {
        Long reservasActuales = repo.contarReservasActivasEnSlot(
                r.getAula().getIdAula(),
                r.getFechaReserva(),
                r.getHorario().getIdHorario()
        );

        Integer capacidad = r.getAula().getCapacidad();

        if (reservasActuales >= capacidad) {
            throw new BusinessException("El aula " + r.getAula().getCodigoAula() + " está llena en ese horario (capacidad: " + capacidad + ", reservas actuales: " + reservasActuales + ")");
        }
    }

    // Regla 3: el usuario no tiene ya reserva activa en ese slot
    private void validarUsuarioNoTieneReservaEnHorario(Reserva r) {
        boolean yaTiene = repo.usuarioYaTieneReservaEnHorario(
                r.getUsuario().getIdUsuario(),
                r.getFechaReserva(),
                r.getHorario().getIdHorario()
        );

        if (yaTiene) {
            throw new BusinessException(
                    "Ya tienes una reserva activa en ese horario");
        }
    }

    // Regla 4: máximo 2 reservas activas por día por usuario
    private void validarMaximoReservasDiarias(Reserva r) {
        Long reservasDelDia = repo.contarReservasUsuarioEnFecha(
                r.getUsuario().getIdUsuario(),
                r.getFechaReserva()
        );

        if (reservasDelDia >= MAX_RESERVAS_POR_DIA) {
            throw new BusinessException(
                    "Solo puedes reservar " + MAX_RESERVAS_POR_DIA +
                            " horarios por día. Ya tienes " + reservasDelDia +
                            " reservas activas para " + r.getFechaReserva());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> readAll() throws Exception {
        return repo.findAllConRelaciones();
    }

    @Override
    @Transactional(readOnly = true)
    public Reserva readByid(Integer id) throws Exception {
        return repo.findByIdConRelaciones(id)
                .orElseThrow(() -> new ModelNotFoundException("Reserva no encontrada: " + id));
    }

}