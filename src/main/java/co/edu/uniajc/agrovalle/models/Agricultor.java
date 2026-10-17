package co.edu.uniajc.agrovalle.models;

/** Cuenta registrada en la plataforma. */
public record Agricultor(
    long id,
    String nombre,
    String correo,
    String cedula,
    String ubicacionValle,
    RolUsuario rol,
    String contrasenaHash) {}
