package co.edu.uniajc.agrovalle.models;

/** Finca asociada a un agricultor. */
public record Finca(
    long id,
    long agricultorId,
    String nombre,
    String municipio,
    String descripcion) {}
