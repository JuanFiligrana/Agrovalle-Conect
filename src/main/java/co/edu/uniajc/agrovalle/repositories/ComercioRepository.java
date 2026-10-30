package co.edu.uniajc.agrovalle.repositories;

import co.edu.uniajc.agrovalle.models.Comercio;
import co.edu.uniajc.agrovalle.ports.ComercioPort;
import co.edu.uniajc.agrovalle.ports.PrecioPort;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

/** Adaptador SQL que comparte conexión y transacción con el gestor JPA de Spring. */
@Repository
public class ComercioRepository implements ComercioPort, PrecioPort {
  private static final String OFERTA_SQL = """
      SELECT c.id, f.agricultor_id, c.producto_id, p.nombre AS producto,
             c.cantidad_kg, c.precio_cop_kg, c.activa
      FROM cosechas c JOIN fincas f ON f.id = c.finca_id
      JOIN productos p ON p.id = c.producto_id
      """;
  private final JdbcTemplate jdbc;

  /** Recibe el acceso SQL administrado por Spring. */
  public ComercioRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public String rol(long usuarioId, boolean bloquear) {
    return jdbc.query("SELECT rol FROM usuarios WHERE id = ?"
        + (bloquear ? " FOR UPDATE" : ""), (rs, row) -> rs.getString(1), usuarioId)
        .stream().findFirst().orElse("");
  }

  @Override
  public Optional<Comercio.Oferta> oferta(long id, boolean bloquear) {
    if (bloquear) {
      jdbc.query("SELECT id FROM cosechas WHERE id = ? FOR UPDATE",
          (rs, row) -> rs.getLong(1), id);
    }
    return jdbc.query(OFERTA_SQL + " WHERE c.id = ?", (rs, row) -> oferta(rs), id)
        .stream().findFirst();
  }

  private Comercio.Oferta oferta(ResultSet rs) throws SQLException {
    return new Comercio.Oferta(rs.getLong("id"), rs.getLong("agricultor_id"),
        rs.getLong("producto_id"), rs.getString("producto"), rs.getBigDecimal("cantidad_kg"),
        rs.getBigDecimal("precio_cop_kg"), rs.getBoolean("activa"));
  }

  @Override
  public List<Comercio.Oferta> ofertas(long agricultorId) {
    return jdbc.query(OFERTA_SQL + " WHERE f.agricultor_id = ? ORDER BY c.id",
        (rs, row) -> oferta(rs), agricultorId);
  }

  @Override
  public BigDecimal reservada(long cosechaId, OffsetDateTime ahora) {
    return jdbc.queryForObject("""
        SELECT COALESCE(SUM(cantidad_kg), 0) FROM reservas
        WHERE cosecha_id = ? AND activa = TRUE AND vence_en > ?
        """, BigDecimal.class, cosechaId, ahora);
  }

  @Override
  public void actualizarOferta(long id, BigDecimal cantidad, BigDecimal precio) {
    jdbc.update("UPDATE cosechas SET cantidad_kg = ?, precio_cop_kg = ? WHERE id = ?",
        cantidad, precio, id);
  }

  @Override
  public Comercio.Carrito carritoActual(long compradorId) {
    return jdbc.query("""
        SELECT id, comprador_id, abierto FROM carritos
        WHERE comprador_id = ? AND abierto = TRUE
        """, (rs, row) -> new Comercio.Carrito(rs.getLong("id"),
            rs.getLong("comprador_id"), rs.getBoolean("abierto")), compradorId)
        .stream().findFirst().orElseGet(() -> new Comercio.Carrito(
            insertar("carritos", "comprador_id", compradorId, "abierto", true),
            compradorId, true));
  }

  @Override
  public Optional<Comercio.Carrito> carrito(long id) {
    return jdbc.query("SELECT * FROM carritos WHERE id = ?",
        (rs, row) -> new Comercio.Carrito(rs.getLong("id"),
            rs.getLong("comprador_id"), rs.getBoolean("abierto")), id).stream().findFirst();
  }

