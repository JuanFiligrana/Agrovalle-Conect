package co.edu.uniajc.agrovalle.services;

import co.edu.uniajc.agrovalle.dto.CosechaRequest;
import co.edu.uniajc.agrovalle.dto.PrecioPromedioResponse;
import co.edu.uniajc.agrovalle.models.Cosecha;
import co.edu.uniajc.agrovalle.models.RolUsuario;
import co.edu.uniajc.agrovalle.repositories.CosechaRepository;
import co.edu.uniajc.agrovalle.repositories.FincaRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Reglas de publicación, filtros y precios de cosechas. */
@Service
public class CosechaService {

  private final CosechaRepository repository;
  private final FincaRepository fincaRepository;
  private final AgricultorService agricultorService;

  /** Recibe los repositorios y el servicio de cuentas para gestionar las ofertas. */
  public CosechaService(CosechaRepository repository, FincaRepository fincaRepository,
      AgricultorService agricultorService) {
    this.repository = repository;
    this.fincaRepository = fincaRepository;
    this.agricultorService = agricultorService;
  }

  /** Comprueba el rol y la propiedad de la finca antes de publicar una oferta. */
  public Cosecha publicar(CosechaRequest request) {
    if (agricultorService.require(request.agricultorId()).rol() != RolUsuario.AGRICULTOR) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          "Solo un agricultor puede publicar cosechas");
    }
    var finca = fincaRepository.findById(request.fincaId()).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la finca"));
    if (finca.agricultorId() != request.agricultorId()) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          "La finca no pertenece al agricultor");
    }
    return repository.save(new Cosecha(0, request.agricultorId(), request.fincaId(),
        request.tipo().trim(), request.categoria().trim(), request.cantidadKg(),
        request.precioCopKg(), request.fechaCosecha(), true));
  }

  /** Filtra las ofertas activas por municipio y categoría cuando se especifican. */
  public List<Cosecha> listar(String municipio, String categoria) {
    return repository.stream()
        .filter(Cosecha::activa)
        .filter(item -> matchesMunicipio(item, municipio))
        .filter(item -> matches(item.categoria(), categoria))
        .toList();
  }

  /** Calcula la media de precios de ofertas activas del producto solicitado. */
  public PrecioPromedioResponse promedio(String producto) {
    List<Cosecha> cosechas = repository.stream()
        .filter(Cosecha::activa)
        .filter(item -> item.tipo().equalsIgnoreCase(producto.trim()))
        .toList();
    BigDecimal promedio = cosechas.stream()
        .map(Cosecha::precioCopKg)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    if (!cosechas.isEmpty()) {
      promedio = promedio.divide(BigDecimal.valueOf(cosechas.size()), 2,
          RoundingMode.HALF_UP);
    } else {
      promedio = null;
    }
    return new PrecioPromedioResponse(producto, promedio, cosechas.size(), "COP/kg");
  }

  /** Obtiene una oferta o responde con un error 404 si no existe. */
  public Cosecha require(long id) {
    Cosecha cosecha = repository.findById(id);
    if (cosecha == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la cosecha " + id);
    }
    return cosecha;
  }

  private boolean matchesMunicipio(Cosecha cosecha, String municipio) {
    if (municipio == null || municipio.isBlank()) {
      return true;
    }
    return fincaRepository.findById(cosecha.fincaId())
        .map(finca -> finca.municipio().equalsIgnoreCase(municipio.trim()))
        .orElse(false);
  }

  private boolean matches(String value, String filter) {
    return filter == null || filter.isBlank() || value.equalsIgnoreCase(filter.trim());
  }
}
