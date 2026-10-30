package co.edu.uniajc.agrovalle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.uniajc.agrovalle.ports.PrecioPort;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

/** Aceptación HTTP de HU03, HU09 y HU11–15 con persistencia H2 y PostgreSQL en CI. */
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
@AutoConfigureMockMvc
@Transactional
class ComercioAcceptanceTests extends ComercioTestSupport {
  @Autowired
  private PrecioPort precios;

  @Test
  void reservaActualizaConfirmaSinDuplicarYconservaPrecio() throws Exception {
    final Cuenta agricultor = cuenta("AGRICULTOR");
    final Cuenta comprador = cuenta("COMPRADOR");
    long oferta = oferta(agricultor, "20", "1000");
    var reserva = reservar(comprador, oferta, "5");
    assertThat(reserva.getResponse().getStatus()).isEqualTo(200);
    final long carrito = cuerpo(reserva).get("id").asLong();
    assertThat(cuerpo(reserva).get("total").decimalValue()).isEqualByComparingTo("5000");
    OffsetDateTime vence = OffsetDateTime.parse(
        cuerpo(reserva).get("items").get(0).get("venceEn").asText());
    assertThat(vence).isAfter(OffsetDateTime.now().plusMinutes(14));
    var inventario = pedir(put("/api/v1/productos/" + oferta), agricultor,
        Map.of("cantidad_fisica_kg", 18, "precio_cop_kg", 2000));
    assertThat(inventario.getResponse().getStatus()).isEqualTo(200);
    assertThat(cuerpo(inventario).get("cantidadDisponibleKg").decimalValue())
        .isEqualByComparingTo("13");
    assertThat(pedir(put("/api/v1/productos/" + oferta), agricultor,
        Map.of("cantidadFisicaKg", 4, "precioCopKg", 2000)).getResponse().getStatus())
        .isEqualTo(409);
    var confirmado = confirmar(comprador, carrito, "pedido-prueba-001");
    assertThat(confirmado.getResponse().getStatus()).isEqualTo(201);
    final long pedido = cuerpo(confirmado).get("pedido").get("id").asLong();
    assertThat(cuerpo(confirmado).get("pedido").get("total").decimalValue())
        .isEqualByComparingTo("5000");
    var repetido = confirmar(comprador, carrito, "pedido-prueba-001");
    assertThat(repetido.getResponse().getStatus()).isEqualTo(200);
    assertThat(cuerpo(repetido).get("pedido").get("id").asLong()).isEqualTo(pedido);
    assertThat(fisica(oferta)).isEqualByComparingTo("13");
    assertThat(cuerpo(pedir(get("/api/v1/carrito"), comprador, null)).get("items")).isEmpty();
    assertThat(confirmar(comprador, carrito + 999, "pedido-prueba-001")
        .getResponse().getStatus()).isEqualTo(409);
    assertThat(confirmar(comprador, carrito, "otra-clave-001")
        .getResponse().getStatus()).isEqualTo(409);
    assertThat(cuerpo(pedir(get("/api/v1/notificaciones/pedidos"), agricultor, null)).size())
        .isEqualTo(1);
  }

  @Test
  void reservaVencidaLiberaDisponibilidadPeroNoPermiteConfirmar() throws Exception {
    final Cuenta comprador = cuenta("COMPRADOR");
    long oferta = oferta(cuenta("AGRICULTOR"), "10", "2500");
    final long carrito = cuerpo(reservar(comprador, oferta, "10")).get("id").asLong();
    jdbc.update("UPDATE reservas SET vence_en = ? WHERE carrito_id = ?",
        OffsetDateTime.now().minusSeconds(1), carrito);
    mvc.perform(get("/api/v1/productos/" + oferta)).andExpect(status().isOk())
        .andExpect(jsonPath("$.cantidadKg").value(10));
    assertThat(confirmar(comprador, carrito, "reserva-vencida").getResponse().getStatus())
        .isEqualTo(409);
    assertThat(fisica(oferta)).isEqualByComparingTo("10");
    assertThat(reservar(comprador, oferta, "10").getResponse().getStatus()).isEqualTo(200);
    assertThat(confirmar(comprador, carrito, "reserva-renovada").getResponse().getStatus())
        .isEqualTo(201);
    assertThat(fisica(oferta)).isEqualByComparingTo("0");
  }