  @Override
  public List<Comercio.Reserva> reservas(long carritoId) {
    return jdbc.query("""
        SELECT r.*, f.agricultor_id, c.producto_id, p.nombre AS producto
        FROM reservas r JOIN cosechas c ON c.id = r.cosecha_id
        JOIN fincas f ON f.id = c.finca_id JOIN productos p ON p.id = c.producto_id
        WHERE r.carrito_id = ? AND r.activa = TRUE ORDER BY r.cosecha_id
        """, (rs, row) -> new Comercio.Reserva(rs.getLong("id"), rs.getLong("carrito_id"),
            rs.getLong("cosecha_id"), rs.getLong("agricultor_id"), rs.getLong("producto_id"),
            rs.getString("producto"), rs.getBigDecimal("cantidad_kg"),
            rs.getBigDecimal("precio_cop_kg"),
            rs.getObject("vence_en", OffsetDateTime.class)), carritoId);
  }

  @Override
  public void reservar(long carritoId, long cosechaId, BigDecimal cantidad, BigDecimal precio,
      OffsetDateTime venceEn) {
    int actualizadas = jdbc.update("""
        UPDATE reservas SET cantidad_kg = ?, precio_cop_kg = ?, vence_en = ?, activa = TRUE
        WHERE carrito_id = ? AND cosecha_id = ?
        """, cantidad, precio, venceEn, carritoId, cosechaId);
    if (actualizadas == 0) {
      insertar("reservas", "carrito_id", carritoId, "cosecha_id", cosechaId,
          "cantidad_kg", cantidad, "precio_cop_kg", precio, "vence_en", venceEn, "activa", true);
    }
  }

  @Override
  public void quitarReserva(long carritoId, long cosechaId) {
    jdbc.update("UPDATE reservas SET activa = FALSE WHERE carrito_id = ? AND cosecha_id = ?",
        carritoId, cosechaId);
  }

  @Override
  public void cerrarCarrito(long carritoId) {
    jdbc.update("UPDATE reservas SET activa = FALSE WHERE carrito_id = ?", carritoId);
    jdbc.update("UPDATE carritos SET abierto = FALSE WHERE id = ?", carritoId);
  }

  @Override
  public Optional<Comercio.Pedido> porClave(long compradorId, String clave) {
    return jdbc.query("SELECT * FROM pedidos WHERE comprador_id = ? AND clave = ?",
        (rs, row) -> pedido(rs), compradorId, clave).stream().findFirst();
  }

  @Override
  public Optional<Comercio.Pedido> pedido(long id, boolean bloquear) {
    return jdbc.query("SELECT * FROM pedidos WHERE id = ?" + (bloquear ? " FOR UPDATE" : ""),
        (rs, row) -> pedido(rs), id).stream().findFirst();
  }

  private Comercio.Pedido pedido(ResultSet rs) throws SQLException {
    return new Comercio.Pedido(rs.getLong("id"), rs.getLong("comprador_id"),
        rs.getLong("agricultor_id"), rs.getLong("carrito_id"), rs.getString("clave"),
        rs.getString("estado"), rs.getBigDecimal("total"),
        rs.getObject("creado_en", OffsetDateTime.class),
        rs.getObject("fecha_programada", OffsetDateTime.class), rs.getString("ruta"));
  }

  @Override
  public List<Comercio.Pedido> pedidos(long usuarioId, boolean agricultor) {
    return jdbc.query("SELECT * FROM pedidos WHERE "
        + (agricultor ? "agricultor_id" : "comprador_id") + " = ? ORDER BY id DESC",
        (rs, row) -> pedido(rs), usuarioId);
  }

  @Override
  public long crearPedido(long compradorId, long agricultorId, long carritoId, String clave,
      BigDecimal total, OffsetDateTime ahora) {
    return insertar("pedidos", "comprador_id", compradorId, "agricultor_id", agricultorId,
        "carrito_id", carritoId, "clave", clave, "estado", "PENDIENTE", "total", total,
        "creado_en", ahora);
  }

  @Override
  public void agregarLinea(long pedidoId, Comercio.Reserva reserva) {
    insertar("pedido_items", "pedido_id", pedidoId, "cosecha_id", reserva.cosechaId(),
        "producto_id", reserva.productoId(), "producto", reserva.producto(),
        "cantidad_kg", reserva.cantidadKg(), "precio_cop_kg", reserva.precioCopKg());
  }

