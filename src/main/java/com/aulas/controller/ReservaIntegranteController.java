package com.aulas.controller;

import com.aulas.dto.ReservaIntegranteDTO;
import com.aulas.model.ReservaIntegrante;
import com.aulas.service.ICrud;
import com.aulas.service.IReservaIntegranteService;
import com.aulas.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservas-integrantes")
@RequiredArgsConstructor
public class ReservaIntegranteController extends CRUDController<ReservaIntegrante, ReservaIntegranteDTO, Integer> {

    private final IReservaIntegranteService service;
    private final MapperUtil mapperUtil;

    @Override protected ICrud<ReservaIntegrante, Integer> getService() { return service; }
    @Override protected MapperUtil getMapperUtil() { return mapperUtil; }
}