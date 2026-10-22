package co.edu.uniajc.agrovalle.services;

import co.edu.uniajc.agrovalle.dto.CosechaRequest;
import co.edu.uniajc.agrovalle.events.CosechaPublicadaEvent;
import co.edu.uniajc.agrovalle.models.Cosecha;
import co.edu.uniajc.agrovalle.models.RolUsuario;
import co.edu.uniajc.agrovalle.ports.CosechaPort;
import co.edu.uniajc.agrovalle.ports.FincaPort;
import co.edu.uniajc.agrovalle.security.UsuarioAutenticado;
import co.edu.uniajc.agrovalle.validation.CategoriasCosecha;
import co.edu.uniajc.agrovalle.validation.MunicipiosValle;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** Reglas de publicación y consulta de cosechas, separadas de la referencia de precios. */
@Service
public class CosechaService {

  private static final ZoneId ZONA_COLOMBIA = ZoneId.of("America/Bogota");
  private final CosechaPort cosechas;
  private final FincaPort fincas;
  private final ApplicationEventPublisher events;

  /** Recibe contratos pequeños de datos mediante inyección por constructor. */
  public CosechaService(CosechaPort cosechas, FincaPort fincas,
      ApplicationEventPublisher events) {
    this.cosechas = cosechas;
    this.fincas = fincas;
    this.events = events;
  }

  /** Comprueba rol, catálogo y propiedad antes de publicar y notificar la oferta. */
  @Transactional
  public Cosecha publicar(CosechaRequest request, UsuarioAutenticado usuario) {
    if (usuario.rol() != RolUsuario.AGRICULTOR) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          "Solo un agricultor puede publicar cosechas");
    }
    String categoria = categoriaValida(request.categoria());
    if (request.fechaCosecha().isBefore(LocalDate.now(ZONA_COLOMBIA))) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "fechaCosecha: no puede ser anterior a hoy en Colombia");
    }
    var finca = fincas.findById(request.fincaId()).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "No existe la finca " + request.fincaId()));
    if (finca.agricultorId() != usuario.id()) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          "La finca no pertenece al agricultor");
    }
    Cosecha saved = cosechas.save(new Cosecha(0, usuario.id(), request.fincaId(),
        request.tipo().trim(), categoria, request.cantidadKg(), request.precioCopKg(),
        request.fechaCosecha(), true));
    events.publishEvent(new CosechaPublicadaEvent(saved));
    return saved;
  }

  /** Filtra las ofertas activas por municipio y categoría cuando se especifican. */
  @Transactional(readOnly = true)
  public List<Cosecha> listar(String municipio, String categoria) {
    String municipioCanonico = municipioValido(municipio);
    String categoriaCanonica = categoriaValidaOpcional(categoria);
    return cosechas.findAll().stream()
        .filter(Cosecha::activa)
        .filter(item -> matchesMunicipio(item, municipioCanonico))
        .filter(item -> matches(item.categoria(), categoriaCanonica))
        .toList();
  }

  /** Obtiene una oferta o informa su ausencia a la capa de presentación. */
  @Transactional(readOnly = true)
  public Cosecha require(long id) {
    Cosecha cosecha = cosechas.findById(id);
    if (cosecha == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la cosecha " + id);
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

  private String municipioValido(String municipio) {
    if (municipio == null || municipio.isBlank()) {
      return null;
    }
    return MunicipiosValle.canonicalizar(municipio)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "municipio: debe pertenecer al catálogo del Valle del Cauca"));
  }

  private String categoriaValidaOpcional(String categoria) {
    return categoria == null || categoria.isBlank() ? null : categoriaValida(categoria);
  }

  private String categoriaValida(String categoria) {
    return CategoriasCosecha.canonicalizar(categoria)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "categoria: no pertenece al catálogo"));
  }
}
