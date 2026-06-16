package com.aulas.service.impl;

import com.aulas.model.Horario;
import com.aulas.repo.IHorarioRepo;
import com.aulas.repo.IGenericRepo;
import com.aulas.service.IHorarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HorarioServiceImpl extends CRUDImpl<Horario, Integer> implements IHorarioService {

    private final IHorarioRepo repo;

    @Override
    protected IGenericRepo<Horario, Integer> getRepo() {
        return repo;
    }

}