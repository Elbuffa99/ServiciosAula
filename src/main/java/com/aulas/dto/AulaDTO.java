package com.aulas.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AulaDTO {

    private Integer idAula;

    @NotNull
    @NotBlank
    @Size(min = 2, max = 10)
    private String codigoAula;

    @NotNull
    @Min(value = 1)
    private Integer capacidad;

    @JsonIncludeProperties(value = {"idTipoAula"})
    @NotNull
    private TipoAulaDTO tipoAula;

    @JsonIncludeProperties(value = {"idSede"})
    @NotNull
    private SedeDTO sede;
}