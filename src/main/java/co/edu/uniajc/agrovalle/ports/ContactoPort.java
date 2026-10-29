package co.edu.uniajc.agrovalle.ports;

import co.edu.uniajc.agrovalle.models.Contacto;
import co.edu.uniajc.agrovalle.models.MensajeContacto;
import co.edu.uniajc.agrovalle.models.NotificacionContacto;
import java.util.List;
import java.util.Optional;

/** Escritura de solicitudes de contacto independiente de su almacenamiento. */
public interface ContactoPort {

  /** Guarda solicitud y notificación de forma atómica; no confirma éxitos parciales. */
  Contacto save(Contacto contacto);

  /** Devuelve hasta 100 solicitudes propias, de la más reciente a la más antigua. */
  List<MensajeContacto> findByUsuario(long usuarioId);

  /** Consulta una notificación para comprobar quién puede marcarla como leída. */
  Optional<NotificacionContacto> findNotificacion(long id);

  /** Marca como leída una notificación del destinatario; repetir no duplica registros. */
  void marcarLeida(long id, long destinatarioId);
}
