package co.edu.uniajc.agrovalle.services;

import co.edu.uniajc.agrovalle.dto.RegistroAgricultorRequest;
import co.edu.uniajc.agrovalle.models.Agricultor;
import co.edu.uniajc.agrovalle.models.RolUsuario;
import co.edu.uniajc.agrovalle.ports.UsuarioPort;
import co.edu.uniajc.agrovalle.validation.MunicipiosValle;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** Reglas de registro y consulta de cuentas. */
@Service
public class AgricultorService {

  private final UsuarioPort repository;
  private final PasswordEncoder passwordEncoder;

  /** Recibe el repositorio de cuentas y el codificador de contraseñas. */
  public AgricultorService(UsuarioPort repository, PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
  }

  /** Valida la unicidad y el rol, calcula el hash de la contraseña y registra la cuenta. */
  @Transactional
  public Agricultor registrar(RegistroAgricultorRequest request) {
    String correo = request.correo().trim().toLowerCase(Locale.ROOT);
    String cedula = request.cedula().trim().toLowerCase(Locale.ROOT);
    if (repository.existsByCorreoOrCedula(correo, cedula)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          "El correo o la cédula ya están registrados");
    }
    RolUsuario rol = request.rol() == null || request.rol().isBlank()
        ? RolUsuario.AGRICULTOR : parseRole(request.rol());
    String municipio = MunicipiosValle.canonicalizar(request.ubicacionValle())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "ubicacionValle: debe pertenecer al catálogo de municipios del Valle del Cauca"));
    return repository.save(new Agricultor(0, request.nombre().trim(), correo,
        cedula, municipio, rol,
        passwordEncoder.encode(request.contrasena())));
  }

  /** Obtiene una cuenta o responde con un error 404 si no existe. */
  @Transactional(readOnly = true)
  public Agricultor require(long id) {
    return repository.findById(id).orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "No existe el usuario " + id));
  }

  private RolUsuario parseRole(String value) {
    try {
      RolUsuario rol = RolUsuario.valueOf(value.trim().toUpperCase(Locale.ROOT));
      if (rol == RolUsuario.ADMINISTRADOR) {
        throw new IllegalArgumentException("El registro público no admite administradores");
      }
      return rol;
    } catch (IllegalArgumentException exception) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "El rol debe ser AGRICULTOR o COMPRADOR");
    }
  }
}
