package co.edu.uniajc.agrovalle;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Verifica la conexión de los adaptadores y las respuestas HTTP tras refactorizar el negocio. */
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
@AutoConfigureMockMvc
class RefactorizacionEndpointsTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void mantieneElErrorJsonDeFincaAjena() throws Exception {
    long propietario = registrar("AGRICULTOR");
    long otroAgricultor = registrar("AGRICULTOR");
    String finca = """
        {"agricultorId":%d,"nombre":"Finca R01","municipio":"Dagua",
        "descripcion":"Finca para comprobar propiedad"}
        """.formatted(propietario);
    var resultado = mockMvc.perform(post("/api/v1/fincas")
            .contentType(MediaType.APPLICATION_JSON).content(finca))
        .andExpect(status().isCreated()).andReturn();
    long fincaId = objectMapper.readTree(resultado.getResponse().getContentAsString())
        .get("id").asLong();

    mockMvc.perform(post("/api/v1/productos").contentType(MediaType.APPLICATION_JSON)
            .content(publicacion(otroAgricultor, fincaId)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status").value("403 FORBIDDEN"))
        .andExpect(jsonPath("$.message").value("La finca no pertenece al agricultor"));
  }

  @Test
  void mantieneElErrorJsonDeUsuarioInexistenteAlPublicar() throws Exception {
    mockMvc.perform(post("/api/v1/productos").contentType(MediaType.APPLICATION_JSON)
            .content(publicacion(Long.MAX_VALUE, 1)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value("404 NOT_FOUND"))
        .andExpect(jsonPath("$.message").value("No existe el usuario " + Long.MAX_VALUE));
  }

  @Test
  void mantieneElErrorJsonDeOfertaInexistenteAlContactar() throws Exception {
    long comprador = registrar("COMPRADOR");
    String solicitud = """
        {"compradorId":%d,"productoId":%d,"mensaje":"Solicitud de prueba R01"}
        """.formatted(comprador, Long.MAX_VALUE);

    mockMvc.perform(post("/api/v1/contacto/mensaje")
            .header("Authorization", "Bearer demo-r01")
            .contentType(MediaType.APPLICATION_JSON).content(solicitud))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value("404 NOT_FOUND"))
        .andExpect(jsonPath("$.message").value("No existe la cosecha " + Long.MAX_VALUE));
  }

  @Test
  void mantieneElErrorJsonDeRolIncorrectoAlContactar() throws Exception {
    long agricultor = registrar("AGRICULTOR");
    String solicitud = """
        {"compradorId":%d,"productoId":1,"mensaje":"Solicitud de prueba R01"}
        """.formatted(agricultor);

    mockMvc.perform(post("/api/v1/contacto/mensaje")
            .header("Authorization", "Bearer demo-r01")
            .contentType(MediaType.APPLICATION_JSON).content(solicitud))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status").value("403 FORBIDDEN"))
        .andExpect(jsonPath("$.message").value("Solo un comprador puede solicitar contacto"));
  }

  @Test
  void mantieneLaValidacionDeProductoVacioEnLaConsultaDePrecios() throws Exception {
    mockMvc.perform(get("/api/v1/precios/promedio").param("producto", " "))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("El producto es obligatorio"));
  }

  @Test
  void mantieneElContratoDePreciosCuandoNoHayOfertasDelProducto() throws Exception {
    String producto = "ProductoSinOfertasR01-" + UUID.randomUUID();

    mockMvc.perform(get("/api/v1/precios/promedio").param("producto", producto))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.producto").value(producto))
        .andExpect(jsonPath("$.promedio").value(nullValue()))
        .andExpect(jsonPath("$.cantidadTransacciones").value(0))
        .andExpect(jsonPath("$.unidad").value("COP/kg"));
  }

  private long registrar(String rol) throws Exception {
    String identificador = UUID.randomUUID().toString();
    String datos = """
        {"nombre":"Persona R01","correo":"%s@example.invalid","contrasena":"clave-prueba",
        "cedula":"%s","ubicacionValle":"Dagua","rol":"%s"}
        """.formatted(identificador, identificador, rol);
    var resultado = mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON).content(datos))
        .andExpect(status().isCreated()).andReturn();
    return objectMapper.readTree(resultado.getResponse().getContentAsString()).get("id").asLong();
  }

  private String publicacion(long agricultorId, long fincaId) {
    return """
        {"agricultorId":%d,"fincaId":%d,"tipo":"Mango R01","categoria":"Frutas",
        "cantidadKg":250,"precioCopKg":3500,"fechaCosecha":"%s"}
        """.formatted(agricultorId, fincaId, LocalDate.now().plusDays(1));
  }
}
