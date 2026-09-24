package co.edu.uniajc.agrovalle.services;

import co.edu.uniajc.agrovalle.dto.ContactoRequest;
import co.edu.uniajc.agrovalle.dto.ContactoResponse;
import co.edu.uniajc.agrovalle.models.Contacto;
import co.edu.uniajc.agrovalle.models.RolUsuario;
import co.edu.uniajc.agrovalle.repositories.ContactoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Reglas para registrar la intención de contacto del comprador. */
@Service
public class ContactoService {

  private final ContactoRepository repository;
  private final AgricultorService agricultorService;
  private final CosechaService cosechaService;

  /** Recibe las dependencias para validar y registrar solicitudes de contacto. */
  public ContactoService(ContactoRepository repository, AgricultorService agricultorService,
      CosechaService cosechaService) {
    this.repository = repository;
    this.agricultorService = agricultorService;
    this.cosechaService = cosechaService;
  }

  /** Comprueba el rol comprador y la oferta antes de registrar el mensaje. */
  public ContactoResponse registrar(ContactoRequest request) {
    if (agricultorService.require(request.compradorId()).rol()
        != RolUsuario.COMPRADOR) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          "Solo un comprador puede solicitar contacto");
    }
    cosechaService.require(request.productoId());
    Contacto saved = repository.save(new Contacto(0, request.compradorId(), request.productoId(),
        request.mensaje().trim(), "REGISTRADO"));
    return new ContactoResponse(saved.id(), saved.productoId(), saved.estado());
  }
}
