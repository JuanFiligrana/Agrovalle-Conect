package co.edu.uniajc.agrovalle.ports;

import co.edu.uniajc.agrovalle.models.Finca;
import java.util.Optional;

/** Consulta de origen y propiedad de las fincas sin elegir su almacenamiento. */
public interface FincaPort {

  /** Busca una finca para comprobar su propietario y municipio. */
  Optional<Finca> findById(long id);
}
