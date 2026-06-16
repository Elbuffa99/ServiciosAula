package com.aulas.service.impl;

import com.aulas.model.ReporteSemanal;
import com.aulas.repo.IGenericRepo;
import com.aulas.repo.IReporteSemanalRepo;
import com.aulas.service.IReporteSemanalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReporteSemanalServiceImpl extends CRUDImpl<ReporteSemanal, Integer> implements IReporteSemanalService {

    private final IReporteSemanalRepo repo;

    @Override
    protected IGenericRepo<ReporteSemanal, Integer> getRepo() {
        return repo;
    }

}