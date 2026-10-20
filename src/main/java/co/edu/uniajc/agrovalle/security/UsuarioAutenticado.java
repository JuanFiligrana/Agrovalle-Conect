package co.edu.uniajc.agrovalle.security;

import co.edu.uniajc.agrovalle.models.RolUsuario;

/** Identidad verificada que Spring Security conserva durante una solicitud. */
public record UsuarioAutenticado(
    long id,
    String correo,
    RolUsuario rol) {}
