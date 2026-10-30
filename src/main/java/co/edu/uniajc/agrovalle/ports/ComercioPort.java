package co.edu.uniajc.agrovalle.ports;

import co.edu.uniajc.agrovalle.models.Comercio;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/** Persistencia transaccional; los bloqueos se mantienen hasta finalizar el caso de uso. */
public interface ComercioPort {
  /** Consulta el rol vigente y opcionalmente bloquea la cuenta. */
  String rol(long usuarioId, boolean bloquear);

  /** Busca una oferta y opcionalmente bloquea su inventario. */
  Optional<Comercio.Oferta> oferta(long id, boolean bloquear);

  /** Lista las ofertas de un agricultor. */
  List<Comercio.Oferta> ofertas(long agricultorId);

  /** Suma únicamente reservas activas cuyo plazo no ha vencido. */
  BigDecimal reservada(long cosechaId, OffsetDateTime ahora);

  /** Actualiza existencias físicas y precio bajo el bloqueo del caso de uso. */
  void actualizarOferta(long id, BigDecimal cantidad, BigDecimal precio);

  /** Obtiene o crea el carrito abierto después de bloquear al comprador. */
  Comercio.Carrito carritoActual(long compradorId);

  /** Busca el propietario y estado del carrito. */
  Optional<Comercio.Carrito> carrito(long id);

  /** Lista las reservas activas, incluidas las vencidas, ordenadas por oferta. */
  List<Comercio.Reserva> reservas(long carritoId);

  /** Guarda o reemplaza una reserva única del carrito. */
  void reservar(long carritoId, long cosechaId, BigDecimal cantidad, BigDecimal precio,
      OffsetDateTime venceEn);

  /** Desactiva una reserva sin alterar existencias físicas. */
  void quitarReserva(long carritoId, long cosechaId);

  /** Cierra el carrito y consume sus reservas en la misma transacción. */
  void cerrarCarrito(long carritoId);

  /** Encuentra un pedido por comprador y clave idempotente. */
  Optional<Comercio.Pedido> porClave(long compradorId, String clave);

  /** Busca un pedido y opcionalmente bloquea sus transiciones. */
  Optional<Comercio.Pedido> pedido(long id, boolean bloquear);

  /** Lista los pedidos privados del comprador o agricultor. */
  List<Comercio.Pedido> pedidos(long usuarioId, boolean agricultor);

  /** Persiste una cabecera pendiente con clave única por comprador. */
  long crearPedido(long compradorId, long agricultorId, long carritoId, String clave,
      BigDecimal total, OffsetDateTime ahora);

  /** Copia cantidad y precio reservado en una línea inmutable. */
  void agregarLinea(long pedidoId, Comercio.Reserva reserva);

  /** Consulta líneas ordenadas por oferta para mantener el orden de bloqueo. */
  List<Comercio.Linea> lineas(long pedidoId);

  /** Persiste estado, programación y ruta del pedido. */
  void estado(long pedidoId, String estado, OffsetDateTime programada, String ruta);

  /** Añade un evento de trazabilidad dentro de la transacción. */
  void evento(long pedidoId, String estado, OffsetDateTime fecha, String descripcion);

  /** Recupera los eventos en orden cronológico estable. */
  List<Comercio.Evento> eventos(long pedidoId);

  /** Persiste un aviso interno para el destinatario indicado. */
  void avisar(long pedidoId, long destinatarioId, String mensaje, OffsetDateTime fecha);

  /** Consulta avisos privados en orden reciente. */
  List<Comercio.Aviso> avisos(long destinatarioId);

  /** Registra una venta completada única por línea entregada. */
  void finalizarVenta(Comercio.Linea linea, OffsetDateTime fecha);

  /** Agrega indicadores sin exponer datos personales. */
  Comercio.Resumen resumen();
}