  @Test
  void cancelarDosVecesDevuelveInventarioUnaSolaVez() throws Exception {
    final Cuenta agricultor = cuenta("AGRICULTOR");
    final Cuenta comprador = cuenta("COMPRADOR");
    long oferta = oferta(agricultor, "10", "1000");
    final long carrito = cuerpo(reservar(comprador, oferta, "3")).get("id").asLong();
    long pedido = cuerpo(confirmar(comprador, carrito, "cancelar-orden"))
        .get("pedido").get("id").asLong();
    for (int intento = 0; intento < 2; intento++) {
      var cancelado = pedir(post("/api/v1/ordenes/" + pedido + "/cancelacion"), comprador, null);
      assertThat(cancelado.getResponse().getStatus()).isEqualTo(200);
      assertThat(cuerpo(cancelado).get("pedido").get("estado").asText()).isEqualTo("CANCELADA");
    }
    assertThat(fisica(oferta)).isEqualByComparingTo("10");
    assertThat(cuerpo(pedir(get("/api/v1/ordenes/" + pedido + "/trazabilidad"),
        comprador, null)).get("eventos").size()).isEqualTo(2);
    assertThat(estado(agricultor, pedido, "EN_CAMINO", null).getResponse().getStatus())
        .isEqualTo(409);
  }

  @Test
  void entregaGeneraTrazabilidadAvisosYprecioHistorico() throws Exception {
    final Cuenta agricultor = cuenta("AGRICULTOR");
    final Cuenta comprador = cuenta("COMPRADOR");
    final long oferta = oferta(agricultor, "10", "1234.50");
    long carrito = cuerpo(reservar(comprador, oferta, "3")).get("id").asLong();
    final long pedido = cuerpo(confirmar(comprador, carrito, "entregar-orden"))
        .get("pedido").get("id").asLong();
    assertThat(estado(agricultor, pedido, "ENTREGADO", null).getResponse().getStatus())
        .isEqualTo(409);
    assertThat(estado(agricultor, pedido, "LISTO_PARA_DESPACHO", null)
        .getResponse().getStatus()).isEqualTo(400);
    assertThat(estado(agricultor, pedido, "LISTO_PARA_DESPACHO",
        OffsetDateTime.now().minusDays(1)).getResponse().getStatus()).isEqualTo(400);
    assertThat(estado(agricultor, pedido, "LISTO_PARA_DESPACHO",
        OffsetDateTime.now().plusDays(1)).getResponse().getStatus()).isEqualTo(200);
    assertThat(pedir(post("/api/v1/ordenes/" + pedido + "/cancelacion"), comprador, null)
        .getResponse().getStatus()).isEqualTo(409);
    assertThat(estado(agricultor, pedido, "EN_CAMINO", null).getResponse().getStatus())
        .isEqualTo(200);
    assertThat(estado(agricultor, pedido, "ENTREGADO", null).getResponse().getStatus())
        .isEqualTo(200);
    assertThat(estado(agricultor, pedido, "ENTREGADO", null).getResponse().getStatus())
        .isEqualTo(409);
    var detalle = cuerpo(pedir(get("/api/v1/ordenes/" + pedido + "/trazabilidad"),
        comprador, null));
    assertThat(detalle.get("eventos").size()).isEqualTo(4);
    assertThat(detalle.get("pedido").get("ruta").asText()).isEqualTo("Finca a Vijes");
    assertThat(cuerpo(pedir(get("/api/v1/notificaciones/pedidos"), comprador, null)).size())
        .isEqualTo(3);
    String producto = detalle.get("items").get(0).get("producto").asText();
    mvc.perform(get("/api/v1/precios/promedio").param("producto", producto))
        .andExpect(status().isOk()).andExpect(jsonPath("$.promedio").value(1234.50))
        .andExpect(jsonPath("$.cantidad_transacciones").value(1));
    assertThat(cuerpo(pedir(get("/api/v1/ordenes"), agricultor, null)).size()).isEqualTo(1);
    assertThat(cuerpo(pedir(get("/api/v1/ordenes"), comprador, null)).size()).isEqualTo(1);
  }

