package co.edu.uniajc.agrovalle.repositories;

import co.edu.uniajc.agrovalle.models.Contacto;
import co.edu.uniajc.agrovalle.models.MensajeContacto;
import co.edu.uniajc.agrovalle.models.NotificacionContacto;
import co.edu.uniajc.agrovalle.ports.ContactoPort;
import co.edu.uniajc.agrovalle.repositories.jpa.ContactoEntity;
import co.edu.uniajc.agrovalle.repositories.jpa.ContactoJpaRepository;
import co.edu.uniajc.agrovalle.repositories.jpa.CosechaJpaRepository;
import co.edu.uniajc.agrovalle.repositories.jpa.NotificacionContactoEntity;
import co.edu.uniajc.agrovalle.repositories.jpa.NotificacionContactoJpaRepository;
import co.edu.uniajc.agrovalle.repositories.jpa.UsuarioJpaRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Adaptador transaccional: el mensaje y su notificación se guardan juntos. */
@Repository
public class ContactoRepository implements ContactoPort {

  private final ContactoJpaRepository contactos;
  private final NotificacionContactoJpaRepository notificaciones;
  private final UsuarioJpaRepository usuarios;
  private final CosechaJpaRepository cosechas;

  /** Conecta las relaciones persistentes del contacto. */
  public ContactoRepository(ContactoJpaRepository contactos,
      NotificacionContactoJpaRepository notificaciones, UsuarioJpaRepository usuarios,
      CosechaJpaRepository cosechas) {
    this.contactos = contactos;
    this.notificaciones = notificaciones;
    this.usuarios = usuarios;
    this.cosechas = cosechas;
  }

  /** No se devuelve una confirmación si falla cualquiera de las dos escrituras. */
  @Override
  @Transactional
  public Contacto save(Contacto contacto) {
    var comprador = usuarios.findById(contacto.compradorId()).orElseThrow();
    var cosecha = cosechas.findById(contacto.productoId()).orElseThrow();
    var agricultor = usuarios.findById(cosecha.getFinca().getAgricultorId()).orElseThrow();
    var entity = contactos.saveAndFlush(new ContactoEntity(comprador, cosecha,
        contacto.mensaje(), OffsetDateTime.now(ZoneOffset.UTC)));
    notificaciones.saveAndFlush(new NotificacionContactoEntity(entity, agricultor));
    return new Contacto(entity.getId(), comprador.getId(), cosecha.getId(),
        entity.getMensaje(), "REGISTRADO");
  }

  /** Limita la bandeja a los 100 mensajes propios más recientes. */
  @Override
  @Transactional(readOnly = true)
  public List<MensajeContacto> findByUsuario(long usuarioId) {
    return notificaciones.findPropias(usuarioId, PageRequest.of(0, 100)).stream()
        .map(this::toMensaje).toList();
  }

  /** Recupera únicamente el contrato necesario para autorizar la lectura. */
  @Override
  @Transactional(readOnly = true)
  public Optional<NotificacionContacto> findNotificacion(long id) {
    return notificaciones.findById(id).map(n -> new NotificacionContacto(n.getId(),
        n.getContacto().getId(), n.getDestinatario().getId(), n.isLeida()));
  }

  /** Mantiene la comprobación del destinatario en la actualización. */
  @Override
  @Transactional
  public void marcarLeida(long id, long destinatarioId) {
    notificaciones.marcarLeida(id, destinatarioId);
  }

  private MensajeContacto toMensaje(NotificacionContactoEntity notificacion) {
    var contacto = notificacion.getContacto();
    return new MensajeContacto(contacto.getId(), contacto.getCosecha().getId(),
        contacto.getCosecha().getProducto().getNombre(), contacto.getComprador().getNombre(),
        notificacion.getDestinatario().getNombre(), contacto.getMensaje(),
        contacto.getFechaCreacion(), notificacion.getId(), notificacion.isLeida());
  }
}
