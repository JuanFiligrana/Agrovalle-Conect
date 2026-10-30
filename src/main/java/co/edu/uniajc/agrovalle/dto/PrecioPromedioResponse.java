package co.edu.uniajc.agrovalle.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

/** Resultado de la consulta de precios regionales. */
public record PrecioPromedioResponse(
    String producto,
    BigDecimal promedio,
    long cantidadTransacciones,
    String unidad) {
  /** Alias del contrato del backlog; mantiene compatibilidad con clientes camelCase. */
  @JsonProperty("cantidad_transacciones")
  public long cantidadTransaccionesContrato() {
    return cantidadTransacciones;
  }
}
