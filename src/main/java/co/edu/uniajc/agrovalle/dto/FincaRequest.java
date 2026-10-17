package co.edu.uniajc.agrovalle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/** Datos de entrada para registrar una finca. */
public record FincaRequest(
    @Positive long agricultorId,
    @NotBlank String nombre,
    @NotBlank String municipio,
    String descripcion) {}
