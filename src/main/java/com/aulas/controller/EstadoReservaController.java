package com.aulas.controller;

import com.aulas.dto.EstadoReservaDTO;
import com.aulas.model.EstadoReserva;
import com.aulas.service.ICrud;
import com.aulas.service.IEstadoReservaService;
import com.aulas.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/estados-reserva")
@RequiredArgsConstructor
public class EstadoReservaController extends CRUDController<EstadoReserva, EstadoReservaDTO, Integer> {

    private final IEstadoReservaService service;
    private final MapperUtil mapperUtil;

    @Override protected ICrud<EstadoReserva, Integer> getService() { return service; }
    @Override protected MapperUtil getMapperUtil() { return mapperUtil; }
}