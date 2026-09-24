package co.edu.uniajc.agrovalle.repositories;

import co.edu.uniajc.agrovalle.models.Agricultor;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/** Repositorio temporal en memoria para el incremento demostrable. */
@Repository
public class AgricultorRepository {

  private final AtomicLong sequence = new AtomicLong();
  private final Map<Long, Agricultor> data = new ConcurrentHashMap<>();

  /** Asigna un identificador y guarda la cuenta durante esta ejecución. */
  public Agricultor save(Agricultor agricultor) {
    long id = sequence.incrementAndGet();
    Agricultor saved = new Agricultor(id, agricultor.nombre(), agricultor.correo(),
        agricultor.cedula(), agricultor.ubicacionValle(), agricultor.rol(),
        agricultor.contrasenaHash());
    data.put(id, saved);
    return saved;
  }

  /** Busca una cuenta por su identificador. */
  public Optional<Agricultor> findById(long id) {
    return Optional.ofNullable(data.get(id));
  }

  /** Detecta correos o cédulas registrados sin distinguir mayúsculas. */
  public boolean existsByCorreoOrCedula(String correo, String cedula) {
    return data.values().stream().anyMatch(item -> item.correo().equalsIgnoreCase(correo)
        || item.cedula().equalsIgnoreCase(cedula));
  }
}
