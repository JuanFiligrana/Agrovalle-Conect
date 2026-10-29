package co.edu.uniajc.agrovalle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.uniajc.agrovalle.ports.UsuarioPort;
import co.edu.uniajc.agrovalle.repositories.jpa.ContactoJpaRepository;
import co.edu.uniajc.agrovalle.repositories.jpa.NotificacionContactoJpaRepository;
import co.edu.uniajc.agrovalle.security.TokenJwtService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

/** Aceptación HU-05/HU-10: sesión real, persistencia, privacidad y lectura. */
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
@AutoConfigureMockMvc
@Transactional
class ContactoAcceptanceTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper json;

  @Autowired
  private ContactoJpaRepository contactos;

  @Autowired
  private NotificacionContactoJpaRepository notificaciones;

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private UsuarioPort usuarios;

  @Autowired
  private TokenJwtService tokens;

  @Test
  void completaRegistroFincaCosechaCatalogoContactoYlecturaPersistente() throws Exception {
    Cuenta agricultor = registrar("AGRICULTOR");
    Cuenta comprador = registrar("COMPRADOR");
    long oferta = publicar(agricultor);
    mockMvc.perform(get("/api/v1/productos").param("municipio", "Pradera")
            .param("categoria", "Hierbas y aromáticas"))
        .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
    mockMvc.perform(get("/api/v1/productos/" + oferta))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.agricultorNombre").value("Cuenta " + agricultor.idLocal()))
        .andExpect(jsonPath("$.fincaNombre").value("Finca contacto"))
        .andExpect(jsonPath("$.municipio").value("Pradera"))
        .andExpect(jsonPath("$.correo").doesNotExist())
        .andExpect(jsonPath("$.contrasenaHash").doesNotExist());

    final long totalAntes = notificaciones.count();
    long contactoId = enviar(comprador, oferta, "  ¿Hay entrega el viernes?  ");
    entityManager.flush();
    entityManager.clear();
    var guardado = contactos.findById(contactoId).orElseThrow();
    assertThat(guardado.getComprador().getId()).isEqualTo(comprador.id());
    assertThat(guardado.getMensaje()).isEqualTo("¿Hay entrega el viernes?");
    assertThat(guardado.getFechaCreacion()).isNotNull();
    assertThat(notificaciones.count()).isEqualTo(totalAntes + 1);

    JsonNode bandeja = leerBandeja(agricultor);
    assertThat(bandeja.size()).isEqualTo(1);
    assertThat(bandeja.get(0).get("leida").asBoolean()).isFalse();
    assertThat(leerBandeja(comprador).get(0).get("id").asLong()).isEqualTo(contactoId);
    long notificacionId = bandeja.get(0).get("notificacionId").asLong();
    for (int intento = 0; intento < 2; intento++) {
      mockMvc.perform(patch("/api/v1/contacto/notificaciones/" + notificacionId + "/lectura")
              .header("Authorization", agricultor.bearer()))
          .andExpect(status().isNoContent());
    }
    entityManager.clear();
    assertThat(notificaciones.findById(notificacionId).orElseThrow().isLeida()).isTrue();
    assertThat(notificaciones.count()).isEqualTo(totalAntes + 1);
  }

  @Test
  void noPermiteSuplantarAlCompradorDesdeElJson() throws Exception {
    Cuenta agricultor = registrar("AGRICULTOR");
    Cuenta comprador = registrar("COMPRADOR");
    Cuenta otro = registrar("COMPRADOR");
    long oferta = publicar(agricultor);
    String body = json.writeValueAsString(Map.of("compradorId", otro.id(),
        "id_producto", oferta, "mensaje", "Mensaje del comprador autenticado"));
    var result = mockMvc.perform(post("/api/v1/contacto/mensaje")
            .header("Authorization", comprador.bearer())
            .contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isOk()).andReturn();
    var guardado = contactos.findById(parse(result).get("id").asLong()).orElseThrow();
    assertThat(guardado.getComprador().getId()).isEqualTo(comprador.id());
    assertThat(leerBandeja(otro).size()).isZero();
  }

  @Test
  void rechazaSinTokenYconTokenInventadoSinGuardarNada() throws Exception {
    long antes = contactos.count();
    mockMvc.perform(post("/api/v1/contacto/mensaje")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"productoId\":1,\"mensaje\":\"Prueba\"}"))
        .andExpect(status().isUnauthorized());
    mockMvc.perform(post("/api/v1/contacto/mensaje").header("Authorization", "Bearer demo")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"productoId\":1,\"mensaje\":\"Prueba\"}"))
        .andExpect(status().isUnauthorized());
    mockMvc.perform(get("/api/v1/contacto/mensajes"))
        .andExpect(status().isUnauthorized());
    assertThat(contactos.count()).isEqualTo(antes);
  }

  @Test
  void rechazaMensajesVaciosLargosYofertasInexistentes() throws Exception {
    Cuenta comprador = registrar("COMPRADOR");
    long antes = contactos.count();
    for (String mensaje : new String[] {"", "   ", "x".repeat(1001)}) {
      mockMvc.perform(post("/api/v1/contacto/mensaje")
              .header("Authorization", comprador.bearer())
              .contentType(MediaType.APPLICATION_JSON)
              .content(json.writeValueAsString(Map.of("productoId", 1, "mensaje", mensaje))))
          .andExpect(status().isBadRequest());
    }
    mockMvc.perform(post("/api/v1/contacto/mensaje")
            .header("Authorization", comprador.bearer())
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(Map.of("productoId", Long.MAX_VALUE,
                "mensaje", "Mensaje válido"))))
        .andExpect(status().isNotFound());
    assertThat(contactos.count()).isEqualTo(antes);
  }

  @Test
  void aceptaLosLimitesDeUnoYmilCaracteres() throws Exception {
    Cuenta agricultor = registrar("AGRICULTOR");
    Cuenta comprador = registrar("COMPRADOR");
    long oferta = publicar(agricultor);
    for (String mensaje : new String[] {"a", "x".repeat(1000)}) {
      long id = enviar(comprador, oferta, mensaje);
      entityManager.flush();
      entityManager.clear();
      assertThat(contactos.findById(id).orElseThrow().getMensaje()).isEqualTo(mensaje);
    }
    assertThat(leerBandeja(agricultor).size()).isEqualTo(2);
  }

  @Test
  void rechazaRolAgricultorYtokenVencidoSinCrearRegistros() throws Exception {
    Cuenta agricultor = registrar("AGRICULTOR");
    final Cuenta comprador = registrar("COMPRADOR");
    long oferta = publicar(agricultor);
    final long contactosAntes = contactos.count();
    final long notificacionesAntes = notificaciones.count();
    String body = json.writeValueAsString(Map.of("productoId", oferta, "mensaje", "Prueba"));
    mockMvc.perform(post("/api/v1/contacto/mensaje").header("Authorization", agricultor.bearer())
            .contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isForbidden());
    String vencido = tokens.generar(usuarios.findById(comprador.id()).orElseThrow(),
        Duration.ofSeconds(-1));
    mockMvc.perform(post("/api/v1/contacto/mensaje").header("Authorization", "Bearer " + vencido)
            .contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isUnauthorized());
    assertThat(contactos.count()).isEqualTo(contactosAntes);
    assertThat(notificaciones.count()).isEqualTo(notificacionesAntes);
  }

  @Test
  void protegeMensajesYnotificacionesDeOtrosUsuarios() throws Exception {
    Cuenta agricultor = registrar("AGRICULTOR");
    Cuenta comprador = registrar("COMPRADOR");
    Cuenta intruso = registrar("AGRICULTOR");
    enviar(comprador, publicar(agricultor), "Solicitud privada");
    long id = leerBandeja(agricultor).get(0).get("notificacionId").asLong();
    assertThat(leerBandeja(intruso).size()).isZero();
    for (Cuenta ajena : new Cuenta[] {comprador, intruso}) {
      mockMvc.perform(patch("/api/v1/contacto/notificaciones/" + id + "/lectura")
              .header("Authorization", ajena.bearer()))
          .andExpect(status().isForbidden());
    }
    mockMvc.perform(patch("/api/v1/contacto/notificaciones/" + Long.MAX_VALUE + "/lectura")
            .header("Authorization", agricultor.bearer()))
        .andExpect(status().isNotFound());
    assertThat(leerBandeja(agricultor).get(0).get("leida").asBoolean()).isFalse();
  }

  @Test
  void unaOfertaInactivaNoSePuedeConsultarNiContactar() throws Exception {
    Cuenta agricultor = registrar("AGRICULTOR");
    final Cuenta comprador = registrar("COMPRADOR");
    long oferta = publicar(agricultor);
    entityManager.createNativeQuery("UPDATE cosechas SET activa = false WHERE id = :id")
        .setParameter("id", oferta).executeUpdate();
    entityManager.clear();
    mockMvc.perform(get("/api/v1/productos/" + oferta)).andExpect(status().isNotFound());
    mockMvc.perform(get("/api/v1/productos/" + Long.MAX_VALUE))
        .andExpect(status().isNotFound());
    mockMvc.perform(post("/api/v1/contacto/mensaje")
            .header("Authorization", comprador.bearer()).contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(Map.of("productoId", oferta, "mensaje", "Prueba"))))
        .andExpect(status().isNotFound());
  }

  private long enviar(Cuenta comprador, long oferta, String mensaje) throws Exception {
    var result = mockMvc.perform(post("/api/v1/contacto/mensaje")
            .header("Authorization", comprador.bearer()).contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(Map.of("productoId", oferta, "mensaje", mensaje))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.notificacionRegistrada").value(true)).andReturn();
    return parse(result).get("id").asLong();
  }

  private JsonNode leerBandeja(Cuenta cuenta) throws Exception {
    return parse(mockMvc.perform(get("/api/v1/contacto/mensajes")
            .header("Authorization", cuenta.bearer())).andExpect(status().isOk()).andReturn());
  }

  private Cuenta registrar(String rol) throws Exception {
    String unico = UUID.randomUUID().toString();
    String correo = unico + "@example.invalid";
    String body = json.writeValueAsString(Map.of("nombre", "Cuenta " + unico,
        "correo", correo, "contrasena", "clave-prueba", "cedula", unico,
        "ubicacionValle", "Pradera", "rol", rol));
    var registro = mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isCreated()).andReturn();
    var login = mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(
                Map.of("correo", correo, "contrasena", "clave-prueba"))))
        .andExpect(status().isOk()).andReturn();
    return new Cuenta(parse(registro).get("id").asLong(), unico,
        "Bearer " + parse(login).get("accessToken").asText());
  }

  private long publicar(Cuenta agricultor) throws Exception {
    var finca = mockMvc.perform(post("/api/v1/fincas")
            .header("Authorization", agricultor.bearer()).contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"nombre":"Finca contacto","municipio":"Pradera","descripcion":"Prueba"}
                """))
        .andExpect(status().isCreated()).andReturn();
    String fecha = LocalDate.now(ZoneId.of("America/Bogota")).plusDays(1).toString();
    String body = json.writeValueAsString(Map.of("fincaId", parse(finca).get("id").asLong(),
        "tipo", "Albahaca", "categoria", "Hierbas y aromáticas", "cantidadKg", 20,
        "precioCopKg", 3500, "fechaCosecha", fecha));
    return parse(mockMvc.perform(post("/api/v1/productos")
            .header("Authorization", agricultor.bearer())
            .contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isCreated()).andReturn()).get("id").asLong();
  }

  private JsonNode parse(MvcResult result) throws Exception {
    return json.readTree(result.getResponse().getContentAsString());
  }

  private record Cuenta(long id, String idLocal, String bearer) {}
}
