package com.aulas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HorarioDTO {

    private Integer idHorario;

    @NotNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaInicio;

    @NotNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaSalida;

    @Size(max = 30)
    private String turno;
}