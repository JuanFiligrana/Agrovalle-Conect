package co.edu.uniajc.agrovalle.controllers;

import co.edu.uniajc.agrovalle.dto.CosechaRequest;
import co.edu.uniajc.agrovalle.dto.OfertaCatalogoResponse;
import co.edu.uniajc.agrovalle.models.Cosecha;
import co.edu.uniajc.agrovalle.security.UsuarioAutenticado;
import co.edu.uniajc.agrovalle.services.CatalogoCosechasService;
import co.edu.uniajc.agrovalle.services.CosechaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints de publicación y consulta de cosechas. */
@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

  private final CosechaService service;
  private final CatalogoCosechasService catalogo;

  /** Conecta la publicación y la consulta con el servicio de cosechas. */
  public ProductoController(CosechaService service, CatalogoCosechasService catalogo) {
    this.service = service;
    this.catalogo = catalogo;
  }

  /** Publica una cosecha asociada a una finca propia. */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Cosecha publicar(@AuthenticationPrincipal UsuarioAutenticado usuario,
      @Valid @RequestBody CosechaRequest request) {
    return service.publicar(request, usuario);
  }

  /** Consulta ofertas activas aplicando filtros opcionales. */
  @GetMapping
  public List<OfertaCatalogoResponse> listar(@RequestParam(required = false) String municipio,
      @RequestParam(required = false) String categoria) {
    return catalogo.listar(municipio, categoria);
  }

  /** Consulta individual pública para evaluar y contactar una oferta activa. */
  @GetMapping("/{id}")
  public OfertaCatalogoResponse detalle(@PathVariable long id) {
    return catalogo.detalle(id);
  }
}
