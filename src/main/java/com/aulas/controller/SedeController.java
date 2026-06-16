package com.aulas.controller;

import com.aulas.dto.SedeDTO;
import com.aulas.model.Sede;
import com.aulas.service.ICrud;
import com.aulas.service.ISedeService;
import com.aulas.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sedes")
@RequiredArgsConstructor
public class SedeController extends CRUDController<Sede, SedeDTO, Integer> {

    private final ISedeService service;
    private final MapperUtil mapperUtil;

    @Override protected ICrud<Sede, Integer> getService() { return service; }
    @Override protected MapperUtil getMapperUtil() { return mapperUtil; }
}