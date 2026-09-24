package co.edu.uniajc.agrovalle.controllers;

import co.edu.uniajc.agrovalle.dto.PrecioPromedioResponse;
import co.edu.uniajc.agrovalle.services.CosechaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** Endpoint de consulta del promedio regional. */
@RestController
@RequestMapping("/api/v1/precios")
public class PrecioController {

  private final CosechaService service;

  /** Conecta la consulta de precios con el servicio de cosechas. */
  public PrecioController(CosechaService service) {
    this.service = service;
  }

  /** Calcula el promedio de las ofertas activas de un producto. */
  @GetMapping("/promedio")
  @ResponseStatus(HttpStatus.OK)
  public PrecioPromedioResponse promedio(@RequestParam String producto) {
    if (producto.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "El producto es obligatorio");
    }
    return service.promedio(producto);
  }
}
