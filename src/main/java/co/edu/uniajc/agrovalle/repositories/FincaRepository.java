package co.edu.uniajc.agrovalle.repositories;

import co.edu.uniajc.agrovalle.models.Finca;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/** Repositorio temporal de fincas. */
@Repository
public class FincaRepository {

  private final AtomicLong sequence = new AtomicLong();
  private final Map<Long, Finca> data = new ConcurrentHashMap<>();

  /** Asigna un identificador y conserva la finca en memoria. */
  public Finca save(Finca finca) {
    long id = sequence.incrementAndGet();
    Finca saved = new Finca(id, finca.agricultorId(), finca.nombre(), finca.municipio(),
        finca.descripcion());
    data.put(id, saved);
    return saved;
  }

  /** Busca una finca por su identificador. */
  public Optional<Finca> findById(long id) {
    return Optional.ofNullable(data.get(id));
  }
}
