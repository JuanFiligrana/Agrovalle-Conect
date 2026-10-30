package co.edu.uniajc.agrovalle.services;

import co.edu.uniajc.agrovalle.dto.ComercioRequest;
import co.edu.uniajc.agrovalle.models.Comercio;
import co.edu.uniajc.agrovalle.ports.ComercioPort;
import co.edu.uniajc.agrovalle.security.UsuarioAutenticado;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** Casos de uso atómicos de compra y logística; nunca confía en identidades del cuerpo. */
@Service
@Transactional
public class ComercioService {
  private final ComercioPort datos;
  private final Clock reloj;

  /** Recibe la persistencia y el reloj compartido del negocio. */
  public ComercioService(ComercioPort datos, Clock reloj) {
    this.datos = datos;
    this.reloj = reloj;
  }

  /** Consulta el inventario del agricultor autenticado. */
  public List<Comercio.Inventario> misOfertas(UsuarioAutenticado usuario) {
    exigirRol(usuario, "AGRICULTOR", false);
    return datos.ofertas(usuario.id()).stream().map(this::inventario).toList();
  }

  /** Actualiza una oferta propia sin comprometer las reservas vigentes. */
  public Comercio.Inventario actualizar(long id, ComercioRequest.ActualizarOferta solicitud,
      UsuarioAutenticado usuario) {
    exigirRol(usuario, "AGRICULTOR", false);
    var oferta = oferta(id, true);
    exigirPropietario(oferta.agricultorId(), usuario.id());
    BigDecimal reservada = datos.reservada(id, ahora());
    if (solicitud.cantidadFisicaKg().compareTo(reservada) < 0) {
      throw conflicto("La cantidad física no puede ser menor que las reservas vigentes");
    }
    datos.actualizarOferta(id, solicitud.cantidadFisicaKg(), solicitud.precioCopKg());
    return inventario(oferta(id, false));
  }

  /** Consulta o inicia el carrito privado del comprador. */
  public Comercio.CarritoDetalle carrito(UsuarioAutenticado usuario) {
    exigirRol(usuario, "COMPRADOR", true);
    return detalleCarrito(datos.carritoActual(usuario.id()).id());
  }

  /** Reemplaza la cantidad reservada y renueva su plazo de quince minutos. */
  public Comercio.CarritoDetalle reservar(ComercioRequest.Reservar solicitud,
      UsuarioAutenticado usuario) {
    exigirRol(usuario, "COMPRADOR", true);
    final var carrito = datos.carritoActual(usuario.id());
    final var oferta = oferta(solicitud.cosechaId(), true);
    final var ahora = ahora();
    List<Comercio.Reserva> reservas = datos.reservas(carrito.id());
    if (reservas.stream().anyMatch(item -> item.agricultorId() != oferta.agricultorId())) {
      throw conflicto("El carrito solo puede contener productos de un agricultor");
    }
    if (!oferta.activa()) {
      throw conflicto("La oferta ya no está activa");
    }
    BigDecimal anterior = reservas.stream()
        .filter(item -> item.cosechaId() == oferta.id() && item.vigente(ahora))
        .map(Comercio.Reserva::cantidadKg).reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal disponible = oferta.cantidadFisicaKg()
        .subtract(datos.reservada(oferta.id(), ahora)).add(anterior);
    if (solicitud.cantidadKg().compareTo(disponible) > 0) {
      throw conflicto("No hay inventario suficiente para esta reserva");
    }
    datos.reservar(carrito.id(), oferta.id(), solicitud.cantidadKg(), oferta.precioCopKg(),
        ahora.plusMinutes(15));
    return detalleCarrito(carrito.id());
  }

  /** Retira una reserva del carrito propio y libera su disponibilidad. */
  public Comercio.CarritoDetalle quitar(long cosechaId, UsuarioAutenticado usuario) {
    exigirRol(usuario, "COMPRADOR", true);
    final var carrito = datos.carritoActual(usuario.id());
    oferta(cosechaId, true);
    datos.quitarReserva(carrito.id(), cosechaId);
    return detalleCarrito(carrito.id());
  }

