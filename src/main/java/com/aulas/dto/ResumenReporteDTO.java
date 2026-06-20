package com.aulas.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResumenReporteDTO {

    private Long totalReservas;
    private Long reservasPendientes;
    private Long reservasAprobadas;
    private Long reservasRechazadas;
    private Long reservasCanceladas;
    private Long totalUsuarios;
    private Long totalAulas;
    private Long totalSedes;
    private Double tasaAprobacion;
    private Double tasaCancelacion;
}