package co.edu.uniajc.agrovalle.repositories;

import co.edu.uniajc.agrovalle.models.Transaccion;
import co.edu.uniajc.agrovalle.ports.PrecioPort;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Consulta histórica portable entre PostgreSQL y H2, sin extensiones de texto obligatorias. */
@Repository
public class TransaccionRepository implements PrecioPort {
  private final JdbcTemplate jdbc;

  /** Conecta la consulta de ventas con la misma base transaccional del comercio. */
  public TransaccionRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  /** Selecciona ventas completas en el período y compara nombres sin mayúsculas ni tildes. */
  public List<Transaccion> completadas(String producto, OffsetDateTime desde, OffsetDateTime hasta) {
    final String buscado = normalizar(producto);
    return jdbc.query("""
        SELECT id, producto_id, producto, precio_cop_kg, estado, finalizada_en
        FROM transacciones WHERE estado = 'COMPLETADA'
        AND finalizada_en >= ? AND finalizada_en <= ? ORDER BY id
        """, (rs, row) -> new Transaccion(rs.getLong("id"), rs.getLong("producto_id"),
            rs.getString("producto"), rs.getBigDecimal("precio_cop_kg"), rs.getString("estado"),
            rs.getObject("finalizada_en", OffsetDateTime.class)), desde, hasta).stream()
        .filter(item -> normalizar(item.producto()).equals(buscado)).toList();
  }

  @Override
  public List<BigDecimal> preciosFinalizados(String producto, OffsetDateTime desde,
      OffsetDateTime hasta) {
    return completadas(producto, desde, hasta).stream()
        .map(Transaccion::precioUnitarioCopKg).toList();
  }

  private String normalizar(String producto) {
    return Normalizer.normalize(producto.trim().toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
        .replaceAll("\\p{M}+", "");
  }
}
