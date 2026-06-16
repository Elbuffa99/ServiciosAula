package com.aulas.controller;

import com.aulas.dto.ClasesDTO;
import com.aulas.model.Clases;
import com.aulas.service.IClasesService;
import com.aulas.service.ICrud;
import com.aulas.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clases")
@RequiredArgsConstructor
public class ClasesController extends CRUDController<Clases, ClasesDTO, Integer> {

    private final IClasesService service;
    private final MapperUtil mapperUtil;

    @Override protected ICrud<Clases, Integer> getService() { return service; }
    @Override protected MapperUtil getMapperUtil() { return mapperUtil; }
}