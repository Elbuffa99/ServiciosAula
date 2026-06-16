package com.aulas.controller;

import com.aulas.dto.IncidenciaDTO;
import com.aulas.model.Incidencia;
import com.aulas.service.ICrud;
import com.aulas.service.IIncidenciaService;
import com.aulas.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/incidencias")
@RequiredArgsConstructor
public class IncidenciaController extends CRUDController<Incidencia, IncidenciaDTO, Integer> {

    private final IIncidenciaService service;
    private final MapperUtil mapperUtil;

    @Override protected ICrud<Incidencia, Integer> getService() { return service; }
    @Override protected MapperUtil getMapperUtil() { return mapperUtil; }
}