package co.edu.uniajc.agrovalle.repositories;

import co.edu.uniajc.agrovalle.models.Contacto;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/** Repositorio temporal de solicitudes de contacto. */
@Repository
public class ContactoRepository {

  private final AtomicLong sequence = new AtomicLong();
  private final Map<Long, Contacto> data = new ConcurrentHashMap<>();

  /** Asigna un identificador y conserva la solicitud de contacto en memoria. */
  public Contacto save(Contacto contacto) {
    long id = sequence.incrementAndGet();
    Contacto saved = new Contacto(id, contacto.compradorId(), contacto.productoId(),
        contacto.mensaje(), contacto.estado());
    data.put(id, saved);
    return saved;
  }
}
