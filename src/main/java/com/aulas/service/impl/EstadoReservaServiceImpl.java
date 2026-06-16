package com.aulas.service.impl;

import com.aulas.model.EstadoReserva;
import com.aulas.repo.IGenericRepo;
import com.aulas.repo.IEstadoReservaRepo;
import com.aulas.service.IEstadoReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EstadoReservaServiceImpl extends CRUDImpl<EstadoReserva, Integer> implements IEstadoReservaService {

    private final IEstadoReservaRepo repo;

    @Override
    protected IGenericRepo<EstadoReserva, Integer> getRepo() {
        return repo;
    }

}