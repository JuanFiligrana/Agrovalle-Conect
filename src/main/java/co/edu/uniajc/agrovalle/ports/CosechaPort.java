package co.edu.uniajc.agrovalle.ports;

import co.edu.uniajc.agrovalle.models.Cosecha;
import java.util.Collection;

/** Operaciones de ofertas requeridas por los servicios del prototipo. */
public interface CosechaPort {

  /** Guarda una oferta y devuelve la representación con su identificador. */
  Cosecha save(Cosecha cosecha);

  /** Obtiene ofertas para las consultas en memoria de este incremento. */
  Collection<Cosecha> findAll();

  /** Busca una oferta; conserva el contrato actual que devuelve null si no existe. */
  Cosecha findById(long id);
}
