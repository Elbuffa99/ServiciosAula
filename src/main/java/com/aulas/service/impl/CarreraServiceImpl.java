package com.aulas.service.impl;

import com.aulas.exception.ModelNotFoundException;
import com.aulas.model.Carrera;
import com.aulas.repo.IGenericRepo;
import com.aulas.repo.ICarreraRepo;
import com.aulas.service.ICarreraService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarreraServiceImpl extends CRUDImpl<Carrera, Integer> implements ICarreraService {

    private final ICarreraRepo repo;

    @Override
    protected IGenericRepo<Carrera, Integer> getRepo() {
        return repo;
    }

  /*  @Override
    @Transactional(readOnly = true)
    public List<Carrera> readAll() throws Exception {
        return repo.findAllConRelaciones();
    }

    @Override
    @Transactional(readOnly = true)
    public Carrera readByid(Integer id) throws Exception {
        return repo.findByIdConRelaciones(id)
                .orElseThrow(() -> new ModelNotFoundException("Carrera no encontrada: " + id));
    } */

}