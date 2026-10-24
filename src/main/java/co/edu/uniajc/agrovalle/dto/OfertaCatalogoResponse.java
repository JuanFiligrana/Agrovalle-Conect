package co.edu.uniajc.agrovalle.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Datos públicos de una cosecha disponibles para construir el catálogo web. */
public record OfertaCatalogoResponse(
    long id,
    long agricultorId,
    String agricultorNombre,
    long fincaId,
    String fincaNombre,
    String municipio,
    String tipo,
    String categoria,
    BigDecimal cantidadKg,
    BigDecimal precioCopKg,
    LocalDate fechaCosecha,
    boolean activa) {}
