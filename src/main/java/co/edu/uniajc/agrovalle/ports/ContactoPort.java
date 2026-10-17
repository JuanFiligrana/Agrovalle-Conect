package co.edu.uniajc.agrovalle.ports;

import co.edu.uniajc.agrovalle.models.Contacto;

/** Escritura de solicitudes de contacto independiente de su almacenamiento. */
public interface ContactoPort {

  /** Guarda una solicitud y devuelve el registro identificado. */
  Contacto save(Contacto contacto);
}
