package co.edu.uniajc.agrovalle.services;

import co.edu.uniajc.agrovalle.dto.LoginRequest;
import co.edu.uniajc.agrovalle.dto.LoginResponse;
import co.edu.uniajc.agrovalle.models.Agricultor;
import co.edu.uniajc.agrovalle.ports.UsuarioPort;
import co.edu.uniajc.agrovalle.security.TokenJwtService;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** Comprueba credenciales y entrega un token firmado. */
@Service
public class AutenticacionService {

  private final UsuarioPort usuarios;
  private final PasswordEncoder passwordEncoder;
  private final TokenJwtService tokenService;

  /** Recibe la persistencia, BCrypt y el emisor de tokens. */
  public AutenticacionService(UsuarioPort usuarios, PasswordEncoder passwordEncoder,
      TokenJwtService tokenService) {
    this.usuarios = usuarios;
    this.passwordEncoder = passwordEncoder;
    this.tokenService = tokenService;
  }

  /** Valida correo y contraseña sin revelar cuál de los dos es incorrecto. */
  @Transactional(readOnly = true)
  public LoginResponse iniciarSesion(LoginRequest request) {
    String correo = request.correo().trim().toLowerCase(Locale.ROOT);
    Agricultor usuario = usuarios.findByCorreo(correo)
        .orElseThrow(AutenticacionService::credencialesInvalidas);
    if (!passwordEncoder.matches(request.contrasena(), usuario.contrasenaHash())) {
      throw credencialesInvalidas();
    }
    return new LoginResponse(tokenService.generar(usuario), "Bearer",
        tokenService.expirationSeconds());
  }

  private static ResponseStatusException credencialesInvalidas() {
    return new ResponseStatusException(HttpStatus.UNAUTHORIZED,
        "Correo o contraseña incorrectos");
  }
}
