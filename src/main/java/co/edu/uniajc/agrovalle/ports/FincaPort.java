package co.edu.uniajc.agrovalle.ports;

import co.edu.uniajc.agrovalle.models.Finca;
import java.util.List;
import java.util.Optional;

/** Consulta de origen y propiedad de las fincas sin elegir su almacenamiento. */
public interface FincaPort {

  /** Guarda una finca y devuelve el identificador asignado por la base de datos. */
  Finca save(Finca finca);

  /** Busca una finca para comprobar su propietario y municipio. */
  Optional<Finca> findById(long id);

  /** Lista las fincas del agricultor autenticado. */
  List<Finca> findAllByAgricultorId(long agricultorId);
}
