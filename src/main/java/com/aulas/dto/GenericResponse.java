package com.aulas.dto;

import java.util.List;

public record GenericResponse<T>(
        int code,
        String type,    // "success" | "error"
        List<T> body
) {}