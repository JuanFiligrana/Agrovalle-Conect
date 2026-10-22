package co.edu.uniajc.agrovalle.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Credenciales recibidas para iniciar una sesión segura. */
public record LoginRequest(
    @NotBlank @Email String correo,
    @NotBlank String contrasena) {}
