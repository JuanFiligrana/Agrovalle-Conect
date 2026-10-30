package co.edu.uniajc.agrovalle.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Contratos de entrada; nunca aceptan el comprador o agricultor desde el cliente. */
public final class ComercioRequest {
  private ComercioRequest() {}

  /** Cambios admitidos de precio e inventario físico. */
  public record ActualizarOferta(
      @NotNull @DecimalMin("0") @Digits(integer = 11, fraction = 3)
      @JsonAlias("cantidad_fisica_kg") BigDecimal cantidadFisicaKg,
      @NotNull @DecimalMin("0.01") @Digits(integer = 12, fraction = 2)
      @JsonAlias("precio_cop_kg") BigDecimal precioCopKg) {}

  /** Cantidad total deseada para una oferta del carrito. */
  public record Reservar(
      @Positive @JsonAlias({"id_producto", "idProducto"}) long cosechaId,
      @NotNull @DecimalMin("0.001") @Digits(integer = 11, fraction = 3)
      @JsonAlias("cantidad_kg") BigDecimal cantidadKg) {}

  /** Identificador del carrito que se desea confirmar. */
  public record Confirmar(@Positive @JsonAlias("carrito_id") long carritoId) {}

  /** Transición solicitada y programación del despacho. */
  public record CambiarEstado(@NotBlank @Size(max = 30) String estado,
      @JsonAlias("fecha_programada") OffsetDateTime fechaProgramada,
      @Size(max = 500) String ruta) {}
}
