package co.edu.uniajc.agrovalle;

import co.edu.uniajc.agrovalle.ports.UsuarioPort;
import co.edu.uniajc.agrovalle.security.TokenJwtService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/** Datos aislados exclusivamente de pruebas, sin usuarios ni permisos preconfigurados en producción. */
abstract class ComercioTestSupport {
  @Autowired
  protected MockMvc mvc;
  @Autowired
  protected JdbcTemplate jdbc;
  @Autowired
  protected ObjectMapper json;
  @Autowired
  protected UsuarioPort usuarios;
  @Autowired
  protected TokenJwtService tokens;

  protected Cuenta cuenta(String rol) {
    String sufijo = UUID.randomUUID().toString();
    long id = insertar("usuarios", Map.of("nombre", "Prueba comercio", "correo",
        sufijo + "@example.test", "cedula", sufijo, "municipio", "Vijes", "rol", rol,
        "contrasena_hash", "solo-fixture-no-utilizable"));
    return new Cuenta(id, "Bearer " + tokens.generar(usuarios.findById(id).orElseThrow()));
  }

  protected long oferta(Cuenta agricultor, String cantidad, String precio) {
    long finca = insertar("fincas", Map.of("agricultor_id", agricultor.id(), "nombre",
        "Finca comercio", "municipio", "Vijes"));
    long categoria = insertar("categorias", Map.of("nombre", UUID.randomUUID().toString()));
    long producto = insertar("productos", Map.of("categoria_id", categoria, "nombre",
        "Cafe-" + UUID.randomUUID()));
    return insertar("cosechas", Map.of("finca_id", finca, "producto_id", producto,
        "cantidad_kg", new java.math.BigDecimal(cantidad),
        "precio_cop_kg", new java.math.BigDecimal(precio),
        "fecha_cosecha", LocalDate.now(), "activa", true));
  }

  protected long insertar(String tabla, Map<String, Object> valores) {
    return new SimpleJdbcInsert(jdbc).withTableName(tabla).usingGeneratedKeyColumns("id")
        .usingColumns(valores.keySet().toArray(String[]::new))
        .executeAndReturnKey(valores).longValue();
  }

  protected MvcResult pedir(MockHttpServletRequestBuilder solicitud, Cuenta cuenta,
      Object cuerpo) throws Exception {
    solicitud.header("Authorization", cuenta.bearer());
    if (cuerpo != null) {
      solicitud.contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(cuerpo));
    }
    return mvc.perform(solicitud).andReturn();
  }

  protected JsonNode cuerpo(MvcResult respuesta) throws Exception {
    return json.readTree(respuesta.getResponse().getContentAsString());
  }

  protected record Cuenta(long id, String bearer) {}
}
