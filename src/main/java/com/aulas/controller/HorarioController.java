package com.aulas.controller;

import com.aulas.dto.HorarioDTO;
import com.aulas.model.Horario;
import com.aulas.service.ICrud;
import com.aulas.service.IHorarioService;
import com.aulas.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/horarios")
@RequiredArgsConstructor
public class HorarioController extends CRUDController<Horario, HorarioDTO, Integer> {

    private final IHorarioService service;
    private final MapperUtil mapperUtil;

    @Override protected ICrud<Horario, Integer> getService() { return service; }
    @Override protected MapperUtil getMapperUtil() { return mapperUtil; }
}