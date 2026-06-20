package com.aulas.service.impl;

import com.aulas.dto.ResumenReporteDTO;
import com.aulas.model.Reserva;
import com.aulas.repo.IAulaRepo;
import com.aulas.repo.IReservaRepo;
import com.aulas.repo.ISedeRepo;
import com.aulas.repo.IUsuarioRepo;
import com.aulas.service.IReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements IReporteService {

    private final IReservaRepo reservaRepo;
    private final IUsuarioRepo usuarioRepo;
    private final IAulaRepo aulaRepo;
    private final ISedeRepo sedeRepo;

    // ===== 1. Top usuarios que más reservan =====
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> topUsuariosQueReservan(int top) {
        return reservaRepo.findAll()
                .stream()
                .collect(groupingBy(
                        r -> r.getUsuario().getNombreUsuario() + " " + r.getUsuario().getApellidoUsuario(),
                        counting()
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(top)
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new   // mantiene el orden de inserción
                ));
    }

    // ===== 2. Ocupación por horario =====
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> ocupacionPorHorario() {
        return reservaRepo.findAll()
                .stream()
                .collect(groupingBy(
                        r -> r.getHorario().getHoraInicio() + " - " + r.getHorario().getHoraSalida(),
                        counting()
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    // ===== 3. Sedes con más demanda =====
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> sedesPorDemanda() {
        return reservaRepo.findAll()
                .stream()
                .collect(groupingBy(
                        r -> r.getSede().getNombreSede(),
                        counting()
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    // ===== 4. Aulas más usadas =====
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> aulasMasUsadas(int top) {
        return reservaRepo.findAll()
                .stream()
                .collect(groupingBy(
                        r -> r.getAula().getCodigoAula(),
                        counting()
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(top)
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    // ===== 5. Reservas por estado =====
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> reservasPorEstado() {
        return reservaRepo.findAll()
                .stream()
                .collect(groupingBy(
                        r -> r.getEstadoReserva().getDescripcion(),
                        counting()
                ));
    }

    // ===== 6. Reservas por día de semana =====
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> reservasPorDiaSemana() {
        return reservaRepo.findAll()
                .stream()
                .collect(groupingBy(
                        r -> r.getFechaReserva().getDayOfWeek().toString(),
                        counting()
                ));
    }

    // ===== 7. Resumen general =====
    @Override
    @Transactional(readOnly = true)
    public ResumenReporteDTO resumenGeneral() {

        Map<String, Long> porEstado = reservaRepo.findAll()
                .stream()
                .collect(groupingBy(
                        r -> r.getEstadoReserva().getDescripcion().toUpperCase(),
                        counting()
                ));

        Long total = porEstado.values().stream().mapToLong(Long::longValue).sum();
        Long aprobadas = porEstado.getOrDefault("APROBADA", 0L);
        Long canceladas = porEstado.getOrDefault("CANCELADA", 0L);

        // Helper para calcular % evitando división por 0
        double tasaAprobacion = total > 0 ? (aprobadas * 100.0) / total : 0.0;
        double tasaCancelacion = total > 0 ? (canceladas * 100.0) / total : 0.0;

        return ResumenReporteDTO.builder()
                .totalReservas(total)
                .reservasPendientes(porEstado.getOrDefault("PENDIENTE", 0L))
                .reservasAprobadas(aprobadas)
                .reservasRechazadas(porEstado.getOrDefault("RECHAZADA", 0L))
                .reservasCanceladas(canceladas)
                .totalUsuarios(usuarioRepo.count())
                .totalAulas(aulaRepo.count())
                .totalSedes(sedeRepo.count())
                .tasaAprobacion(Math.round(tasaAprobacion * 100.0) / 100.0)
                .tasaCancelacion(Math.round(tasaCancelacion * 100.0) / 100.0)
                .build();
    }
}