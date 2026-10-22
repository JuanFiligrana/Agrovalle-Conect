package co.edu.uniajc.agrovalle.dto;

import co.edu.uniajc.agrovalle.validation.MunicipioValle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Datos de entrada para registrar una finca. */
public record FincaRequest(
    @NotBlank @Size(max = 120) String nombre,
    @NotBlank @MunicipioValle String municipio,
    @Size(max = 500) String descripcion) {}
