package co.edu.uniajc.agrovalle.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Datos de entrada para registrar una cuenta. */
public record RegistroAgricultorRequest(
    @NotBlank String nombre,
    @NotBlank @Email String correo,
    @NotBlank @Size(min = 6) String contrasena,
    @NotBlank String cedula,
    @NotBlank String ubicacionValle,
    @NotBlank String rol) {}
