package com.aulas.service.impl;

import com.aulas.model.TipoReserva;
import com.aulas.repo.IGenericRepo;
import com.aulas.repo.ITipoReservaRepo;
import com.aulas.service.ITipoReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TipoReservaServiceImpl extends CRUDImpl<TipoReserva, Integer> implements ITipoReservaService {

    private final ITipoReservaRepo repo;

    @Override
    protected IGenericRepo<TipoReserva, Integer> getRepo() {
        return repo;
    }

}