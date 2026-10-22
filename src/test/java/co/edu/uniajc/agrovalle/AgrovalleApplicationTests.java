package co.edu.uniajc.agrovalle;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
@AutoConfigureMockMvc
class AgrovalleApplicationTests {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void estadoRespetaElContratoJson() throws Exception {
    mockMvc.perform(get("/api/v1/estado"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.aplicacion").value("AgroValle Connect"))
        .andExpect(jsonPath("$.estado").value("OK"))
        .andExpect(jsonPath("$.etapa").value("Sprint 1"));
  }

  @Test
  void portadaSeSirveComoHtml() throws Exception {
    mockMvc.perform(get("/index.html"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
        .andExpect(content().string(containsString("AgroValle Connect")));
  }

  @Test
  void unaRutaInexistenteNoSimulaUnaFuncionalidadImplementada() throws Exception {
    mockMvc.perform(get("/api/v1/ruta-inexistente"))
        .andExpect(status().isNotFound());
  }
}
