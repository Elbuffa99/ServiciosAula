package com.aulas.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CupoAulaDTO {
    private Integer idAula;
    private String codigoAula;
    private Integer capacidad;
    private Long ocupado;
    private Long disponible;
    private boolean llena;
}