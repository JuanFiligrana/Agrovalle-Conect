package co.edu.uniajc.agrovalle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

/** Transacciones reales en conexiones distintas: nunca ejecuta dentro de rollback de test. */
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
@AutoConfigureMockMvc
class ComercioConcurrencyTests extends ComercioTestSupport {
  @Test
  void dosCompradoresNoPuedenReservarElMismoInventarioCompleto() throws Exception {
    final Cuenta uno = cuenta("COMPRADOR");
    final Cuenta dos = cuenta("COMPRADOR");
    final long oferta = oferta(cuenta("AGRICULTOR"), "10", "1000");
    List<Integer> estados = simultaneas(
        () -> pedir(post("/api/v1/carrito/items"), uno,
            Map.of("cosechaId", oferta, "cantidadKg", 8)).getResponse().getStatus(),
        () -> pedir(post("/api/v1/carrito/items"), dos,
            Map.of("cosechaId", oferta, "cantidadKg", 8)).getResponse().getStatus());
    assertThat(estados).containsExactlyInAnyOrder(200, 409);
    assertThat(jdbc.queryForObject(
        "SELECT SUM(cantidad_kg) FROM reservas WHERE cosecha_id = ? AND activa = TRUE",
        BigDecimal.class, oferta)).isEqualByComparingTo("8");
    assertThat(jdbc.queryForObject("SELECT cantidad_kg FROM cosechas WHERE id = ?",
        BigDecimal.class, oferta)).isEqualByComparingTo("10");
  }

  @Test
  void confirmacionesYcancelacionesConcurrentesAfectanElInventarioUnaVez() throws Exception {
    final Cuenta comprador = cuenta("COMPRADOR");
    final long oferta = oferta(cuenta("AGRICULTOR"), "10", "1000");
    final long carrito = cuerpo(pedir(post("/api/v1/carrito/items"), comprador,
        Map.of("cosechaId", oferta, "cantidadKg", 4))).get("id").asLong();
    Callable<Integer> confirmar = () -> pedir(
        post("/api/v1/ordenes").header("Idempotency-Key", "clave-concurrente"),
        comprador, Map.of("carritoId", carrito)).getResponse().getStatus();
    assertThat(simultaneas(confirmar, confirmar)).containsExactlyInAnyOrder(201, 200);
    assertThat(jdbc.queryForObject("SELECT cantidad_kg FROM cosechas WHERE id = ?",
        BigDecimal.class, oferta)).isEqualByComparingTo("6");
    final long pedido = jdbc.queryForObject("SELECT id FROM pedidos WHERE carrito_id = ?",
        Long.class, carrito);
    Callable<Integer> cancelar = () -> pedir(
        post("/api/v1/ordenes/" + pedido + "/cancelacion"), comprador, null)
        .getResponse().getStatus();
    assertThat(simultaneas(cancelar, cancelar)).containsExactly(200, 200);
    assertThat(jdbc.queryForObject("SELECT cantidad_kg FROM cosechas WHERE id = ?",
        BigDecimal.class, oferta)).isEqualByComparingTo("10");
    assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM eventos_pedido WHERE pedido_id = ?",
        Long.class, pedido)).isEqualTo(2);
  }

  private List<Integer> simultaneas(Callable<Integer> primera, Callable<Integer> segunda)
      throws Exception {
    var ejecutor = Executors.newFixedThreadPool(2);
    try {
      final var listos = new CountDownLatch(2);
      final var inicio = new CountDownLatch(1);
      var uno = ejecutor.submit(() -> ejecutar(primera, listos, inicio));
      var dos = ejecutor.submit(() -> ejecutar(segunda, listos, inicio));
      assertThat(listos.await(10, TimeUnit.SECONDS)).isTrue();
      inicio.countDown();
      return List.of(uno.get(30, TimeUnit.SECONDS), dos.get(30, TimeUnit.SECONDS));
    } finally {
      ejecutor.shutdownNow();
      assertThat(ejecutor.awaitTermination(10, TimeUnit.SECONDS)).isTrue();
    }
  }

  private int ejecutar(Callable<Integer> tarea, CountDownLatch listos, CountDownLatch inicio)
      throws Exception {
    listos.countDown();
    if (!inicio.await(10, TimeUnit.SECONDS)) {
      throw new IllegalStateException("No arrancaron ambas solicitudes");
    }
    return tarea.call();
  }
}
