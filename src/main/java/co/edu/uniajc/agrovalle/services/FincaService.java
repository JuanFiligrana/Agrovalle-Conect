package co.edu.uniajc.agrovalle.services;

import co.edu.uniajc.agrovalle.dto.FincaRequest;
import co.edu.uniajc.agrovalle.models.Finca;
import co.edu.uniajc.agrovalle.models.RolUsuario;
import co.edu.uniajc.agrovalle.ports.FincaPort;
import co.edu.uniajc.agrovalle.security.UsuarioAutenticado;
import co.edu.uniajc.agrovalle.validation.MunicipiosValle;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** Reglas para crear fincas pertenecientes a agricultores. */
@Service
public class FincaService {

  private final FincaPort repository;

  /** Recibe el puerto de persistencia de fincas. */
  public FincaService(FincaPort repository) {
    this.repository = repository;
  }

  /** Registra una finca para el propietario identificado por el JWT. */
  @Transactional
  public Finca crear(FincaRequest request, UsuarioAutenticado usuario) {
    if (usuario.rol() != RolUsuario.AGRICULTOR) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          "Solo un agricultor puede registrar una finca");
    }
    String municipio = MunicipiosValle.canonicalizar(request.municipio())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "municipio: debe pertenecer al catálogo del Valle del Cauca"));
    String descripcion = request.descripcion() == null ? null : request.descripcion().trim();
    return repository.save(new Finca(0, usuario.id(), request.nombre().trim(), municipio,
        descripcion));
  }

  /** Lista las fincas que pertenecen al agricultor autenticado. */
  @Transactional(readOnly = true)
  public List<Finca> listarPropias(UsuarioAutenticado usuario) {
    return repository.findAllByAgricultorId(usuario.id());
  }

  /** Obtiene una finca o responde con un error 404 si no existe. */
  @Transactional(readOnly = true)
  public Finca require(long id) {
    return repository.findById(id).orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "No existe la finca " + id));
  }
}
