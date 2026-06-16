package com.aulas.service;

import com.aulas.dto.ReservaDTO;
import com.aulas.model.Reserva;

import java.util.List;

public interface IReservaService extends ICrud<Reserva, Integer>{

    // IReservaService
    List<ReservaDTO> findMisReservas(String correo) throws Exception;
}
