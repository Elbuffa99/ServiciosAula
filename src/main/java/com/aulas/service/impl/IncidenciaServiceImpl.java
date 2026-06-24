package com.aulas.service.impl;

import com.aulas.exception.ModelNotFoundException;
import com.aulas.model.Incidencia;
import com.aulas.repo.IGenericRepo;
import com.aulas.repo.IIncidenciaRepo;
import com.aulas.service.IIncidenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidenciaServiceImpl extends CRUDImpl<Incidencia, Integer> implements IIncidenciaService {

    private final IIncidenciaRepo repo;

    @Override
    protected IGenericRepo<Incidencia, Integer> getRepo() {
        return repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Incidencia> readAll() throws Exception {
        return repo.findAllConRelaciones();
    }

    @Override
    @Transactional(readOnly = true)
    public Incidencia readByid(Integer id) throws Exception {
        return repo.findByIdConRelaciones(id)
                .orElseThrow(() -> new ModelNotFoundException("Incidencia no encontrada: " + id));
    }

}