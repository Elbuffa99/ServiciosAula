package com.aulas.service.impl;

import com.aulas.model.Carrera;
import com.aulas.repo.IGenericRepo;
import com.aulas.repo.ICarreraRepo;
import com.aulas.service.ICarreraService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarreraServiceImpl extends CRUDImpl<Carrera, Integer> implements ICarreraService {

    private final ICarreraRepo repo;

    @Override
    protected IGenericRepo<Carrera, Integer> getRepo() {
        return repo;
    }

}