package co.edu.uniajc.agrovalle;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

/** Prueba HTTP contra PostgreSQL que cierra y vuelve a iniciar la aplicación. */
@EnabledIfEnvironmentVariable(named = "AGROVALLE_TEST_POSTGRES", matches = "true")
class FlujoPostgresqlTests {

  private final ObjectMapper json = new ObjectMapper();
  private final HttpClient http = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(10)).build();
  private String base;

  @Test
  void conservaCuentasFincaOfertaYmensajesDespuesDeReiniciar() throws Exception {
    String unico = UUID.randomUUID().toString();
    String correoAgricultor = "agricultor-" + unico + "@example.invalid";
    String correoComprador = "comprador-" + unico + "@example.invalid";
    long fincaId;
    long ofertaId;
    long contactoId;
    try (var contexto = iniciar()) {
      configurarBase(contexto);
      try (var connection = contexto.getBean(DataSource.class).getConnection()) {
        assertThat(connection.getMetaData().getDatabaseProductName()).isEqualTo("PostgreSQL");
      }
      registrar(correoAgricultor, "AGRICULTOR");
      registrar(correoComprador, "COMPRADOR");
      String agricultor = login(correoAgricultor);
      String comprador = login(correoComprador);
      fincaId = solicitud("POST", "/api/v1/fincas", agricultor,
          Map.of("nombre", "Finca reinicio", "municipio", "Pradera",
              "descripcion", "Prueba HTTP PostgreSQL"), 201).get("id").asLong();
      ofertaId = solicitud("POST", "/api/v1/productos", agricultor,
          Map.of("fincaId", fincaId, "tipo", "Cosecha-reinicio-" + unico,
              "categoria", "Otros", "cantidadKg", 25, "precioCopKg", 4200,
              "fechaCosecha", LocalDate.now(ZoneId.of("America/Bogota"))
                  .plusDays(1).toString()), 201).get("id").asLong();
      contactoId = solicitud("POST", "/api/v1/contacto/mensaje", comprador,
          Map.of("productoId", ofertaId, "mensaje", "Mensaje que debe sobrevivir al reinicio"),
          200).get("id").asLong();
      comprobarRollback(contexto, comprador, ofertaId);
    }
    try (var contexto = iniciar()) {
      configurarBase(contexto);
      String agricultor = login(correoAgricultor);
      String comprador = login(correoComprador);
      JsonNode fincas = solicitud("GET", "/api/v1/fincas", agricultor, null, 200);
      assertThat(fincas.get(0).get("id").asLong()).isEqualTo(fincaId);
      JsonNode oferta = solicitud("GET", "/api/v1/productos/" + ofertaId, null, null, 200);
      assertThat(oferta.get("cantidadKg").asInt()).isEqualTo(25);
      JsonNode mensajes = solicitud("GET", "/api/v1/contacto/mensajes", agricultor, null, 200);
      assertThat(mensajes.size()).isEqualTo(1);
      assertThat(mensajes.get(0).get("id").asLong()).isEqualTo(contactoId);
      assertThat(mensajes.get(0).get("leida").asBoolean()).isFalse();
      assertThat(solicitud("GET", "/api/v1/contacto/mensajes", comprador, null, 200)
          .get(0).get("id").asLong()).isEqualTo(contactoId);
    }
  }

  private void comprobarRollback(ServletWebServerApplicationContext contexto,
      String comprador, long ofertaId) throws Exception {
    var jdbc = new JdbcTemplate(contexto.getBean(DataSource.class));
    final Long contactosAntes = jdbc.queryForObject(
        "SELECT count(*) FROM interacciones_contacto", Long.class);
    final Long notificacionesAntes = jdbc.queryForObject(
        "SELECT count(*) FROM notificaciones_contacto", Long.class);
    // Solo en la base desechable de CI: falla la segunda escritura de la transacción.
    jdbc.execute("ALTER TABLE notificaciones_contacto ADD CONSTRAINT prueba_fallo_notificacion "
        + "CHECK (contacto_id < 0) NOT VALID");
    try {
      JsonNode error = solicitud("POST", "/api/v1/contacto/mensaje", comprador,
          Map.of("productoId", ofertaId, "mensaje", "Debe revertirse completamente"), 409);
      assertThat(error.get("message").asText())
          .isEqualTo("La operación entra en conflicto con los datos registrados");
      assertThat(jdbc.queryForObject("SELECT count(*) FROM interacciones_contacto", Long.class))
          .isEqualTo(contactosAntes);
      assertThat(jdbc.queryForObject("SELECT count(*) FROM notificaciones_contacto", Long.class))
          .isEqualTo(notificacionesAntes);
    } finally {
      jdbc.execute("ALTER TABLE notificaciones_contacto DROP CONSTRAINT prueba_fallo_notificacion");
    }
  }

  private ServletWebServerApplicationContext iniciar() {
    return (ServletWebServerApplicationContext) SpringApplication.run(AgrovalleApplication.class,
        "--spring.profiles.active=ci-postgres", "--server.port=0");
  }

  private void configurarBase(ServletWebServerApplicationContext contexto) {
    base = "http://127.0.0.1:" + contexto.getWebServer().getPort();
  }

  private void registrar(String correo, String rol) throws Exception {
    solicitud("POST", "/api/v1/auth/register", null, Map.of("nombre", "Persona HTTP",
        "correo", correo, "cedula", UUID.randomUUID().toString(), "contrasena", "clave-prueba",
        "ubicacionValle", "Pradera", "rol", rol), 201);
  }

  private String login(String correo) throws Exception {
    return solicitud("POST", "/api/v1/auth/login", null,
        Map.of("correo", correo, "contrasena", "clave-prueba"), 200)
        .get("accessToken").asText();
  }

  private JsonNode solicitud(String metodo, String ruta, String token,
      Map<String, Object> body, int esperado) throws Exception {
    var request = HttpRequest.newBuilder(URI.create(base + ruta))
        .timeout(Duration.ofSeconds(20)).header("Accept", "application/json");
    if (token != null) {
      request.header("Authorization", "Bearer " + token);
    }
    if (body == null) {
      request.method(metodo, HttpRequest.BodyPublishers.noBody());
    } else {
      request.header("Content-Type", "application/json")
          .method(metodo, HttpRequest.BodyPublishers.ofString(json.writeValueAsString(body)));
    }
    var response = http.send(request.build(), HttpResponse.BodyHandlers.ofString());
    assertThat(response.statusCode()).as("%s %s", metodo, ruta).isEqualTo(esperado);
    return json.readTree(response.body());
  }
}
