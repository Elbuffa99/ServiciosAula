package com.aulas.service.impl;

import com.aulas.model.Rol;
import com.aulas.repo.IGenericRepo;
import com.aulas.repo.IRolRepo;
import com.aulas.service.IRolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RolServiceImpl extends CRUDImpl<Rol, Integer> implements IRolService {

    private final IRolRepo repo;

    @Override
    protected IGenericRepo<Rol, Integer> getRepo() {
        return repo;
    }

}