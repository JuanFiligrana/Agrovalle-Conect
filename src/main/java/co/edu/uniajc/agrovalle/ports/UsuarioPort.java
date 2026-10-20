package co.edu.uniajc.agrovalle.ports;

import co.edu.uniajc.agrovalle.models.Agricultor;
import java.util.Optional;

/** Consulta de cuentas requerida por los casos de uso de cosechas y contacto. */
public interface UsuarioPort {

  /** Guarda una cuenta y devuelve el identificador asignado por la base de datos. */
  Agricultor save(Agricultor usuario);

  /** Busca la cuenta con su rol; el prototipo la representa mediante Agricultor. */
  Optional<Agricultor> findById(long id);

  /** Comprueba la unicidad del correo y la cédula. */
  boolean existsByCorreoOrCedula(String correo, String cedula);
}
