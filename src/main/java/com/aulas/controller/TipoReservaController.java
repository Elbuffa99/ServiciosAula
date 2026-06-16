package com.aulas.controller;

import com.aulas.dto.TipoReservaDTO;
import com.aulas.model.TipoReserva;
import com.aulas.service.ICrud;
import com.aulas.service.ITipoReservaService;
import com.aulas.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tipos-reserva")
@RequiredArgsConstructor
public class TipoReservaController extends CRUDController<TipoReserva, TipoReservaDTO, Integer> {

    private final ITipoReservaService service;
    private final MapperUtil mapperUtil;

    @Override protected ICrud<TipoReserva, Integer> getService() { return service; }
    @Override protected MapperUtil getMapperUtil() { return mapperUtil; }
}