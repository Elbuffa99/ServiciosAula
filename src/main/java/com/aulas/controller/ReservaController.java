package com.aulas.controller;

import com.aulas.dto.ReservaDTO;
import com.aulas.model.Reserva;
import com.aulas.service.IReservaService;
import com.aulas.service.ICrud;
import com.aulas.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController extends CRUDController<Reserva, ReservaDTO, Integer> {

    private final IReservaService service;
    private final MapperUtil mapperUtil;

    @Override protected ICrud<Reserva, Integer> getService() { return service; }
    @Override protected MapperUtil getMapperUtil() { return mapperUtil; }

    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaDTO>> misReservas(
            org.springframework.security.core.Authentication auth) throws Exception {
        // auth.getName() devuelve el "subject" del JWT = correo institucional
        return ResponseEntity.ok(service.findMisReservas(auth.getName()));
    }
}