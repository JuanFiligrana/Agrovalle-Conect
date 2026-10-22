package co.edu.uniajc.agrovalle.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Datos de entrada para publicar una cosecha. */
public record CosechaRequest(
    @Positive long agricultorId,
    @Positive long fincaId,
    @NotBlank String tipo,
    @NotBlank String categoria,
    @NotNull @DecimalMin("0.01") BigDecimal cantidadKg,
    @NotNull @DecimalMin("0.01") BigDecimal precioCopKg,
    @NotNull @FutureOrPresent LocalDate fechaCosecha) {}
