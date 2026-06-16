package com.aulas.exception;

import com.aulas.dto.GenericResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalErrorHandler extends ResponseEntityExceptionHandler {

    // ===== 500: catch-all =====
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse<CustomErrorResponse>> handleDefault(Exception ex, WebRequest req) {
        ex.printStackTrace();
        CustomErrorResponse error = new CustomErrorResponse(
                LocalDateTime.now(), ex.getMessage(), req.getDescription(false));
        return new ResponseEntity<>(
                new GenericResponse<>(500, "error", List.of(error)),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ===== 404: recurso no encontrado =====
    @ExceptionHandler(ModelNotFoundException.class)
    public ResponseEntity<GenericResponse<CustomErrorResponse>> handleNotFound(ModelNotFoundException ex, WebRequest req) {
        CustomErrorResponse error = new CustomErrorResponse(
                LocalDateTime.now(), ex.getMessage(), req.getDescription(false));
        return new ResponseEntity<>(
                new GenericResponse<>(404, "error", List.of(error)),
                HttpStatus.NOT_FOUND);
    }

    // ===== 400: regla de negocio violada =====
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<GenericResponse<CustomErrorResponse>> handleBusiness(BusinessException ex, WebRequest req) {
        CustomErrorResponse error = new CustomErrorResponse(
                LocalDateTime.now(), ex.getMessage(), req.getDescription(false));
        return new ResponseEntity<>(
                new GenericResponse<>(400, "error", List.of(error)),
                HttpStatus.BAD_REQUEST);
    }

    // ===== 401: login fallido =====
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<GenericResponse<CustomErrorResponse>> handleBadCredentials(BadCredentialsException ex, WebRequest req) {
        CustomErrorResponse error = new CustomErrorResponse(
                LocalDateTime.now(), "Credenciales inválidas", req.getDescription(false));
        return new ResponseEntity<>(
                new GenericResponse<>(401, "error", List.of(error)),
                HttpStatus.UNAUTHORIZED);
    }

    // ===== 403: usuario autenticado sin permisos =====
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GenericResponse<CustomErrorResponse>> handleAccessDenied(AccessDeniedException ex, WebRequest req) {
        CustomErrorResponse error = new CustomErrorResponse(
                LocalDateTime.now(), "No tiene permisos para acceder a este recurso", req.getDescription(false));
        return new ResponseEntity<>(
                new GenericResponse<>(403, "error", List.of(error)),
                HttpStatus.FORBIDDEN);
    }

    // ===== 400: validación @Valid en DTOs falló =====
    // Sobrescribimos el metodo del padre para devolver nuestro formato
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest req) {

        // Convertimos cada error de campo en un CustomErrorResponse
        List<CustomErrorResponse> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new CustomErrorResponse(
                        LocalDateTime.now(),
                        fe.getField() + ": " + fe.getDefaultMessage(),
                        req.getDescription(false)))
                .collect(Collectors.toList());

        return new ResponseEntity<>(
                new GenericResponse<>(400, "error", errors),
                HttpStatus.BAD_REQUEST);
    }
}