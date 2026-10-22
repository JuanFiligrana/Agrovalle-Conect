package co.edu.uniajc.agrovalle.models;

/** Solicitud de contacto entre un comprador y un agricultor. */
public record Contacto(
    long id,
    long compradorId,
    long productoId,
    String mensaje,
    String estado) {}
