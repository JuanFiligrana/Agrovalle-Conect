package co.edu.uniajc.agrovalle.controllers;

import co.edu.uniajc.agrovalle.dto.CosechaRequest;
import co.edu.uniajc.agrovalle.models.Cosecha;
import co.edu.uniajc.agrovalle.services.CosechaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
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

  /** Conecta la publicación y la consulta con el servicio de cosechas. */
  public ProductoController(CosechaService service) {
    this.service = service;
  }

  /** Publica una cosecha asociada a una finca propia. */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Cosecha publicar(@Valid @RequestBody CosechaRequest request) {
    return service.publicar(request);
  }

  /** Consulta ofertas activas aplicando filtros opcionales. */
  @GetMapping
  public List<Cosecha> listar(@RequestParam(required = false) String municipio,
      @RequestParam(required = false) String categoria) {
    return service.listar(municipio, categoria);
  }
}
