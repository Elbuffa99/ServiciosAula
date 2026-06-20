package com.aulas.service;

import com.aulas.dto.ResumenReporteDTO;
import java.util.Map;

public interface IReporteService {
    Map<String, Long> topUsuariosQueReservan(int top) throws Exception;
    Map<String, Long> ocupacionPorHorario() throws Exception;
    Map<String, Long> sedesPorDemanda() throws Exception;
    Map<String, Long> aulasMasUsadas(int top) throws Exception;
    Map<String, Long> reservasPorEstado() throws Exception;
    Map<String, Long> reservasPorDiaSemana() throws Exception;
    ResumenReporteDTO resumenGeneral() throws Exception;
}