  @Test
  void prohibeAccesoAjenoRolesIncorrectosYcarritosMezclados() throws Exception {
    final Cuenta agricultor = cuenta("AGRICULTOR");
    final Cuenta comprador = cuenta("COMPRADOR");
    final Cuenta otro = cuenta("COMPRADOR");
    final Cuenta otroAgricultor = cuenta("AGRICULTOR");
    long oferta = oferta(agricultor, "10", "1000");
    final long carrito = cuerpo(reservar(comprador, oferta, "3")).get("id").asLong();
    long otraOferta = oferta(otroAgricultor, "10", "1000");
    assertThat(reservar(comprador, otraOferta, "1").getResponse().getStatus()).isEqualTo(409);
    assertThat(reservar(agricultor, oferta, "1").getResponse().getStatus()).isEqualTo(403);
    assertThat(reservar(otro, oferta, "8").getResponse().getStatus()).isEqualTo(409);
    assertThat(confirmar(otro, carrito, "carrito-ajeno").getResponse().getStatus()).isEqualTo(403);
    assertThat(pedir(put("/api/v1/productos/" + oferta), otroAgricultor,
        Map.of("cantidadFisicaKg", 9, "precioCopKg", 1000)).getResponse().getStatus())
        .isEqualTo(403);
    final long pedido = cuerpo(confirmar(comprador, carrito, "pedido-privado"))
        .get("pedido").get("id").asLong();
    assertThat(pedir(get("/api/v1/ordenes/" + pedido + "/trazabilidad"), otro, null)
        .getResponse().getStatus()).isEqualTo(403);
    assertThat(pedir(post("/api/v1/ordenes/" + pedido + "/cancelacion"), otro, null)
        .getResponse().getStatus()).isEqualTo(403);
    assertThat(estado(otroAgricultor, pedido, "LISTO_PARA_DESPACHO",
        OffsetDateTime.now().plusDays(1)).getResponse().getStatus()).isEqualTo(403);
    assertThat(estado(comprador, pedido, "EN_CAMINO", null).getResponse().getStatus())
        .isEqualTo(403);
    assertThat(cuerpo(pedir(get("/api/v1/ordenes"), otro, null))).isEmpty();
    assertThat(pedir(get("/api/v1/ordenes/9223372036854775807/trazabilidad"), comprador, null)
        .getResponse().getStatus()).isEqualTo(404);
  }

  @Test
  void editarReservaReemplazaCantidadYquitarLaLibera() throws Exception {
    final Cuenta comprador = cuenta("COMPRADOR");
    long oferta = oferta(cuenta("AGRICULTOR"), "10", "1000");
    reservar(comprador, oferta, "9");
    assertThat(reservar(comprador, oferta, "10").getResponse().getStatus()).isEqualTo(200);
    var carrito = pedir(delete("/api/v1/carrito/items/" + oferta), comprador, null);
    assertThat(carrito.getResponse().getStatus()).isEqualTo(200);
    assertThat(cuerpo(carrito).get("items")).isEmpty();
    assertThat(confirmar(comprador, cuerpo(carrito).get("id").asLong(), "vacio-carrito")
        .getResponse().getStatus()).isEqualTo(409);
    jdbc.update("UPDATE cosechas SET activa = FALSE WHERE id = ?", oferta);
    assertThat(reservar(comprador, oferta, "1").getResponse().getStatus()).isEqualTo(409);
  }

