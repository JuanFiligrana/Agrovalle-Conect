package co.edu.uniajc.agrovalle.controllers;

import co.edu.uniajc.agrovalle.dto.ContactoRequest;
import co.edu.uniajc.agrovalle.dto.ContactoResponse;
import co.edu.uniajc.agrovalle.models.MensajeContacto;
import co.edu.uniajc.agrovalle.security.UsuarioAutenticado;
import co.edu.uniajc.agrovalle.services.ContactoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Endpoint de contacto entre comprador y agricultor. */
@RestController
@RequestMapping("/api/v1/contacto")
public class ContactoController {

  private final ContactoService service;

  /** Conecta las solicitudes HTTP con el servicio de contacto. */
  public ContactoController(ContactoService service) {
    this.service = service;
  }

  /** Registra un mensaje con la identidad verificada del JWT. */
  @PostMapping("/mensaje")
  @ResponseStatus(HttpStatus.OK)
  public ContactoResponse registrar(@AuthenticationPrincipal UsuarioAutenticado usuario,
      @Valid @RequestBody ContactoRequest request) {
    return service.registrar(request, usuario);
  }

  /** Devuelve únicamente los mensajes enviados o recibidos por esta cuenta. */
  @GetMapping("/mensajes")
  public List<MensajeContacto> listar(@AuthenticationPrincipal UsuarioAutenticado usuario) {
    return service.listar(usuario);
  }

  /** Confirma lectura sin crear mensajes ni notificaciones adicionales. */
  @PatchMapping("/notificaciones/{id}/lectura")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void marcarLeida(@PathVariable long id,
      @AuthenticationPrincipal UsuarioAutenticado usuario) {
    service.marcarLeida(id, usuario);
  }
}