  /** Confirma el carrito atómicamente y reconoce reintentos por clave. */
  public Comercio.Confirmacion confirmar(long carritoId, String clave,
      UsuarioAutenticado usuario) {
    exigirRol(usuario, "COMPRADOR", true);
    if (clave == null || !clave.matches("[A-Za-z0-9._:-]{8,128}")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Idempotency-Key debe contener entre 8 y 128 letras, números, puntos, guiones o dos puntos");
    }
    var anterior = datos.porClave(usuario.id(), clave);
    if (anterior.isPresent()) {
      if (anterior.get().carritoId() != carritoId) {
        throw conflicto("La clave ya se utilizó con otro carrito");
      }
      return new Comercio.Confirmacion(detalle(anterior.get()), false);
    }
    var carrito = datos.carrito(carritoId).orElseThrow(() -> noExiste("carrito"));
    exigirPropietario(carrito.compradorId(), usuario.id());
    if (!carrito.abierto()) {
      throw conflicto("El carrito ya fue confirmado");
    }
    final List<Comercio.Reserva> reservas = datos.reservas(carritoId);
    if (reservas.isEmpty()) {
      throw conflicto("El carrito está vacío");
    }
    final Map<Long, Comercio.Oferta> bloqueadas = bloquearOfertas(reservas);
    final var ahora = ahora();
    long agricultor = reservas.get(0).agricultorId();
    for (var item : reservas) {
      var oferta = bloqueadas.get(item.cosechaId());
      if (!item.vigente(ahora) || !oferta.activa()
          || item.agricultorId() != agricultor
          || oferta.cantidadFisicaKg().compareTo(datos.reservada(oferta.id(), ahora)) < 0) {
        throw conflicto("La reserva venció o cambió el inventario; actualiza el carrito");
      }
    }
    BigDecimal total = reservas.stream().map(Comercio.Reserva::subtotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    long pedidoId = datos.crearPedido(usuario.id(), agricultor, carritoId, clave, total, ahora);
    for (var item : reservas) {
      var oferta = bloqueadas.get(item.cosechaId());
      datos.actualizarOferta(oferta.id(), oferta.cantidadFisicaKg().subtract(item.cantidadKg()),
          oferta.precioCopKg());
      datos.agregarLinea(pedidoId, item);
    }
    datos.cerrarCarrito(carritoId);
    datos.evento(pedidoId, "PENDIENTE", ahora, "Pedido confirmado; inventario descontado");
    datos.avisar(pedidoId, agricultor, "Tienes un nuevo pedido pendiente", ahora);
    return new Comercio.Confirmacion(detalle(pedido(pedidoId, false)), true);
  }

  /** Lista exclusivamente los pedidos en los que participa la cuenta. */
  public List<Comercio.Pedido> pedidos(UsuarioAutenticado usuario) {
    String rol = exigirParticipante(usuario);
    return datos.pedidos(usuario.id(), "AGRICULTOR".equals(rol));
  }

  /** Consulta las líneas y eventos de un pedido propio. */
  public Comercio.PedidoDetalle trazabilidad(long id, UsuarioAutenticado usuario) {
    exigirParticipante(usuario);
    var pedido = pedido(id, false);
    if (pedido.compradorId() != usuario.id() && pedido.agricultorId() != usuario.id()) {
      throw prohibido();
    }
    return detalle(pedido);
  }

  /** Cancela un pedido pendiente y devuelve existencias una sola vez. */
  public Comercio.PedidoDetalle cancelar(long id, UsuarioAutenticado usuario) {
    exigirRol(usuario, "COMPRADOR", true);
    final var pedido = pedido(id, true);
    exigirPropietario(pedido.compradorId(), usuario.id());
    if ("CANCELADA".equals(pedido.estado())) {
      return detalle(pedido);
    }
    if (!"PENDIENTE".equals(pedido.estado())) {
      throw conflicto("Solo se puede cancelar un pedido pendiente de alistamiento");
    }
    for (var linea : datos.lineas(id)) {
      var oferta = oferta(linea.cosechaId(), true);
      datos.actualizarOferta(oferta.id(), oferta.cantidadFisicaKg().add(linea.cantidadKg()),
          oferta.precioCopKg());
    }
    var ahora = ahora();
    datos.estado(id, "CANCELADA", null, null);
    datos.evento(id, "CANCELADA", ahora, "Cancelado por el comprador; inventario devuelto");
    datos.avisar(id, pedido.agricultorId(), "El comprador canceló el pedido", ahora);
    return detalle(pedido(id, false));
  }

  /** Avanza el despacho y registra eventos, avisos e historial de venta. */
  public Comercio.PedidoDetalle cambiarEstado(long id, ComercioRequest.CambiarEstado solicitud,
      UsuarioAutenticado usuario) {
    exigirRol(usuario, "AGRICULTOR", false);
    final var pedido = pedido(id, true);
    exigirPropietario(pedido.agricultorId(), usuario.id());
    String siguiente = switch (pedido.estado()) {
      case "PENDIENTE" -> "LISTO_PARA_DESPACHO";
      case "LISTO_PARA_DESPACHO" -> "EN_CAMINO";
      case "EN_CAMINO" -> "ENTREGADO";
      default -> "";
    };
    if (!siguiente.equals(solicitud.estado()) || siguiente.isEmpty()) {
      throw conflicto("La transición de estado solicitada no está permitida");
    }
    final var ahora = ahora();
    OffsetDateTime programada = pedido.fechaProgramada();
    String ruta = pedido.ruta();
    if ("LISTO_PARA_DESPACHO".equals(siguiente)) {
      if (solicitud.fechaProgramada() == null || !solicitud.fechaProgramada().isAfter(ahora)
          || solicitud.ruta() == null || solicitud.ruta().isBlank()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Indica una fecha futura con zona horaria y una ruta de entrega");
      }
      programada = solicitud.fechaProgramada();
      ruta = solicitud.ruta().trim();
    }
    datos.estado(id, siguiente, programada, ruta);
    datos.evento(id, siguiente, ahora, "Estado actualizado por el agricultor");
    datos.avisar(id, pedido.compradorId(), "Tu pedido cambió a " + siguiente, ahora);
    if ("ENTREGADO".equals(siguiente)) {
      for (var linea : datos.lineas(id)) {
        datos.finalizarVenta(linea, ahora);
      }
    }
    return detalle(pedido(id, false));
  }

  /** Consulta las notificaciones privadas de pedidos. */
  public List<Comercio.Aviso> avisos(UsuarioAutenticado usuario) {
    exigirParticipante(usuario);
    return datos.avisos(usuario.id());
  }

  /** Consulta indicadores exclusivamente con el rol administrativo vigente. */
  public Comercio.Resumen resumen(UsuarioAutenticado usuario) {
    exigirRol(usuario, "ADMINISTRADOR", false);
    return datos.resumen();
  }

  private Map<Long, Comercio.Oferta> bloquearOfertas(List<Comercio.Reserva> reservas) {
    Map<Long, Comercio.Oferta> resultado = new LinkedHashMap<>();
    reservas.stream().map(Comercio.Reserva::cosechaId).distinct().sorted()
        .forEach(id -> resultado.put(id, oferta(id, true)));
    return resultado;
  }

  private Comercio.CarritoDetalle detalleCarrito(long id) {
    List<Comercio.Reserva> items = datos.reservas(id);
    return new Comercio.CarritoDetalle(id, items,
        items.stream().map(Comercio.Reserva::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add),
        ahora());
  }

  private Comercio.PedidoDetalle detalle(Comercio.Pedido pedido) {
    return new Comercio.PedidoDetalle(pedido, datos.lineas(pedido.id()),
        datos.eventos(pedido.id()));
  }

  private Comercio.Inventario inventario(Comercio.Oferta oferta) {
    BigDecimal reservada = datos.reservada(oferta.id(), ahora());
    return new Comercio.Inventario(oferta, reservada, oferta.cantidadFisicaKg().subtract(reservada));
  }

  private Comercio.Oferta oferta(long id, boolean bloquear) {
    return datos.oferta(id, bloquear).orElseThrow(() -> noExiste("oferta"));
  }

  private Comercio.Pedido pedido(long id, boolean bloquear) {
    return datos.pedido(id, bloquear).orElseThrow(() -> noExiste("pedido"));
  }

  private void exigirRol(UsuarioAutenticado usuario, String rol, boolean bloquear) {
    if (usuario == null || !rol.equals(datos.rol(usuario.id(), bloquear))) {
      throw prohibido();
    }
  }

  private String exigirParticipante(UsuarioAutenticado usuario) {
    String rol = usuario == null ? "" : datos.rol(usuario.id(), false);
    if (!"COMPRADOR".equals(rol) && !"AGRICULTOR".equals(rol)) {
      throw prohibido();
    }
    return rol;
  }

  private void exigirPropietario(long propietario, long solicitante) {
    if (propietario != solicitante) {
      throw prohibido();
    }
  }

  private OffsetDateTime ahora() {
    return OffsetDateTime.now(reloj);
  }

  private ResponseStatusException noExiste(String recurso) {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el " + recurso);
  }

  private ResponseStatusException conflicto(String mensaje) {
    return new ResponseStatusException(HttpStatus.CONFLICT, mensaje);
  }

  private ResponseStatusException prohibido() {
    return new ResponseStatusException(HttpStatus.FORBIDDEN,
        "No tienes permiso para realizar esta operación");
  }
}
