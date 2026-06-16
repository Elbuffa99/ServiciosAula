package com.aulas.service.impl;

import com.aulas.model.Incidencia;
import com.aulas.repo.IGenericRepo;
import com.aulas.repo.IIncidenciaRepo;
import com.aulas.service.IIncidenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncidenciaServiceImpl extends CRUDImpl<Incidencia, Integer> implements IIncidenciaService {

    private final IIncidenciaRepo repo;

    @Override
    protected IGenericRepo<Incidencia, Integer> getRepo() {
        return repo;
    }

}