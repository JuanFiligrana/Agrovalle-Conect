package co.edu.uniajc.agrovalle.models;

import java.time.OffsetDateTime;

/** Vista privada de una solicitud, sin documentos, correos ni credenciales. */
public record MensajeContacto(
    long id,
    long productoId,
    String producto,
    String comprador,
    String agricultor,
    String mensaje,
    OffsetDateTime fechaCreacion,
    long notificacionId,
    boolean leida) {}
