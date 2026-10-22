package co.edu.uniajc.agrovalle.dto;

import java.math.BigDecimal;

/** Resultado del cálculo de flete para un municipio. */
public record FleteResponse(
    String municipio,
    String estrategia,
    BigDecimal valorCop) {}
