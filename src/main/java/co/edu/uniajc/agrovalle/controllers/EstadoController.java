package co.edu.uniajc.agrovalle.controllers;

import co.edu.uniajc.agrovalle.models.EstadoSistema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Expone el estado de la base tecnica del Sprint 1. */
@RestController
@RequestMapping("/api/v1/estado")
public class EstadoController {

  /**
   * Confirma que el servicio web responde.
   *
   * @return contrato JSON del estado del servicio
   */
  @GetMapping
  public EstadoSistema consultarEstado() {
    return new EstadoSistema("AgroValle Connect", "OK", "Sprint 1");
  }
}
