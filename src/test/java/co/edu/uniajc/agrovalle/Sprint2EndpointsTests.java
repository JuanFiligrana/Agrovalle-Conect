package co.edu.uniajc.agrovalle;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/** Aceptación REST de HU-02 y HU-04 con persistencia y JWT. */
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
@AutoConfigureMockMvc
class Sprint2EndpointsTests {

  private static final ZoneId ZONA_COLOMBIA = ZoneId.of("America/Bogota");

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void publicaUnaCosechaPersistenteYpermiteFiltrarla() throws Exception {
    CuentaPrueba agricultor = registrar("AGRICULTOR");
    long fincaId = crearFinca(agricultor, "Dagua");
    String body = cosechaJson(fincaId, "Mango", "Frutas",
        LocalDate.now(ZONA_COLOMBIA).plusDays(1));

    mockMvc.perform(post("/api/v1/productos")
            .header(HttpHeaders.AUTHORIZATION, bearer(agricultor))
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.agricultorId").value(agricultor.id()))
        .andExpect(jsonPath("$.fincaId").value(fincaId))
        .andExpect(jsonPath("$.activa").value(true));

    mockMvc.perform(get("/api/v1/productos")
            .param("municipio", "Dagua")
            .param("categoria", "Frutas"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].tipo").value("Mango"));

    mockMvc.perform(get("/api/v1/notificaciones/cosechas")
            .header(HttpHeaders.AUTHORIZATION, bearer(agricultor)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].cosechaId").isNumber())
        .andExpect(jsonPath("$[0].mensaje").value(
            "Cosecha de Mango publicada correctamente"));
  }

  @Test
  void exigeJwtDeAgricultorParaPublicar() throws Exception {
    CuentaPrueba comprador = registrar("COMPRADOR");
    String body = cosechaJson(1, "Mango", "Frutas",
        LocalDate.now(ZONA_COLOMBIA).plusDays(1));

    mockMvc.perform(post("/api/v1/productos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(post("/api/v1/productos")
            .header(HttpHeaders.AUTHORIZATION, bearer(comprador))
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isForbidden());
  }

  @Test
  void rechazaFincaAjenaFechaPasadaCategoriaInvalida() throws Exception {
    CuentaPrueba propietario = registrar("AGRICULTOR");
    CuentaPrueba intruso = registrar("AGRICULTOR");
    long fincaId = crearFinca(propietario, "Cali");

    mockMvc.perform(post("/api/v1/productos")
            .header(HttpHeaders.AUTHORIZATION, bearer(intruso))
            .contentType(MediaType.APPLICATION_JSON)
            .content(cosechaJson(fincaId, "Cafe", "Granos",
                LocalDate.now(ZONA_COLOMBIA).plusDays(1))))
        .andExpect(status().isForbidden());

    mockMvc.perform(post("/api/v1/productos")
            .header(HttpHeaders.AUTHORIZATION, bearer(propietario))
            .contentType(MediaType.APPLICATION_JSON)
            .content(cosechaJson(fincaId, "Cafe", "Granos",
                LocalDate.now(ZONA_COLOMBIA).minusDays(1))))
        .andExpect(status().isBadRequest());

    mockMvc.perform(get("/api/v1/productos").param("categoria", "Inexistente"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("categoria: no pertenece al catálogo"));
  }

  @Test
  void calculaFleteConLaEstrategiaDeLaZona() throws Exception {
    mockMvc.perform(get("/api/v1/fletes/calculo")
            .param("municipio", "Dagua")
            .param("subtotal", "100000"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.municipio").value("Dagua"))
        .andExpect(jsonPath("$.estrategia").value("INTERMEDIO"))
        .andExpect(jsonPath("$.valorCop").value(18000.0));

    mockMvc.perform(get("/api/v1/fletes/calculo")
            .param("municipio", "Bogota")
            .param("subtotal", "100000"))
        .andExpect(status().isBadRequest());
  }

  private CuentaPrueba registrar(String rol) throws Exception {
    String suffix = UUID.randomUUID().toString();
    String correo = suffix + "@agrovalle.test";
    String body = """
        {"nombre":"Persona %s","correo":"%s","contrasena":"secreto123",
        "cedula":"%s","ubicacionValle":"Cali","rol":"%s"}
        """.formatted(suffix, correo, suffix, rol);
    MvcResult registro = mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isCreated())
        .andReturn();
    String loginBody = """
        {"correo":"%s","contrasena":"secreto123"}
        """.formatted(correo);
    MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginBody))
        .andExpect(status().isOk())
        .andReturn();
    return new CuentaPrueba(idFrom(registro), json(login).get("accessToken").asText());
  }

  private long crearFinca(CuentaPrueba cuenta, String municipio) throws Exception {
    String body = """
        {"nombre":"Finca Sprint 2","municipio":"%s","descripcion":"Prueba"}
        """.formatted(municipio);
    MvcResult result = mockMvc.perform(post("/api/v1/fincas")
            .header(HttpHeaders.AUTHORIZATION, bearer(cuenta))
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isCreated())
        .andReturn();
    return idFrom(result);
  }

  private String cosechaJson(long fincaId, String tipo, String categoria, LocalDate fecha) {
    return """
        {"fincaId":%d,"tipo":"%s","categoria":"%s","cantidadKg":100,
        "precioCopKg":3500,"fechaCosecha":"%s"}
        """.formatted(fincaId, tipo, categoria, fecha);
  }

  private String bearer(CuentaPrueba cuenta) {
    return "Bearer " + cuenta.token();
  }

  private long idFrom(MvcResult result) throws Exception {
    return json(result).get("id").asLong();
  }

  private JsonNode json(MvcResult result) throws Exception {
    return objectMapper.readTree(result.getResponse().getContentAsString());
  }

  private record CuentaPrueba(long id, String token) {}
}
