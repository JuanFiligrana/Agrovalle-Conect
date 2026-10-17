package co.edu.uniajc.agrovalle.services;

import co.edu.uniajc.agrovalle.dto.ContactoRequest;
import co.edu.uniajc.agrovalle.dto.ContactoResponse;
import co.edu.uniajc.agrovalle.exceptions.OperacionNoPermitidaException;
import co.edu.uniajc.agrovalle.exceptions.RecursoNoEncontradoException;
import co.edu.uniajc.agrovalle.models.Contacto;
import co.edu.uniajc.agrovalle.models.RolUsuario;
import co.edu.uniajc.agrovalle.ports.ContactoPort;
import co.edu.uniajc.agrovalle.ports.CosechaPort;
import co.edu.uniajc.agrovalle.ports.UsuarioPort;
import org.springframework.stereotype.Service;

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
  public ContactoResponse registrar(ContactoRequest request) {
    var comprador = usuarios.findById(request.compradorId()).orElseThrow(
        () -> new RecursoNoEncontradoException("No existe el usuario " + request.compradorId()));
    if (comprador.rol() != RolUsuario.COMPRADOR) {
      throw new OperacionNoPermitidaException("Solo un comprador puede solicitar contacto");
    }
    if (cosechas.findById(request.productoId()) == null) {
      throw new RecursoNoEncontradoException("No existe la cosecha " + request.productoId());
    }
    Contacto saved = contactos.save(new Contacto(0, request.compradorId(), request.productoId(),
        request.mensaje().trim(), "REGISTRADO"));
    return new ContactoResponse(saved.id(), saved.productoId(), saved.estado());
  }
}
