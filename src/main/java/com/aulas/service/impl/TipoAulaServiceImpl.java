package com.aulas.service.impl;

import com.aulas.model.TipoAula;
import com.aulas.repo.IGenericRepo;
import com.aulas.repo.ITipoAulaRepo;
import com.aulas.service.ITipoAulaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TipoAulaServiceImpl extends CRUDImpl<TipoAula, Integer> implements ITipoAulaService {

    private final ITipoAulaRepo repo;

    @Override
    protected IGenericRepo<TipoAula, Integer> getRepo() {
        return repo;
    }

}