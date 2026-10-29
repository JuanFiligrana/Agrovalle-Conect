package co.edu.uniajc.agrovalle.controllers;

import co.edu.uniajc.agrovalle.exceptions.OperacionNoPermitidaException;
import co.edu.uniajc.agrovalle.exceptions.RecursoNoEncontradoException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/** Respuestas de error uniformes para los endpoints del Sprint 1. */
@RestControllerAdvice
public class ApiExceptionHandler {

  /** Traduce un recurso ausente sin trasladar HTTP a los servicios refactorizados. */
  @ExceptionHandler(RecursoNoEncontradoException.class)
  public ResponseEntity<Map<String, String>> handleNotFound(
      RecursoNoEncontradoException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(error(HttpStatus.NOT_FOUND.toString(), exception.getMessage()));
  }

  /** Traduce restricciones de rol o propiedad manteniendo el JSON existente. */
  @ExceptionHandler(OperacionNoPermitidaException.class)
  public ResponseEntity<Map<String, String>> handleForbidden(
      OperacionNoPermitidaException exception) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(error(HttpStatus.FORBIDDEN.toString(), exception.getMessage()));
  }

  /** Convierte errores de reglas de negocio en JSON con estado y mensaje. */
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, String>> handleStatus(ResponseStatusException exception) {
    return ResponseEntity.status(exception.getStatusCode())
        .body(error(exception.getStatusCode().toString(), exception.getReason()));
  }

  /** Convierte errores de validación de los DTO en una respuesta legible. */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(
      MethodArgumentNotValidException exception) {
    Map<String, String> fields = new LinkedHashMap<>();
    exception.getBindingResult().getFieldErrors().forEach(fieldError ->
        fields.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage()));
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("status", HttpStatus.BAD_REQUEST.toString());
    body.put("message", "Los datos enviados no son válidos");
    body.put("errors", fields);
    return ResponseEntity.badRequest().body(body);
  }

  /** Informa un conflicto de integridad sin revelar SQL ni atribuirlo a otra operación. */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, String>> handleDataIntegrity(
      DataIntegrityViolationException exception) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(error(HttpStatus.CONFLICT.toString(),
            "La operación entra en conflicto con los datos registrados"));
  }

  private Map<String, String> error(String status, String message) {
    Map<String, String> body = new LinkedHashMap<>();
    body.put("status", status);
    body.put("message", message == null ? "Error de solicitud" : message);
    return body;
  }
}
