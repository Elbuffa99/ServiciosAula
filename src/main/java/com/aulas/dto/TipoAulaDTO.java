package com.aulas.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TipoAulaDTO {

    private Integer idTipoAula;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 200)
    private String descripcion;
}