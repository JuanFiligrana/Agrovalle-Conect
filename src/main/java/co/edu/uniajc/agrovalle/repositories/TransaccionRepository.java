package co.edu.uniajc.agrovalle.repositories;

import co.edu.uniajc.agrovalle.models.Transaccion;
import co.edu.uniajc.agrovalle.ports.PrecioPort;
import co.edu.uniajc.agrovalle.repositories.jpa.TransaccionJpaRepository;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Consulta histórica portable entre PostgreSQL y H2, sin extensiones de texto obligatorias. */
@Repository
@Transactional(readOnly = true)
public class TransaccionRepository implements PrecioPort {
  private final TransaccionJpaRepository transacciones;

  /** Conecta la consulta de ventas con la misma base transaccional del comercio. */
  public TransaccionRepository(TransaccionJpaRepository transacciones) {
    this.transacciones = transacciones;
  }

  /** Selecciona ventas completas en el período y compara nombres sin mayúsculas ni tildes. */
  public List<Transaccion> completadas(String producto, OffsetDateTime desde,
      OffsetDateTime hasta) {
    final String buscado = normalizar(producto);
    return transacciones.findByEstadoAndFechaFinalizacionBetweenOrderByIdAsc(
        "COMPLETADA", desde, hasta).stream()
        .map(item -> new Transaccion(item.getId(), item.getProducto().getId(),
            item.getNombreProducto(), item.getPrecioUnitarioCopKg(), item.getEstado(),
            item.getFechaFinalizacion()))
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
