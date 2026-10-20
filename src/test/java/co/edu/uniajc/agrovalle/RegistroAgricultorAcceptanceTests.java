package co.edu.uniajc.agrovalle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.uniajc.agrovalle.models.RolUsuario;
import co.edu.uniajc.agrovalle.repositories.jpa.UsuarioJpaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/** Escenarios BDD de HU-01 para el registro seguro de agricultores. */
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
@AutoConfigureMockMvc
class RegistroAgricultorAcceptanceTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UsuarioJpaRepository usuarios;

  @Test
  void dadoDatosValidosCuandoRegistraEntoncesPersisteAgricultorSinExponerHash()
      throws Exception {
    String unique = UUID.randomUUID().toString();
    MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(solicitud(unique, unique, "municipio", "Santiago de Cali")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.rol").value("AGRICULTOR"))
        .andExpect(jsonPath("$.ubicacionValle").value("Cali"))
        .andExpect(jsonPath("$.contrasena").doesNotExist())
        .andExpect(jsonPath("$.contrasenaHash").doesNotExist())
        .andReturn();

    JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
    var persisted = usuarios.findById(response.get("id").asLong()).orElseThrow();
    assertThat(persisted.getRol()).isEqualTo(RolUsuario.AGRICULTOR);
    assertThat(persisted.getContrasenaHash()).startsWith("$2");
    assertThat(persisted.getContrasenaHash()).doesNotContain("secreto123");
  }

  @Test
  void dadoCorreoDuplicadoCuandoRegistraEntoncesResponde409SinCrearCuenta()
      throws Exception {
    String unique = UUID.randomUUID().toString();
    registrar(unique, unique);
    long before = usuarios.count();

    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(solicitud(unique, unique + "-otra", "ubicacion_valle", "Dagua")))
        .andExpect(status().isConflict());

    assertThat(usuarios.count()).isEqualTo(before);
  }

  @Test
  void dadaCedulaDuplicadaCuandoRegistraEntoncesResponde409SinCrearCuenta()
      throws Exception {
    String unique = UUID.randomUUID().toString();
    registrar(unique, unique);
    long before = usuarios.count();

    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(solicitud(unique + "-otro", unique, "ubicacionValle", "Palmira")))
        .andExpect(status().isConflict());

    assertThat(usuarios.count()).isEqualTo(before);
  }

  @Test
  void dadosCamposInvalidosCuandoRegistraEntoncesRespondeBadRequestSinPersistir()
      throws Exception {
    String unique = UUID.randomUUID().toString();
    long before = usuarios.count();
    String request = """
        {"nombre":"Persona de prueba","correo":"%s@agrovalle.test",
        "contrasena":"secreto123","cedula":"","municipio":"Bogotá"}
        """.formatted(unique);

    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON).content(request))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.cedula").exists())
        .andExpect(jsonPath("$.errors.ubicacionValle").exists());

    assertThat(usuarios.count()).isEqualTo(before);
  }

  private void registrar(String correo, String cedula) throws Exception {
    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(solicitud(correo, cedula, "ubicacionValle", "Dagua")))
        .andExpect(status().isCreated());
  }

  private String solicitud(String correo, String cedula, String campoMunicipio,
      String municipio) {
    return """
        {"nombre":"Persona de prueba","correo":"%s@agrovalle.test",
        "contrasena":"secreto123","cedula":"%s","%s":"%s"}
        """.formatted(correo, cedula, campoMunicipio, municipio);
  }
}
