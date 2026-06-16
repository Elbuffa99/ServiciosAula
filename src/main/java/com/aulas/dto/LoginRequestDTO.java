package com.aulas.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginRequestDTO {

    @NotNull
    @NotBlank
    @Email
    private String correoInstitucional;

    @NotNull
    @NotBlank
    private String contrasena;
}