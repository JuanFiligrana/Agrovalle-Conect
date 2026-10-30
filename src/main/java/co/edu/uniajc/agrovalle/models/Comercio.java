package co.edu.uniajc.agrovalle.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/** Instantáneas inmutables del inventario, las reservas y los pedidos. */
public final class Comercio {
  private Comercio() {}

  /** Oferta física y precio actual, sin restar reservas. */
  public record Oferta(long id, long agricultorId, long productoId, String producto,
      BigDecimal cantidadFisicaKg, BigDecimal precioCopKg, boolean activa) {}

  /** Existencias físicas, reservadas y disponibles de una oferta. */
  public record Inventario(Oferta oferta, BigDecimal cantidadReservadaKg,
      BigDecimal cantidadDisponibleKg) {}

  /** Carrito perteneciente a un único comprador. */
  public record Carrito(long id, long compradorId, boolean abierto) {}

  /** Cantidad y precio reservados hasta su vencimiento. */
  public record Reserva(long id, long carritoId, long cosechaId, long agricultorId,
      long productoId, String producto, BigDecimal cantidadKg, BigDecimal precioCopKg,
      OffsetDateTime venceEn) {
    /** Redondea el importe de la reserva a centavos. */
    @com.fasterxml.jackson.annotation.JsonProperty("subtotal")
    public BigDecimal subtotal() {
      return cantidadKg.multiply(precioCopKg).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    /** Indica si la reserva aún retiene inventario. */
    public boolean vigente(OffsetDateTime ahora) {
      return venceEn.isAfter(ahora);
    }
  }

  /** Contenido del carrito y hora del servidor para evaluar vencimientos. */
  public record CarritoDetalle(long id, List<Reserva> items, BigDecimal total,
      OffsetDateTime fechaConsulta) {}

  /** Cabecera del pedido; la clave interna no se expone en respuestas. */
  public record Pedido(long id, long compradorId, long agricultorId, long carritoId,
      @JsonIgnore String clave, String estado, BigDecimal total, OffsetDateTime creadoEn,
      OffsetDateTime fechaProgramada, String ruta) {}

  /** Copia inmutable del producto, cantidad y precio confirmados. */
  public record Linea(long id, long pedidoId, long cosechaId, long productoId,
      String producto, BigDecimal cantidadKg, BigDecimal precioCopKg) {}

  /** Hito persistido del recorrido de un pedido. */
  public record Evento(long id, String estado, OffsetDateTime fecha, String descripcion) {}

  /** Pedido con sus líneas e historial cronológico. */
  public record PedidoDetalle(Pedido pedido, List<Linea> items, List<Evento> eventos) {}

  /** Resultado que distingue una creación de un reintento idempotente. */
  public record Confirmacion(PedidoDetalle orden, boolean creada) {}

  /** Notificación interna para un participante del pedido. */
  public record Aviso(long id, long pedidoId, String mensaje, OffsetDateTime fecha) {}

  /** Totales administrativos sin datos personales ni credenciales. */
  public record Resumen(long usuarios, long fincas, long ofertas, long pedidos,
      long pendientes, long entregados, long cancelados) {}
}
