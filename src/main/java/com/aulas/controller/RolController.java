package com.aulas.controller;

import com.aulas.dto.RolDTO;
import com.aulas.model.Rol;
import com.aulas.service.ICrud;
import com.aulas.service.IRolService;
import com.aulas.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RolController extends CRUDController<Rol, RolDTO, Integer> {

    private final IRolService service;
    private final MapperUtil mapperUtil;

    @Override protected ICrud<Rol, Integer> getService() { return service; }
    @Override protected MapperUtil getMapperUtil() { return mapperUtil; }
}