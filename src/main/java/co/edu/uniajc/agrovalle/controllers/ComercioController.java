package co.edu.uniajc.agrovalle.controllers;

import co.edu.uniajc.agrovalle.dto.ComercioRequest;
import co.edu.uniajc.agrovalle.models.Comercio;
import co.edu.uniajc.agrovalle.security.UsuarioAutenticado;
import co.edu.uniajc.agrovalle.services.ComercioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** API de inventario privado, reservas, pedidos, trazabilidad y consulta administrativa. */
@RestController
@RequestMapping("/api/v1")
public class ComercioController {
  private final ComercioService service;

  /** Conecta la API con los casos de uso de comercio. */
  public ComercioController(ComercioService service) {
    this.service = service;
  }

  /** Consulta las ofertas propias. */
  @GetMapping("/mis-ofertas")
  public List<Comercio.Inventario> ofertas(@AuthenticationPrincipal UsuarioAutenticado usuario) {
    return service.misOfertas(usuario);
  }

  /** Actualiza una oferta propia sin comprometer las reservas vigentes. */
  @PutMapping("/productos/{id}")
  public Comercio.Inventario actualizar(@PathVariable long id,
      @Valid @RequestBody ComercioRequest.ActualizarOferta solicitud,
      @AuthenticationPrincipal UsuarioAutenticado usuario) {
    return service.actualizar(id, solicitud, usuario);
  }

  /** Consulta o inicia el carrito privado del comprador. */
  @GetMapping("/carrito")
  public Comercio.CarritoDetalle carrito(@AuthenticationPrincipal UsuarioAutenticado usuario) {
    return service.carrito(usuario);
  }

  /** Reemplaza la cantidad reservada y renueva su plazo de quince minutos. */
  @PostMapping("/carrito/items")
  public Comercio.CarritoDetalle reservar(@Valid @RequestBody ComercioRequest.Reservar solicitud,
      @AuthenticationPrincipal UsuarioAutenticado usuario) {
    return service.reservar(solicitud, usuario);
  }

  /** Retira una reserva del carrito propio y libera su disponibilidad. */
  @DeleteMapping("/carrito/items/{id}")
  public Comercio.CarritoDetalle quitar(@PathVariable long id,
      @AuthenticationPrincipal UsuarioAutenticado usuario) {
    return service.quitar(id, usuario);
  }

  /** Confirma el carrito atómicamente y reconoce reintentos por clave. */
  @PostMapping("/ordenes")
  public ResponseEntity<Comercio.PedidoDetalle> confirmar(
      @Valid @RequestBody ComercioRequest.Confirmar solicitud,
      @RequestHeader(value = "Idempotency-Key", required = false) String clave,
      @AuthenticationPrincipal UsuarioAutenticado usuario) {
    var resultado = service.confirmar(solicitud.carritoId(), clave, usuario);
    return ResponseEntity.status(resultado.creada() ? 201 : 200).body(resultado.orden());
  }

  /** Lista exclusivamente los pedidos en los que participa la cuenta. */
  @GetMapping("/ordenes")
  public List<Comercio.Pedido> pedidos(@AuthenticationPrincipal UsuarioAutenticado usuario) {
    return service.pedidos(usuario);
  }

  /** Consulta las líneas y eventos de un pedido propio. */
  @GetMapping("/ordenes/{id}/trazabilidad")
  public Comercio.PedidoDetalle trazabilidad(@PathVariable long id,
      @AuthenticationPrincipal UsuarioAutenticado usuario) {
    return service.trazabilidad(id, usuario);
  }

  /** Cancela un pedido pendiente y devuelve existencias una sola vez. */
  @PostMapping("/ordenes/{id}/cancelacion")
  public Comercio.PedidoDetalle cancelar(@PathVariable long id,
      @AuthenticationPrincipal UsuarioAutenticado usuario) {
    return service.cancelar(id, usuario);
  }

  /** Solicita el siguiente estado válido del despacho. */
  @PatchMapping("/ordenes/{id}/estado")
  public Comercio.PedidoDetalle estado(@PathVariable long id,
      @Valid @RequestBody ComercioRequest.CambiarEstado solicitud,
      @AuthenticationPrincipal UsuarioAutenticado usuario) {
    return service.cambiarEstado(id, solicitud, usuario);
  }

  /** Consulta las notificaciones privadas de pedidos. */
  @GetMapping("/notificaciones/pedidos")
  public List<Comercio.Aviso> avisos(@AuthenticationPrincipal UsuarioAutenticado usuario) {
    return service.avisos(usuario);
  }

  /** Consulta indicadores exclusivamente con el rol administrativo vigente. */
  @GetMapping("/administracion/resumen")
  public Comercio.Resumen resumen(@AuthenticationPrincipal UsuarioAutenticado usuario) {
    return service.resumen(usuario);
  }
}
