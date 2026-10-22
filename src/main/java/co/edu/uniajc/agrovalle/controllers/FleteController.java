package co.edu.uniajc.agrovalle.controllers;

import co.edu.uniajc.agrovalle.dto.FleteResponse;
import co.edu.uniajc.agrovalle.services.CalculadoraFleteService;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Endpoint de cálculo que demuestra el patrón Strategy. */
@RestController
@RequestMapping("/api/v1/fletes")
public class FleteController {

  private final CalculadoraFleteService service;

  /** Recibe el contexto que selecciona la estrategia de flete. */
  public FleteController(CalculadoraFleteService service) {
    this.service = service;
  }

  /** Calcula el flete para un municipio y un subtotal. */
  @GetMapping("/calculo")
  public FleteResponse calcular(@RequestParam String municipio,
      @RequestParam BigDecimal subtotal) {
    return service.calcular(municipio, subtotal);
  }
}
