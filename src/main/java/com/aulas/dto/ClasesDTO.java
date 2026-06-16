package com.aulas.dto;

import com.aulas.model.DiaSemana;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClasesDTO {

    private Integer idClases;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 100)
    private String nombreCurso;

    @NotNull
    private DiaSemana diaSemana;

    @JsonIncludeProperties(value = {"idAula"})
    @NotNull
    private AulaDTO aula;

    @JsonIncludeProperties(value = {"idHorario"})
    @NotNull
    private HorarioDTO horario;
}