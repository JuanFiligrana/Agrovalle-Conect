package co.edu.uniajc.agrovalle.services;

import co.edu.uniajc.agrovalle.dto.FincaRequest;
import co.edu.uniajc.agrovalle.models.Finca;
import co.edu.uniajc.agrovalle.models.RolUsuario;
import co.edu.uniajc.agrovalle.repositories.FincaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Reglas para crear fincas pertenecientes a agricultores. */
@Service
public class FincaService {

  private final FincaRepository repository;
  private final AgricultorService agricultorService;

  /** Recibe las dependencias para registrar y consultar fincas. */
  public FincaService(FincaRepository repository, AgricultorService agricultorService) {
    this.repository = repository;
    this.agricultorService = agricultorService;
  }

  /** Registra una finca después de comprobar que su propietario es agricultor. */
  public Finca crear(FincaRequest request) {
    if (agricultorService.require(request.agricultorId()).rol() != RolUsuario.AGRICULTOR) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          "Solo un agricultor puede registrar una finca");
    }
    return repository.save(new Finca(0, request.agricultorId(), request.nombre().trim(),
        request.municipio().trim(), request.descripcion()));
  }

  /** Obtiene una finca o responde con un error 404 si no existe. */
  public Finca require(long id) {
    return repository.findById(id).orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "No existe la finca " + id));
  }
}
