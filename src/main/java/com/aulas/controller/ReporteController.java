package com.aulas.controller;

import com.aulas.dto.ResumenReporteDTO;
import com.aulas.service.IReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReporteController {

    private final IReporteService service;

    @GetMapping("/top-usuarios")
    public ResponseEntity<Map<String, Long>> topUsuarios(
            @RequestParam(defaultValue = "10") int top) throws Exception {
        return ResponseEntity.ok(service.topUsuariosQueReservan(top));
    }

    @GetMapping("/ocupacion-por-horario")
    public ResponseEntity<Map<String, Long>> ocupacionPorHorario() throws Exception {
        return ResponseEntity.ok(service.ocupacionPorHorario());
    }

    @GetMapping("/sedes-demanda")
    public ResponseEntity<Map<String, Long>> sedesDemanda() throws Exception {
        return ResponseEntity.ok(service.sedesPorDemanda());
    }

    @GetMapping("/aulas-mas-usadas")
    public ResponseEntity<Map<String, Long>> aulasMasUsadas(
            @RequestParam(defaultValue = "10") int top) throws Exception {
        return ResponseEntity.ok(service.aulasMasUsadas(top));
    }

    @GetMapping("/reservas-por-estado")
    public ResponseEntity<Map<String, Long>> reservasPorEstado() throws Exception {
        return ResponseEntity.ok(service.reservasPorEstado());
    }

    @GetMapping("/reservas-por-dia")
    public ResponseEntity<Map<String, Long>> reservasPorDia() throws Exception {
        return ResponseEntity.ok(service.reservasPorDiaSemana());
    }

    @GetMapping("/resumen-general")
    public ResponseEntity<ResumenReporteDTO> resumenGeneral() throws Exception {
        return ResponseEntity.ok(service.resumenGeneral());
    }
}