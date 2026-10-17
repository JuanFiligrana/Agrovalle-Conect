package co.edu.uniajc.agrovalle;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/** Pruebas de aceptación de las funcionalidades priorizadas del Sprint 1. */
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
@AutoConfigureMockMvc
class Sprint1EndpointsTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void demuestraFlujoCompletoDelSprint() throws Exception {
    long agricultorId = registrar("AGRICULTOR");
    long compradorId = registrar("COMPRADOR");
    String fincaJson = """
        {"agricultorId":%d,"nombre":"La Esperanza","municipio":"Cali",
        "descripcion":"Finca de prueba"}
        """.formatted(agricultorId);
    MvcResult fincaResult = mockMvc.perform(post("/api/v1/fincas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(fincaJson))
        .andExpect(status().isCreated())
        .andReturn();
    long fincaId = idFrom(fincaResult);

    String fecha = LocalDate.now().plusDays(1).toString();
    String cosechaJson = """
        {"agricultorId":%d,"fincaId":%d,"tipo":"Tomate",
        "categoria":"Frutas","cantidadKg":100,"precioCopKg":1200,
        "fechaCosecha":"%s"}
        """.formatted(agricultorId, fincaId, fecha);
    MvcResult cosechaResult = mockMvc.perform(post("/api/v1/productos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(cosechaJson))
        .andExpect(status().isCreated())
        .andReturn();
    long productoId = idFrom(cosechaResult);

    mockMvc.perform(get("/api/v1/productos")
            .param("municipio", "Cali")
            .param("categoria", "Frutas"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].tipo").value("Tomate"));

    mockMvc.perform(get("/api/v1/precios/promedio").param("producto", "Tomate"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.promedio").value(1200.0))
        .andExpect(jsonPath("$.cantidadTransacciones").value(1));

    String contactoJson = """
        {"compradorId":%d,"productoId":%d,"mensaje":"Deseo conocer la disponibilidad"}
        """.formatted(compradorId, productoId);
    mockMvc.perform(post("/api/v1/contacto/mensaje")
            .header("Authorization", "Bearer demo")
            .contentType(MediaType.APPLICATION_JSON)
            .content(contactoJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.estado").value("REGISTRADO"));
  }

  @Test
  void rechazaDuplicadosAdemasDeContactosSinAutorizacion() throws Exception {
    String correo = "duplicado-" + UUID.randomUUID() + "@agrovalle.test";
    String request = """
        {"nombre":"Persona de prueba","correo":"%s","contrasena":"secreto123",
        "cedula":"%s","ubicacionValle":"Cali","rol":"AGRICULTOR"}
        """.formatted(correo, UUID.randomUUID());
    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isCreated());
    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isConflict());

    mockMvc.perform(post("/api/v1/contacto/mensaje")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"compradorId\":1,\"productoId\":1,\"mensaje\":\"Hola\"}"))
        .andExpect(status().isUnauthorized());
  }

  private long registrar(String rol) throws Exception {
    String suffix = UUID.randomUUID().toString();
    String request = """
        {"nombre":"Persona %s","correo":"%s@agrovalle.test",
        "contrasena":"secreto123","cedula":"%s","ubicacionValle":"Cali",
        "rol":"%s"}
        """.formatted(suffix, suffix, suffix, rol);
    MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isCreated())
        .andReturn();
    return idFrom(result);
  }

  private long idFrom(MvcResult result) throws Exception {
    JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
    return body.get("id").asLong();
  }
}
