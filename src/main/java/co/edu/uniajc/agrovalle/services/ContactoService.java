package co.edu.uniajc.agrovalle.services;

import co.edu.uniajc.agrovalle.dto.ContactoRequest;
import co.edu.uniajc.agrovalle.dto.ContactoResponse;
import co.edu.uniajc.agrovalle.exceptions.OperacionNoPermitidaException;
import co.edu.uniajc.agrovalle.exceptions.RecursoNoEncontradoException;
import co.edu.uniajc.agrovalle.models.Contacto;
import co.edu.uniajc.agrovalle.models.MensajeContacto;
import co.edu.uniajc.agrovalle.models.RolUsuario;
import co.edu.uniajc.agrovalle.ports.ContactoPort;
import co.edu.uniajc.agrovalle.ports.CosechaPort;
import co.edu.uniajc.agrovalle.ports.UsuarioPort;
import co.edu.uniajc.agrovalle.security.UsuarioAutenticado;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Reglas para registrar la intención de contacto del comprador. */
@Service
public class ContactoService {

  private final ContactoPort contactos;
  private final UsuarioPort usuarios;
  private final CosechaPort cosechas;

  /** Recibe contratos de escritura y consulta sin depender de servicios concretos. */
  public ContactoService(ContactoPort contactos, UsuarioPort usuarios, CosechaPort cosechas) {
    this.contactos = contactos;
    this.usuarios = usuarios;
    this.cosechas = cosechas;
  }

  /** Comprueba el rol comprador y la oferta antes de registrar el mensaje. */
  @Transactional
  public ContactoResponse registrar(ContactoRequest request, UsuarioAutenticado usuario) {
    if (usuario.rol() != RolUsuario.COMPRADOR) {
      throw new OperacionNoPermitidaException("Solo un comprador puede solicitar contacto");
    }
    var comprador = usuarios.findById(usuario.id()).orElseThrow(
        () -> new RecursoNoEncontradoException("No existe el usuario " + usuario.id()));
    if (comprador.rol() != RolUsuario.COMPRADOR) {
      throw new OperacionNoPermitidaException("Solo un comprador puede solicitar contacto");
    }
    var cosecha = cosechas.findById(request.productoId());
    if (cosecha == null || !cosecha.activa()) {
      throw new RecursoNoEncontradoException("No existe la cosecha " + request.productoId());
    }
    Contacto saved = contactos.save(new Contacto(0, usuario.id(), request.productoId(),
        request.mensaje().trim(), "REGISTRADO"));
    return new ContactoResponse(saved.id(), saved.productoId(), saved.estado(), true);
  }

  /** La identidad nunca se obtiene de un parámetro de la petición. */
  @Transactional(readOnly = true)
  public List<MensajeContacto> listar(UsuarioAutenticado usuario) {
    return contactos.findByUsuario(usuario.id());
  }

  /** Solo el destinatario puede confirmar lectura; repetir es seguro. */
  @Transactional
  public void marcarLeida(long id, UsuarioAutenticado usuario) {
    var notificacion = contactos.findNotificacion(id).orElseThrow(
        () -> new RecursoNoEncontradoException("No existe la notificación " + id));
    if (notificacion.destinatarioId() != usuario.id()) {
      throw new OperacionNoPermitidaException("La notificación no pertenece al usuario");
    }
    contactos.marcarLeida(id, usuario.id());
  }
}
