package com.aulas.service.impl;

import com.aulas.dto.AulaDTO;
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
    public List<AulaDTO> findDisponibles(LocalDate fecha, Integer idHorario, Integer idSede) throws Exception {

        //1 Mapear java.time.DayOfWeek -> que seria el enum del día de semana
        DiaSemana dia = mapDayOfWeek(fecha.getDayOfWeek());

        //2 IDs de aulas con clase regular en ese día/hora/sede

        Set<Integer> aulasConClase = clasesRepo
                .findByDiaSemanaAndHorario_IdHorarioAndAula_Sede_IdSede(dia,idHorario,idSede)
                .stream()
                .map(c -> c.getAula().getIdAula())
                .collect(Collectors.toSet());

        //3 IDs de aulas con reserva activa en esa fecha/horario/sede

        Set<Integer> aulasReservadas = reservaRepo
                .findByFechaReservaAndHorario_IdHorarioAndSede_IdSedeAndEstadoReserva_DescripcionNotIn(fecha, idHorario, idSede, List.of("CANCELADA", "RECHAZADA"))
                .stream()
                .map(r -> r.getAula().getIdAula())
                .collect(Collectors.toSet());

        //4 Todas las aulas de la sede, filtrar las ocupadas, mapear a DTO

        List<Aula> aulasSede  = repo.findAll()
                .stream()
                .filter(a -> a.getSede().getIdSede().equals(idSede))
                .filter(a -> !aulasConClase.contains(a.getIdAula()))
                .filter(a -> !aulasReservadas.contains(a.getIdAula()))
                .toList();

        return mapperUtil.mapList(aulasSede, AulaDTO.class);
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