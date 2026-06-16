package com.aulas.controller;

import com.aulas.dto.TipoAulaDTO;
import com.aulas.model.TipoAula;
import com.aulas.service.ICrud;
import com.aulas.service.ITipoAulaService;
import com.aulas.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tipos-aula")
@RequiredArgsConstructor
public class TipoAulaController extends CRUDController<TipoAula, TipoAulaDTO, Integer> {

    private final ITipoAulaService service;
    private final MapperUtil mapperUtil;

    @Override protected ICrud<TipoAula, Integer> getService() { return service; }
    @Override protected MapperUtil getMapperUtil() { return mapperUtil; }
}