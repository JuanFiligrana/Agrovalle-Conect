package co.edu.uniajc.agrovalle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Datos de entrada para solicitar contacto con un agricultor. */
public record ContactoRequest(
    @Positive long compradorId,
    @Positive long productoId,
    @NotBlank @Size(max = 1000) String mensaje) {}
