package co.edu.uniajc.agrovalle.controllers;

import co.edu.uniajc.agrovalle.models.NotificacionCosecha;
import co.edu.uniajc.agrovalle.security.UsuarioAutenticado;
import co.edu.uniajc.agrovalle.services.NotificacionCosechaListener;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Consulta de avisos generados mediante el patrón Observer. */
@RestController
@RequestMapping("/api/v1/notificaciones/cosechas")
public class NotificacionController {

  private final NotificacionCosechaListener listener;

  /** Recibe el observador que conserva los avisos del incremento. */
  public NotificacionController(NotificacionCosechaListener listener) {
    this.listener = listener;
  }

  /** Lista los avisos pertenecientes a la identidad del JWT. */
  @GetMapping
  public List<NotificacionCosecha> listar(
      @AuthenticationPrincipal UsuarioAutenticado usuario) {
    return listener.listar(usuario.id());
  }
}
