package com.aulas.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioDTO {

    private Integer idUsuario;

    @NotNull
    @NotBlank
    @Size(min = 2, max = 100)
    private String nombreUsuario;

    @NotNull
    @NotBlank
    @Size(min = 2, max = 100)
    private String apellidoUsuario;

    @NotNull
    @NotBlank
    @Email
    @Size(max = 60)
    private String correoInstitucional;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 20)
    private String codigoInstitucional;

    // WRITE_ONLY = el cliente puede enviarlo (POST/PUT) pero nunca lo recibe (GET)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @NotBlank
    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    private String contrasena;

    @JsonIncludeProperties(value = {"idRol", "nombreRol"})
    @NotNull
    private RolDTO rol;

    @JsonIncludeProperties(value = {"idSede", "nombreSede"})
    @NotNull
    private SedeDTO sede;

    @JsonIncludeProperties(value = {"idCarrera", "nombreCarrera"})
    private CarreraDTO carrera;

    @JsonIncludeProperties(value = {"idUsuario"})
    private UsuarioDTO adminSedeCheck;
}