package co.edu.uniajc.agrovalle.dto;

/** Token emitido después de validar las credenciales. */
public record LoginResponse(
    String accessToken,
    String tokenType,
    long expiresInSeconds) {}
