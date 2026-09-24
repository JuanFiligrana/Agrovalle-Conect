package co.edu.uniajc.agrovalle.repositories;

import co.edu.uniajc.agrovalle.models.Cosecha;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import org.springframework.stereotype.Repository;

/** Repositorio temporal de cosechas publicadas. */
@Repository
public class CosechaRepository {

  private final AtomicLong sequence = new AtomicLong();
  private final Map<Long, Cosecha> data = new ConcurrentHashMap<>();

  /** Asigna un identificador y conserva la oferta en memoria. */
  public Cosecha save(Cosecha cosecha) {
    long id = sequence.incrementAndGet();
    Cosecha saved = new Cosecha(id, cosecha.agricultorId(), cosecha.fincaId(), cosecha.tipo(),
        cosecha.categoria(), cosecha.cantidadKg(), cosecha.precioCopKg(),
        cosecha.fechaCosecha(), cosecha.activa());
    data.put(id, saved);
    return saved;
  }

  /** Devuelve las ofertas almacenadas durante esta ejecución. */
  public Collection<Cosecha> findAll() {
    return data.values();
  }

  /** Permite consultar y filtrar las ofertas almacenadas. */
  public Stream<Cosecha> stream() {
    return data.values().stream();
  }

  /** Busca una oferta por identificador; devuelve null si no existe. */
  public Cosecha findById(long id) {
    return data.get(id);
  }
}
