package co.edu.uniajc.agrovalle.dto;

import java.math.BigDecimal;

/** Resultado de la consulta de precios regionales. */
public record PrecioPromedioResponse(
    String producto,
    BigDecimal promedio,
    long cantidadTransacciones,
    String unidad) {}
