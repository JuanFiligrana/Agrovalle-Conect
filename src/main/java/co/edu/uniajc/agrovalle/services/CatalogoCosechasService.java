package co.edu.uniajc.agrovalle.services;

import co.edu.uniajc.agrovalle.dto.OfertaCatalogoResponse;
import co.edu.uniajc.agrovalle.models.Agricultor;
import co.edu.uniajc.agrovalle.models.Cosecha;
import co.edu.uniajc.agrovalle.models.Finca;
import co.edu.uniajc.agrovalle.ports.FincaPort;
import co.edu.uniajc.agrovalle.ports.UsuarioPort;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Construye respuestas públicas del catálogo sin exponer datos privados de las cuentas. */
@Service
public class CatalogoCosechasService {

  private final CosechaService cosechas;
  private final FincaPort fincas;
  private final UsuarioPort usuarios;

  /** Recibe los casos de uso y puertos necesarios para enriquecer una oferta. */
  public CatalogoCosechasService(CosechaService cosechas, FincaPort fincas,
      UsuarioPort usuarios) {
    this.cosechas = cosechas;
    this.fincas = fincas;
    this.usuarios = usuarios;
  }

  /** Lista las ofertas activas con los datos de origen que necesita la interfaz web. */
  @Transactional(readOnly = true)
  public List<OfertaCatalogoResponse> listar(String municipio, String categoria) {
    return cosechas.listar(municipio, categoria).stream().map(this::toResponse).toList();
  }

  private OfertaCatalogoResponse toResponse(Cosecha cosecha) {
    Finca finca = fincas.findById(cosecha.fincaId())
        .orElseThrow(() -> new IllegalStateException("La cosecha no tiene una finca válida"));
    Agricultor agricultor = usuarios.findById(cosecha.agricultorId())
        .orElseThrow(() -> new IllegalStateException("La cosecha no tiene un agricultor válido"));
    return new OfertaCatalogoResponse(cosecha.id(), cosecha.agricultorId(),
        agricultor.nombre(), cosecha.fincaId(), finca.nombre(), finca.municipio(),
        cosecha.tipo(), cosecha.categoria(), cosecha.cantidadKg(),
        cosecha.precioCopKg(), cosecha.fechaCosecha(), cosecha.activa());
  }
}
