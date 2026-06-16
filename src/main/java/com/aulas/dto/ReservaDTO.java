package com.aulas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservaDTO {

    private Integer idReserva;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaReserva;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaSolicitud;

    @JsonIncludeProperties(value = {"idUsuario", "nombreUsuario", "apellidoUsuario"})
    @NotNull
    private UsuarioDTO usuario;

    @JsonIncludeProperties(value = {"idAula", "codigoAula"})
    @NotNull
    private AulaDTO aula;

    @JsonIncludeProperties(value = {"idSede", "nombreSede"})
    @NotNull
    private SedeDTO sede;

    @JsonIncludeProperties(value = {"idHorario", "horaInicio", "horaSalida"})
    @NotNull
    private HorarioDTO horario;

    @JsonIncludeProperties(value = {"idEstadoReserva", "descripcion"})
    @NotNull
    private EstadoReservaDTO estadoReserva;

    @JsonIncludeProperties(value = {"idTipoReserva", "descripcion"})
    @NotNull
    private TipoReservaDTO tipoReserva;
}