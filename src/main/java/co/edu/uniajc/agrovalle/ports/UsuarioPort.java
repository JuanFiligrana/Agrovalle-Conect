package co.edu.uniajc.agrovalle.ports;

import co.edu.uniajc.agrovalle.models.Agricultor;
import java.util.Optional;

/** Consulta de cuentas requerida por los casos de uso de cosechas y contacto. */
public interface UsuarioPort {

  /** Busca la cuenta con su rol; el prototipo la representa mediante Agricultor. */
  Optional<Agricultor> findById(long id);
}
