package co.edu.uniajc.agrovalle.controllers;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/** Respuestas de error uniformes para los endpoints del Sprint 1. */
@RestControllerAdvice
public class ApiExceptionHandler {

  /** Convierte errores de reglas de negocio en JSON con estado y mensaje. */
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, String>> handleStatus(ResponseStatusException exception) {
    return ResponseEntity.status(exception.getStatusCode())
        .body(error(exception.getStatusCode().toString(), exception.getReason()));
  }

  /** Convierte errores de validación de los DTO en una respuesta legible. */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidation(
      MethodArgumentNotValidException exception) {
    String message = exception.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .findFirst()
        .orElse("Los datos enviados no son válidos");
    return ResponseEntity.badRequest().body(error(HttpStatus.BAD_REQUEST.toString(), message));
  }

  private Map<String, String> error(String status, String message) {
    Map<String, String> body = new LinkedHashMap<>();
    body.put("status", status);
    body.put("message", message == null ? "Error de solicitud" : message);
    return body;
  }
}
