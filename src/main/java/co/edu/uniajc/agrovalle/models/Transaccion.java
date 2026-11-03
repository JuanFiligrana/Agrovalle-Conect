package co.edu.uniajc.agrovalle.models;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Entidad histórica: precio pactado de una línea de venta, independiente de la oferta actual. */
public record Transaccion(long id, long productoId, String producto, BigDecimal precioUnitarioCopKg,
    String estado, OffsetDateTime fechaFinalizacion) {}
