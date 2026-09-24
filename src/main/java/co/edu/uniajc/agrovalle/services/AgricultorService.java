package co.edu.uniajc.agrovalle.services;

import co.edu.uniajc.agrovalle.dto.RegistroAgricultorRequest;
import co.edu.uniajc.agrovalle.models.Agricultor;
import co.edu.uniajc.agrovalle.models.RolUsuario;
import co.edu.uniajc.agrovalle.repositories.AgricultorRepository;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Reglas de registro y consulta de cuentas. */
@Service
public class AgricultorService {

  private final AgricultorRepository repository;
  private final PasswordEncoder passwordEncoder;

  /** Recibe el repositorio de cuentas y el codificador de contraseñas. */
  public AgricultorService(AgricultorRepository repository, PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
  }

  /** Valida la unicidad y el rol, calcula el hash de la contraseña y registra la cuenta. */
  public Agricultor registrar(RegistroAgricultorRequest request) {
    if (repository.existsByCorreoOrCedula(request.correo(), request.cedula())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          "El correo o la cédula ya están registrados");
    }
    RolUsuario rol = parseRole(request.rol());
    return repository.save(new Agricultor(0, request.nombre().trim(), request.correo().trim(),
        request.cedula().trim(), request.ubicacionValle().trim(), rol,
        passwordEncoder.encode(request.contrasena())));
  }

  /** Obtiene una cuenta o responde con un error 404 si no existe. */
  public Agricultor require(long id) {
    return repository.findById(id).orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "No existe el usuario " + id));
  }

  private RolUsuario parseRole(String value) {
    try {
      return RolUsuario.valueOf(value.trim().toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException exception) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "El rol debe ser AGRICULTOR o COMPRADOR");
    }
  }
}
