package com.aulas.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EstadoReservaDTO {

    private Integer idEstadoReserva;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 30)
    private String descripcion;
}