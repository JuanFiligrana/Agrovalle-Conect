package co.edu.uniajc.agrovalle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.uniajc.agrovalle.repositories.jpa.FincaJpaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/** Escenarios BDD de HU-08 para registrar fincas persistentes y protegidas. */
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
@AutoConfigureMockMvc
class RegistroFincaAcceptanceTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private FincaJpaRepository fincas;

  @Test
  void dadoAgricultorAutenticadoCuandoRegistraFincaEntoncesPersisteComoPropietario()
      throws Exception {
    CuentaPrueba cuenta = crearCuentaAutenticada("AGRICULTOR");
    String request = """
        {"agricultorId":999999,"nombre":" La Esperanza ",
        "municipio":"Santiago de Cali","descripcion":" Producción familiar "}
        """;

    mockMvc.perform(post("/api/v1/fincas")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + cuenta.token())
            .contentType(MediaType.APPLICATION_JSON).content(request))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.agricultorId").value(cuenta.id()))
        .andExpect(jsonPath("$.nombre").value("La Esperanza"))
        .andExpect(jsonPath("$.municipio").value("Cali"));

    mockMvc.perform(get("/api/v1/fincas")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + cuenta.token()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].agricultorId").value(cuenta.id()));
  }

  @Test
  void dadaSolicitudSinTokenCuandoRegistraFincaEntoncesNoPersiste()
      throws Exception {
    long before = fincas.count();

    mockMvc.perform(post("/api/v1/fincas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(fincaValida()))
        .andExpect(status().isUnauthorized());

    assertThat(fincas.count()).isEqualTo(before);
  }

  @Test
  void dadoCompradorAutenticadoCuandoRegistraFincaEntoncesRespondeForbidden()
      throws Exception {
    CuentaPrueba cuenta = crearCuentaAutenticada("COMPRADOR");
    long before = fincas.count();

    mockMvc.perform(post("/api/v1/fincas")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + cuenta.token())
            .contentType(MediaType.APPLICATION_JSON)
            .content(fincaValida()))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message")
            .value("Solo un agricultor puede registrar una finca"));

    assertThat(fincas.count()).isEqualTo(before);
  }

  @Test
  void dadoMunicipioInvalidoCuandoRegistraFincaEntoncesNoPersiste()
      throws Exception {
    CuentaPrueba cuenta = crearCuentaAutenticada("AGRICULTOR");
    long before = fincas.count();
    String request = """
        {"nombre":"Finca fuera del catálogo","municipio":"Bogotá"}
        """;

    mockMvc.perform(post("/api/v1/fincas")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + cuenta.token())
            .contentType(MediaType.APPLICATION_JSON).content(request))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.municipio").exists());

    assertThat(fincas.count()).isEqualTo(before);
  }

  private CuentaPrueba crearCuentaAutenticada(String rol) throws Exception {
    String unique = UUID.randomUUID().toString();
    String correo = unique + "@agrovalle.test";
    String registration = """
        {"nombre":"Persona de prueba","correo":"%s","contrasena":"secreto123",
        "cedula":"%s","municipio":"Dagua","rol":"%s"}
        """.formatted(correo, unique, rol);
    MvcResult registrationResult = mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON).content(registration))
        .andExpect(status().isCreated()).andReturn();
    long id = objectMapper.readTree(registrationResult.getResponse().getContentAsString())
        .get("id").asLong();

    String login = """
        {"correo":"%s","contrasena":"secreto123"}
        """.formatted(correo);
    MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON).content(login))
        .andExpect(status().isOk()).andReturn();
    JsonNode loginBody = objectMapper.readTree(loginResult.getResponse().getContentAsString());
    return new CuentaPrueba(id, loginBody.get("accessToken").asText());
  }

  private String fincaValida() {
    return """
        {"nombre":"Finca de prueba","municipio":"Dagua",
        "descripcion":"Producción de prueba"}
        """;
  }

  private record CuentaPrueba(long id, String token) {}
}
