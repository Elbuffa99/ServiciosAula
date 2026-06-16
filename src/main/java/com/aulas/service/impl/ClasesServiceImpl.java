package com.aulas.service.impl;

import com.aulas.model.Clases;
import com.aulas.repo.IClasesRepo;
import com.aulas.repo.IGenericRepo;
import com.aulas.service.IClasesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClasesServiceImpl extends CRUDImpl<Clases, Integer> implements IClasesService {

    private final IClasesRepo repo;

    @Override
    protected IGenericRepo<Clases, Integer> getRepo() {
        return repo;
    }

}