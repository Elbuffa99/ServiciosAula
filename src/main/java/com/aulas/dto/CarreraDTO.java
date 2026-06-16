package com.aulas.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CarreraDTO {

    private Integer idCarrera;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 200)
    private String nombreCarrera;

    @JsonIncludeProperties(value = {"idTipoAula"})
    private Set<TipoAulaDTO> tiposAula;
}