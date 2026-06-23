package com.aulas.service.impl;

import com.aulas.exception.ModelNotFoundException;
import com.aulas.model.Clases;
import com.aulas.repo.IClasesRepo;
import com.aulas.repo.IGenericRepo;
import com.aulas.service.IClasesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClasesServiceImpl extends CRUDImpl<Clases, Integer> implements IClasesService {

    private final IClasesRepo repo;

    @Override
    protected IGenericRepo<Clases, Integer> getRepo() {
        return repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Clases> readAll() throws Exception {
        return repo.findAllConRelaciones();
    }

    @Override
    @Transactional(readOnly = true)
    public Clases readByid(Integer id) throws Exception {
        return repo.findByIdConRelaciones(id)
                .orElseThrow(() -> new ModelNotFoundException("Clase no encontrada: " + id));
    }

}