  @Test
  void validaCantidadesClavesYCierraAdministracionAlPublico() throws Exception {
    final Cuenta comprador = cuenta("COMPRADOR");
    long oferta = oferta(cuenta("AGRICULTOR"), "10", "1000");
    for (String cantidad : new String[] {"0", "-1", "0.0001", "1000000000000"}) {
      assertThat(reservar(comprador, oferta, cantidad).getResponse().getStatus()).isEqualTo(400);
    }
    assertThat(confirmar(comprador, 1, "corta").getResponse().getStatus()).isEqualTo(400);
    assertThat(pedir(post("/api/v1/ordenes"), comprador, Map.of("carrito_id", 1))
        .getResponse().getStatus()).isEqualTo(400);
    for (String ruta : new String[] {"/api/v1/carrito", "/api/v1/ordenes",
        "/api/v1/mis-ofertas", "/api/v1/notificaciones/pedidos",
        "/api/v1/administracion/resumen"}) {
      mvc.perform(get(ruta)).andExpect(status().isUnauthorized());
      mvc.perform(get(ruta).header("Authorization", "Bearer inventado"))
          .andExpect(status().isUnauthorized());
    }
    assertThat(pedir(get("/api/v1/administracion/resumen"), comprador, null)
        .getResponse().getStatus()).isEqualTo(403);
    var resumen = pedir(get("/api/v1/administracion/resumen"), cuenta("ADMINISTRADOR"), null);
    assertThat(resumen.getResponse().getStatus()).isEqualTo(200);
    assertThat(cuerpo(resumen).has("contrasenaHash")).isFalse();
    mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
        .content(json.writeValueAsString(Map.of("nombre", "Sin privilegios", "correo",
            "admin-" + java.util.UUID.randomUUID() + "@example.test", "cedula",
            java.util.UUID.randomUUID().toString(), "contrasena", "secreto123",
            "ubicacionValle", "Cali", "rol", "ADMINISTRADOR"))))
        .andExpect(status().isBadRequest());
  }

  @Test
  void mediaDeCincuentaVentasYlimitesDeVentanaHistorica() throws Exception {
    long cosecha = oferta(cuenta("AGRICULTOR"), "20", "99999");
    final long productoId = jdbc.queryForObject(
        "SELECT producto_id FROM cosechas WHERE id = ?", Long.class, cosecha);
    final String producto = jdbc.queryForObject(
        "SELECT nombre FROM productos WHERE id = ?", String.class, productoId);
    final var hasta = OffsetDateTime.now();
    for (int index = 0; index < 50; index++) {
      transaccion(productoId, producto, index < 25 ? "1000" : "2000",
          "COMPLETADA", hasta.minusHours(1));
    }
    transaccion(productoId, producto, "99999", "COMPLETADA", hasta.minusHours(25));
    transaccion(productoId, producto, "99999", "PENDIENTE", hasta.minusHours(1));
    transaccion(productoId, producto, "99999", "COMPLETADA", hasta.plusHours(1));
    transaccion(productoId, "Otro producto", "99999", "COMPLETADA", hasta.minusHours(1));
    mvc.perform(get("/api/v1/precios/promedio").param("producto", " " + producto.toUpperCase()
            + " ")).andExpect(status().isOk()).andExpect(jsonPath("$.promedio").value(1500.00))
        .andExpect(jsonPath("$.cantidadTransacciones").value(50));
    transaccion(productoId, producto, "1000", "COMPLETADA", hasta.minusHours(24));
    transaccion(productoId, producto, "2000", "COMPLETADA", hasta);
    assertThat(precios.preciosFinalizados(producto, hasta.minusHours(24), hasta)).hasSize(52);
    mvc.perform(get("/api/v1/precios/promedio").param("producto", "Inexistente-historial"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.promedio").value(nullValue()))
        .andExpect(jsonPath("$.cantidadTransacciones").value(0));
  }

  private void transaccion(long productoId, String producto, String precio, String estado,
      OffsetDateTime fecha) {
    insertar("transacciones", Map.of("producto_id", productoId, "producto", producto,
        "precio_cop_kg", new BigDecimal(precio), "estado", estado, "finalizada_en", fecha));
  }

  private org.springframework.test.web.servlet.MvcResult reservar(Cuenta comprador,
      long oferta, String cantidad) throws Exception {
    return pedir(post("/api/v1/carrito/items"), comprador,
        Map.of("id_producto", oferta, "cantidad_kg", new BigDecimal(cantidad)));
  }

  private org.springframework.test.web.servlet.MvcResult confirmar(Cuenta comprador,
      long carrito, String clave) throws Exception {
    return pedir(post("/api/v1/ordenes").header("Idempotency-Key", clave), comprador,
        Map.of("carrito_id", carrito));
  }

  private org.springframework.test.web.servlet.MvcResult estado(Cuenta agricultor,
      long pedido, String estado, OffsetDateTime programada) throws Exception {
    Map<String, Object> datos = new java.util.HashMap<>();
    datos.put("estado", estado);
    if (programada != null) {
      datos.put("fecha_programada", programada);
      datos.put("ruta", "Finca a Vijes");
    }
    return pedir(patch("/api/v1/ordenes/" + pedido + "/estado"), agricultor, datos);
  }

  private BigDecimal fisica(long oferta) {
    return jdbc.queryForObject("SELECT cantidad_kg FROM cosechas WHERE id = ?",
        BigDecimal.class, oferta);
  }
}
