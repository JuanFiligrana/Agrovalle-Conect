package co.edu.uniajc.agrovalle.services;

import co.edu.uniajc.agrovalle.dto.CosechaRequest;
import co.edu.uniajc.agrovalle.exceptions.OperacionNoPermitidaException;
import co.edu.uniajc.agrovalle.exceptions.RecursoNoEncontradoException;
import co.edu.uniajc.agrovalle.models.Cosecha;
import co.edu.uniajc.agrovalle.models.RolUsuario;
import co.edu.uniajc.agrovalle.ports.CosechaPort;
import co.edu.uniajc.agrovalle.ports.FincaPort;
import co.edu.uniajc.agrovalle.ports.UsuarioPort;
import java.util.List;
import org.springframework.stereotype.Service;

/** Reglas de publicación y consulta de cosechas, separadas de la referencia de precios. */
@Service
public class CosechaService {

  private final CosechaPort cosechas;
  private final FincaPort fincas;
  private final UsuarioPort usuarios;

  /** Recibe contratos pequeños de datos mediante inyección por constructor. */
  public CosechaService(CosechaPort cosechas, FincaPort fincas, UsuarioPort usuarios) {
    this.cosechas = cosechas;
    this.fincas = fincas;
    this.usuarios = usuarios;
  }

  /** Comprueba el rol y la propiedad de la finca antes de publicar una oferta. */
  public Cosecha publicar(CosechaRequest request) {
    var agricultor = usuarios.findById(request.agricultorId()).orElseThrow(
        () -> new RecursoNoEncontradoException("No existe el usuario " + request.agricultorId()));
    if (agricultor.rol() != RolUsuario.AGRICULTOR) {
      throw new OperacionNoPermitidaException("Solo un agricultor puede publicar cosechas");
    }
    var finca = fincas.findById(request.fincaId()).orElseThrow(
        () -> new RecursoNoEncontradoException("No existe la finca"));
    if (finca.agricultorId() != request.agricultorId()) {
      throw new OperacionNoPermitidaException("La finca no pertenece al agricultor");
    }
    return cosechas.save(new Cosecha(0, request.agricultorId(), request.fincaId(),
        request.tipo().trim(), request.categoria().trim(), request.cantidadKg(),
        request.precioCopKg(), request.fechaCosecha(), true));
  }

  /** Filtra las ofertas activas por municipio y categoría cuando se especifican. */
  public List<Cosecha> listar(String municipio, String categoria) {
    return cosechas.findAll().stream()
        .filter(Cosecha::activa)
        .filter(item -> matchesMunicipio(item, municipio))
        .filter(item -> matches(item.categoria(), categoria))
        .toList();
  }

  /** Obtiene una oferta o informa su ausencia a la capa de presentación. */
  public Cosecha require(long id) {
    Cosecha cosecha = cosechas.findById(id);
    if (cosecha == null) {
      throw new RecursoNoEncontradoException("No existe la cosecha " + id);
    }
    return cosecha;
  }

  private boolean matchesMunicipio(Cosecha cosecha, String municipio) {
    if (municipio == null || municipio.isBlank()) {
      return true;
    }
    return fincas.findById(cosecha.fincaId())
        .map(finca -> finca.municipio().equalsIgnoreCase(municipio.trim()))
        .orElse(false);
  }

  private boolean matches(String value, String filter) {
    return filter == null || filter.isBlank() || value.equalsIgnoreCase(filter.trim());
  }
}
