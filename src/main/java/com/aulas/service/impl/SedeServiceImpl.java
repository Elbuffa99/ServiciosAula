package com.aulas.service.impl;

import com.aulas.model.Sede;
import com.aulas.repo.IGenericRepo;
import com.aulas.repo.ISedeRepo;
import com.aulas.service.ISedeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SedeServiceImpl extends CRUDImpl<Sede, Integer> implements ISedeService {

    private final ISedeRepo repo;

    @Override
    protected IGenericRepo<Sede, Integer> getRepo() {
        return repo;
    }

}