package co.edu.uniajc.agrovalle.models;

/** Identidad y estado de lectura de la notificación interna de un contacto. */
public record NotificacionContacto(
    long id, long contactoId, long destinatarioId, boolean leida) {}
