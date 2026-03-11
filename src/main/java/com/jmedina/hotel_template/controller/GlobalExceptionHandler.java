package com.jmedina.hotel_template.controller;

import com.jmedina.hotel_template.dto.response.ApiResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice // intercepta excepciones de todos los controllers
public class GlobalExceptionHandler {

    // Errores de validación (@Valid falló)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleValidation(
            MethodArgumentNotValidException ex) {

        String errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponseDTO.error("Errores de validación: " + errors));
    }

    // Entidad no encontrada
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponseDTO.error(ex.getMessage()));
    }

    // Argumento inválido (ej: email duplicado)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleIllegalArgument(
            IllegalArgumentException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponseDTO.error(ex.getMessage()));
    }

    // Estado inválido (ej: no hay habitaciones disponibles)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiResponseDTO.error(ex.getMessage()));
    }

    // Credenciales incorrectas en login
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleBadCredentials(
            BadCredentialsException ex) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponseDTO.error("Email o contraseña incorrectos"));
    }

    // Acceso denegado — rol insuficiente
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiResponseDTO.error("No tienes permiso para realizar esta acción"));
    }

    // Cualquier otro error no controlado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleGeneral(Exception ex) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponseDTO.error("Error interno del servidor: " + ex.getMessage()));
    }
}