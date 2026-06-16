package com.aulas.service.impl;

import com.aulas.model.ReservaIntegrante;
import com.aulas.repo.IGenericRepo;
import com.aulas.repo.IReservaIntegranteRepo;
import com.aulas.service.IReservaIntegranteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservaIntegranteServiceImpl extends CRUDImpl<ReservaIntegrante, Integer> implements IReservaIntegranteService {

    private final IReservaIntegranteRepo repo;

    @Override
    protected IGenericRepo<ReservaIntegrante, Integer> getRepo() {
        return repo;
    }

}