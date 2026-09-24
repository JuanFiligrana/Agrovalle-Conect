package co.edu.uniajc.agrovalle.controllers;

import co.edu.uniajc.agrovalle.dto.RegistroAgricultorRequest;
import co.edu.uniajc.agrovalle.dto.RegistroAgricultorResponse;
import co.edu.uniajc.agrovalle.models.Agricultor;
import co.edu.uniajc.agrovalle.services.AgricultorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints de registro de usuarios. */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AgricultorService service;

  /** Conecta el controlador de registro con el servicio de cuentas. */
  public AuthController(AgricultorService service) {
    this.service = service;
  }

  /** Registra un agricultor o comprador sin exponer la contraseña. */
  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public RegistroAgricultorResponse registrar(
      @Valid @RequestBody RegistroAgricultorRequest request) {
    Agricultor saved = service.registrar(request);
    return new RegistroAgricultorResponse(saved.id(), saved.nombre(), saved.correo(),
        saved.cedula(), saved.ubicacionValle(), saved.rol());
  }
}
