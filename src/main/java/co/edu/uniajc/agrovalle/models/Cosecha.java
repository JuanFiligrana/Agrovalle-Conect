package co.edu.uniajc.agrovalle.models;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Oferta de una cosecha publicada por un agricultor. */
public record Cosecha(
    long id,
    long agricultorId,
    long fincaId,
    String tipo,
    String categoria,
    BigDecimal cantidadKg,
    BigDecimal precioCopKg,
    LocalDate fechaCosecha,
    boolean activa) {}
