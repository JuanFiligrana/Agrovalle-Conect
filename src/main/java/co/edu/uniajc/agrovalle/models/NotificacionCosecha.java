package co.edu.uniajc.agrovalle.models;

/** Aviso creado por el observador cuando se publica una nueva cosecha. */
public record NotificacionCosecha(
    long id,
    long agricultorId,
    long cosechaId,
    String mensaje) {}
