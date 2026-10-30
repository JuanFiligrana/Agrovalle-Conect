package co.edu.uniajc.agrovalle.ports;

import co.edu.uniajc.agrovalle.models.Comercio;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/** Persistencia transaccional; los bloqueos se mantienen hasta finalizar el caso de uso. */
public interface ComercioPort {
  String rol(long usuarioId, boolean bloquear);

  Optional<Comercio.Oferta> oferta(long id, boolean bloquear);

  List<Comercio.Oferta> ofertas(long agricultorId);

  BigDecimal reservada(long cosechaId, OffsetDateTime ahora);

  void actualizarOferta(long id, BigDecimal cantidad, BigDecimal precio);

  Comercio.Carrito carritoActual(long compradorId);

  Optional<Comercio.Carrito> carrito(long id);

  List<Comercio.Reserva> reservas(long carritoId);

  void reservar(long carritoId, long cosechaId, BigDecimal cantidad, BigDecimal precio,
      OffsetDateTime venceEn);

  void quitarReserva(long carritoId, long cosechaId);

  void cerrarCarrito(long carritoId);

  Optional<Comercio.Pedido> porClave(long compradorId, String clave);

  Optional<Comercio.Pedido> pedido(long id, boolean bloquear);

  List<Comercio.Pedido> pedidos(long usuarioId, boolean agricultor);

  long crearPedido(long compradorId, long agricultorId, long carritoId, String clave,
      BigDecimal total, OffsetDateTime ahora);

  void agregarLinea(long pedidoId, Comercio.Reserva reserva);

  List<Comercio.Linea> lineas(long pedidoId);

  void estado(long pedidoId, String estado, OffsetDateTime programada, String ruta);

  void evento(long pedidoId, String estado, OffsetDateTime fecha, String descripcion);

  List<Comercio.Evento> eventos(long pedidoId);

  void avisar(long pedidoId, long destinatarioId, String mensaje, OffsetDateTime fecha);

  List<Comercio.Aviso> avisos(long destinatarioId);

  void finalizarVenta(Comercio.Linea linea, OffsetDateTime fecha);

  Comercio.Resumen resumen();
}
