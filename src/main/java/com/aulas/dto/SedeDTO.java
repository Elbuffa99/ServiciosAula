package com.aulas.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SedeDTO {

    private Integer idSede;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 100)
    private String nombreSede;

    @Size(max = 150)
    private String direccionSede;
}