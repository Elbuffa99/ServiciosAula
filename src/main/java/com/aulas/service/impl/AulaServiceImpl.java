package com.aulas.service.impl;

import com.aulas.dto.AulaDTO;
import com.aulas.dto.CupoAulaDTO;
import com.aulas.exception.ModelNotFoundException;
import com.aulas.model.Aula;
import com.aulas.model.DiaSemana;
import com.aulas.repo.IAulaRepo;
import com.aulas.repo.IClasesRepo;
import com.aulas.repo.IGenericRepo;
import com.aulas.repo.IReservaRepo;
import com.aulas.service.IAulaService;
import com.aulas.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AulaServiceImpl extends CRUDImpl<Aula, Integer> implements IAulaService {

    private final IAulaRepo repo;
    private final IReservaRepo reservaRepo;
    private final IClasesRepo clasesRepo;
    private final MapperUtil mapperUtil;

    @Override
    protected IGenericRepo<Aula, Integer> getRepo() {
        return repo;
    }


    @Override
    @Transactional(readOnly = true)
    public List<AulaDTO> findDisponibles(LocalDate fecha, Integer idHorario, Integer idSede) throws Exception {

        DiaSemana dia = mapDayOfWeek(fecha.getDayOfWeek());

        // Aulas con clase regular (bloqueadas totalmente)
        Set<Integer> aulasConClase = clasesRepo
                .findByDiaSemanaAndHorario_IdHorarioAndAula_Sede_IdSede(dia, idHorario, idSede)
                .stream()
                .map(c -> c.getAula().getIdAula())
                .collect(Collectors.toSet());

        // Filtrar aulas de la sede, sin clase regular, y que aún tengan cupo
        return repo.findAll()
                .stream()
                .filter(a -> a.getSede().getIdSede().equals(idSede))
                .filter(a -> !aulasConClase.contains(a.getIdAula()))
                .filter(a -> {
                    Long reservas = reservaRepo.contarReservasActivasEnSlot(
                            a.getIdAula(), fecha, idHorario);
                    return reservas < a.getCapacidad();  // ← cupo disponible
                })
                .map(a -> mapperUtil.map(a, AulaDTO.class))
                .toList();
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

    @Override
    @Transactional(readOnly = true)
    public CupoAulaDTO consultarCupo(Integer idAula, LocalDate fecha, Integer idHorario) throws Exception {
        Aula aula = repo.findById(idAula)
                .orElseThrow(() -> new ModelNotFoundException("Aula no encontrada: " + idAula));

        Long ocupado = reservaRepo.contarReservasActivasEnSlot(idAula, fecha, idHorario);
        long disponible = aula.getCapacidad() - ocupado;

        return CupoAulaDTO.builder()
                .idAula(aula.getIdAula())
                .codigoAula(aula.getCodigoAula())
                .capacidad(aula.getCapacidad())
                .ocupado(ocupado)
                .disponible(Math.max(0, disponible))
                .llena(disponible <= 0)
                .build();
    }
}