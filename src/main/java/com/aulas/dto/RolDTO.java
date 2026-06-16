package com.aulas.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RolDTO {

    private Integer idRol;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 50)
    private String nombreRol;
}