package com.aulas.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservaIntegranteDTO {

    private Integer idReservaIntegrante;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 20)
    private String codigoAlumno;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 100)
    private String nombreAlumno;

    @JsonIncludeProperties(value = {"idReserva"})
    @NotNull
    private ReservaDTO reserva;
}