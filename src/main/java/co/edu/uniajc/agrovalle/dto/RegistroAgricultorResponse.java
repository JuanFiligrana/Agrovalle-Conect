package co.edu.uniajc.agrovalle.dto;

import co.edu.uniajc.agrovalle.models.RolUsuario;

/** Respuesta segura de un registro, sin incluir la contraseña. */
public record RegistroAgricultorResponse(
    long id,
    String nombre,
    String correo,
    String cedula,
    String ubicacionValle,
    RolUsuario rol) {}
