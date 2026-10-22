package co.edu.uniajc.agrovalle.dto;

/** Respuesta de una solicitud de contacto registrada. */
public record ContactoResponse(long id, long productoId, String estado) {}
