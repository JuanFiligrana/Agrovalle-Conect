package co.edu.uniajc.agrovalle.dto;

import co.edu.uniajc.agrovalle.models.RolUsuario;

/** Identidad pública obtenida de un token firmado. */
public record UsuarioAutenticadoResponse(
    long id,
    String correo,
    RolUsuario rol) {}