  @Override
  public List<Comercio.Linea> lineas(long pedidoId) {
    return jdbc.query("SELECT * FROM pedido_items WHERE pedido_id = ? ORDER BY cosecha_id",
        (rs, row) -> new Comercio.Linea(rs.getLong("id"), rs.getLong("pedido_id"),
            rs.getLong("cosecha_id"), rs.getLong("producto_id"), rs.getString("producto"),
            rs.getBigDecimal("cantidad_kg"), rs.getBigDecimal("precio_cop_kg")), pedidoId);
  }

  @Override
  public void estado(long pedidoId, String estado, OffsetDateTime programada, String ruta) {
    jdbc.update("UPDATE pedidos SET estado = ?, fecha_programada = ?, ruta = ? WHERE id = ?",
        estado, programada, ruta, pedidoId);
  }

  @Override
  public void evento(long pedidoId, String estado, OffsetDateTime fecha, String descripcion) {
    insertar("eventos_pedido", "pedido_id", pedidoId, "estado", estado,
        "fecha", fecha, "descripcion", descripcion);
  }

  @Override
  public List<Comercio.Evento> eventos(long pedidoId) {
    return jdbc.query("SELECT * FROM eventos_pedido WHERE pedido_id = ? ORDER BY fecha, id",
        (rs, row) -> new Comercio.Evento(rs.getLong("id"), rs.getString("estado"),
            rs.getObject("fecha", OffsetDateTime.class), rs.getString("descripcion")), pedidoId);
  }

  @Override
  public void avisar(long pedidoId, long destinatarioId, String mensaje, OffsetDateTime fecha) {
    insertar("avisos_pedido", "pedido_id", pedidoId, "destinatario_id", destinatarioId,
        "mensaje", mensaje, "fecha", fecha);
  }

  @Override
  public List<Comercio.Aviso> avisos(long destinatarioId) {
    return jdbc.query("""
        SELECT * FROM avisos_pedido WHERE destinatario_id = ? ORDER BY fecha DESC, id DESC
        """, (rs, row) -> new Comercio.Aviso(rs.getLong("id"), rs.getLong("pedido_id"),
            rs.getString("mensaje"), rs.getObject("fecha", OffsetDateTime.class)), destinatarioId);
  }

  @Override
  public void finalizarVenta(Comercio.Linea linea, OffsetDateTime fecha) {
    insertar("transacciones", "pedido_item_id", linea.id(), "producto_id", linea.productoId(),
        "producto", linea.producto(), "precio_cop_kg", linea.precioCopKg(),
        "estado", "COMPLETADA", "finalizada_en", fecha);
  }

  @Override
  public List<BigDecimal> preciosFinalizados(String producto, OffsetDateTime desde,
      OffsetDateTime hasta) {
    return jdbc.query("""
        SELECT precio_cop_kg FROM transacciones
        WHERE LOWER(TRIM(producto)) = LOWER(TRIM(?)) AND estado = 'COMPLETADA'
        AND finalizada_en >= ? AND finalizada_en <= ? ORDER BY id
        """, (rs, row) -> rs.getBigDecimal(1), producto, desde, hasta);
  }

  @Override
  public Comercio.Resumen resumen() {
    return new Comercio.Resumen(contar("usuarios"), contar("fincas"), contar("cosechas"),
        contar("pedidos"), contarEstado("PENDIENTE"), contarEstado("ENTREGADO"),
        contarEstado("CANCELADA"));
  }

  private long contar(String tabla) {
    return jdbc.queryForObject("SELECT COUNT(*) FROM " + tabla, Long.class);
  }

  private long contarEstado(String estado) {
    return jdbc.queryForObject("SELECT COUNT(*) FROM pedidos WHERE estado = ?",
        Long.class, estado);
  }

  private long insertar(String tabla, Object... pares) {
    Map<String, Object> valores = new LinkedHashMap<>();
    for (int index = 0; index < pares.length; index += 2) {
      valores.put((String) pares[index], pares[index + 1]);
    }
    return new SimpleJdbcInsert(jdbc).withTableName(tabla).usingGeneratedKeyColumns("id")
        .usingColumns(valores.keySet().toArray(String[]::new)).executeAndReturnKey(valores)
        .longValue();
  }
}
