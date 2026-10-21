package co.edu.uniajc.agrovalle.controllers;

import co.edu.uniajc.agrovalle.dto.FincaRequest;
import co.edu.uniajc.agrovalle.models.Finca;
import co.edu.uniajc.agrovalle.security.UsuarioAutenticado;
import co.edu.uniajc.agrovalle.services.FincaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints del modelo de fincas. */
@RestController
@RequestMapping("/api/v1/fincas")
public class FincaController {

  private final FincaService service;

  /** Conecta las solicitudes HTTP con el servicio de fincas. */
  public FincaController(FincaService service) {
    this.service = service;
  }

  /** Registra una finca asociada a un agricultor. */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Finca crear(@AuthenticationPrincipal UsuarioAutenticado usuario,
      @Valid @RequestBody FincaRequest request) {
    return service.crear(request, usuario);
  }

  /** Lista las fincas del agricultor autenticado. */
  @GetMapping
  public List<Finca> listar(@AuthenticationPrincipal UsuarioAutenticado usuario) {
    return service.listarPropias(usuario);
  }
}
