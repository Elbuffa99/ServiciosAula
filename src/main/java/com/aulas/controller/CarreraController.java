package com.aulas.controller;

import com.aulas.dto.CarreraDTO;
import com.aulas.model.Carrera;
import com.aulas.service.ICrud;
import com.aulas.service.ICarreraService;
import com.aulas.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carreras")
@RequiredArgsConstructor
public class CarreraController extends CRUDController<Carrera, CarreraDTO, Integer> {

    private final ICarreraService service;
    private final MapperUtil mapperUtil;

    @Override protected ICrud<Carrera, Integer> getService() { return service; }
    @Override protected MapperUtil getMapperUtil() { return mapperUtil; }
}