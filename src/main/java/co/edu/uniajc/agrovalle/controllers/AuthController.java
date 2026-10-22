package co.edu.uniajc.agrovalle.controllers;

import co.edu.uniajc.agrovalle.dto.LoginRequest;
import co.edu.uniajc.agrovalle.dto.LoginResponse;
import co.edu.uniajc.agrovalle.dto.RegistroAgricultorRequest;
import co.edu.uniajc.agrovalle.dto.RegistroAgricultorResponse;
import co.edu.uniajc.agrovalle.dto.UsuarioAutenticadoResponse;
import co.edu.uniajc.agrovalle.models.Agricultor;
import co.edu.uniajc.agrovalle.security.UsuarioAutenticado;
import co.edu.uniajc.agrovalle.services.AgricultorService;
import co.edu.uniajc.agrovalle.services.AutenticacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints de registro de usuarios. */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AgricultorService agricultorService;
  private final AutenticacionService autenticacionService;

  /** Conecta las operaciones públicas de registro e inicio de sesión. */
  public AuthController(AgricultorService agricultorService,
      AutenticacionService autenticacionService) {
    this.agricultorService = agricultorService;
    this.autenticacionService = autenticacionService;
  }

  /** Registra un agricultor o comprador sin exponer la contraseña. */
  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public RegistroAgricultorResponse registrar(
      @Valid @RequestBody RegistroAgricultorRequest request) {
    Agricultor saved = agricultorService.registrar(request);
    return new RegistroAgricultorResponse(saved.id(), saved.nombre(), saved.correo(),
        saved.cedula(), saved.ubicacionValle(), saved.rol());
  }

  /** Valida las credenciales y entrega un JWT firmado con expiración. */
  @PostMapping("/login")
  public LoginResponse iniciarSesion(@Valid @RequestBody LoginRequest request) {
    return autenticacionService.iniciarSesion(request);
  }

  /** Demuestra que el token identifica al usuario sin exponer datos sensibles. */
  @GetMapping("/me")
  public UsuarioAutenticadoResponse consultarSesion(
      @AuthenticationPrincipal UsuarioAutenticado usuario) {
    return new UsuarioAutenticadoResponse(usuario.id(), usuario.correo(), usuario.rol());
  }
}
