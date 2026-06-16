package com.aulas.controller;

import com.aulas.dto.ReporteSemanalDTO;
import com.aulas.model.ReporteSemanal;
import com.aulas.service.ICrud;
import com.aulas.service.IReporteSemanalService;
import com.aulas.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reportes-semanales")
@RequiredArgsConstructor
public class ReporteSemanalController extends CRUDController<ReporteSemanal, ReporteSemanalDTO, Integer> {

    private final IReporteSemanalService service;
    private final MapperUtil mapperUtil;

    @Override protected ICrud<ReporteSemanal, Integer> getService() { return service; }
    @Override protected MapperUtil getMapperUtil() { return mapperUtil; }
}