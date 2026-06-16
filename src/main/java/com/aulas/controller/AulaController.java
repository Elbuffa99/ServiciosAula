package com.aulas.controller;

import com.aulas.dto.AulaDTO;
import com.aulas.model.Aula;
import com.aulas.service.IAulaService;
import com.aulas.service.ICrud;
import com.aulas.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/aulas")
@RequiredArgsConstructor
public class AulaController extends CRUDController<Aula, AulaDTO, Integer> {

    private final IAulaService service;
    private final MapperUtil mapperUtil;

    @Override protected ICrud<Aula, Integer> getService() {
        return service; }
    @Override protected MapperUtil getMapperUtil() {
        return mapperUtil; }

    // ===== Endpoint custom =====
    @GetMapping("/disponibles")
    public ResponseEntity<List<AulaDTO>> findDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam Integer idHorario,
            @RequestParam Integer idSede) throws Exception {
        return ResponseEntity.ok(service.findDisponibles(fecha, idHorario, idSede));
    }
}