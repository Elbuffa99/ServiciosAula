package com.aulas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IncidenciaDTO {

    private Integer idIncidencia;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 100)
    private String asunto;

    @NotNull
    @NotBlank
    @Size(min = 5, max = 1000)
    private String descripcion;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaReporte;

    @JsonIncludeProperties(value = {"idUsuario", "nombreUsuario"})
    @NotNull
    private UsuarioDTO usuario;

    @JsonIncludeProperties(value = {"idAula", "codigoAula"})
    @NotNull
    private AulaDTO aula;
}