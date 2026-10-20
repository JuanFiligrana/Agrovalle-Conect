package co.edu.uniajc.agrovalle;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.uniajc.agrovalle.models.Agricultor;
import co.edu.uniajc.agrovalle.ports.UsuarioPort;
import co.edu.uniajc.agrovalle.security.TokenJwtService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/** Escenarios BDD de HU-07 para autenticación segura mediante JWT. */
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
@AutoConfigureMockMvc
class InicioSesionAcceptanceTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UsuarioPort usuarios;

  @Autowired
  private TokenJwtService tokenService;

  @Test
  void dadasCredencialesValidasCuandoIniciaSesionEntoncesObtieneTokenUtilizable()
      throws Exception {
    String correo = correoUnico();
    registrar(correo, "secreto123");

    MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(login(correo, "secreto123")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").isString())
        .andExpect(jsonPath("$.tokenType").value("Bearer"))
        .andExpect(jsonPath("$.expiresInSeconds").value(3600))
        .andReturn();

    JsonNode response = objectMapper.readTree(login.getResponse().getContentAsString());
    String token = response.get("accessToken").asText();
    mockMvc.perform(get("/api/v1/auth/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.correo").value(correo))
        .andExpect(jsonPath("$.rol").value("AGRICULTOR"));
  }

  @Test
  void dadasCredencialesIncorrectasCuandoIniciaSesionEntoncesRespondeUnauthorized()
      throws Exception {
    String correo = correoUnico();
    registrar(correo, "secreto123");

    mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(login(correo, "incorrecta")))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Correo o contraseña incorrectos"))
        .andExpect(jsonPath("$.accessToken").doesNotExist());
  }

  @Test
  void dadoTokenVencidoCuandoConsultaRutaProtegidaEntoncesRespondeUnauthorized()
      throws Exception {
    String correo = correoUnico();
    registrar(correo, "secreto123");
    Agricultor usuario = usuarios.findByCorreo(correo).orElseThrow();
    String tokenVencido = tokenService.generar(usuario, Duration.ofSeconds(-1));

    mockMvc.perform(get("/api/v1/auth/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenVencido))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Token ausente, inválido o vencido"));
  }

  @Test
  void dadoTokenAlteradoCuandoConsultaRutaProtegidaEntoncesRespondeUnauthorized()
      throws Exception {
    mockMvc.perform(get("/api/v1/auth/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer token.alterado.sin-firma"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Token ausente, inválido o vencido"));
  }

  private void registrar(String correo, String contrasena) throws Exception {
    String cedula = UUID.randomUUID().toString();
    String request = """
        {"nombre":"Persona de prueba","correo":"%s","contrasena":"%s",
        "cedula":"%s","municipio":"Cali"}
        """.formatted(correo, contrasena, cedula);
    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isCreated());
  }

  private String login(String correo, String contrasena) {
    return """
        {"correo":"%s","contrasena":"%s"}
        """.formatted(correo, contrasena);
  }

  private String correoUnico() {
    return UUID.randomUUID() + "@agrovalle.test";
  }
}
