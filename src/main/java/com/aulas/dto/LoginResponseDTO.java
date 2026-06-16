package com.aulas.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponseDTO {

    private String token;
    private String tipo;            // "Bearer"
    private Long expiresIn;         // milisegundos

    // Info básica del usuario (sin password, sin relaciones pesadas)
    private Integer idUsuario;
    private String correoInstitucional;
    private String nombreUsuario;
    private String apellidoUsuario;
    private String rol;             // nombreRol, plano
}