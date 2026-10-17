package co.edu.uniajc.agrovalle.controllers;

import co.edu.uniajc.agrovalle.dto.ContactoRequest;
import co.edu.uniajc.agrovalle.dto.ContactoResponse;
import co.edu.uniajc.agrovalle.services.ContactoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** Endpoint de contacto entre comprador y agricultor. */
@RestController
@RequestMapping("/api/v1/contacto")
public class ContactoController {

  private final ContactoService service;

  /** Conecta las solicitudes HTTP con el servicio de contacto. */
  public ContactoController(ContactoService service) {
    this.service = service;
  }

  /** Registra una intención de compra con una autorización presente. */
  @PostMapping("/mensaje")
  @ResponseStatus(HttpStatus.OK)
  public ContactoResponse registrar(@RequestHeader(value = "Authorization", required = false)
      String authorization, @Valid @RequestBody ContactoRequest request) {
    if (authorization == null || authorization.isBlank()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
          "Se requiere una autorización para contactar al agricultor");
    }
    return service.registrar(request);
  }
}
