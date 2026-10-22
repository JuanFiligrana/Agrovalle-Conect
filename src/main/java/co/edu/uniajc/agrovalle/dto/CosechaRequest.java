package co.edu.uniajc.agrovalle.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Datos de entrada para publicar una cosecha. */
public record CosechaRequest(
    @JsonAlias("finca_id") @Positive long fincaId,
    @NotBlank String tipo,
    @NotBlank String categoria,
    @JsonAlias("cantidad_kg") @NotNull @DecimalMin("0.01") BigDecimal cantidadKg,
    @JsonAlias("precio_cop_kg") @NotNull @DecimalMin("0.01") BigDecimal precioCopKg,
    @JsonAlias("fecha_cosecha") @NotNull LocalDate fechaCosecha) {}
