package com.aulas.service;

import com.aulas.dto.ReservaDTO;
import com.aulas.model.Reserva;

import java.time.LocalDate;
import java.util.List;

public interface IReservaService extends ICrud<Reserva, Integer>{


    List<ReservaDTO> findMisReservas(String correo) throws Exception;
    List<ReservaDTO> findFiltradas(LocalDate fecha, Integer idSede, String estado) throws Exception;
    ReservaDTO crearReserva(Reserva reserva) throws Exception;

    ReservaDTO aprobar(Integer idReserva) throws Exception;
    ReservaDTO rechazar(Integer idReserva) throws Exception;
    ReservaDTO cancelar(Integer idReserva, String correoUsuario) throws Exception;

}
