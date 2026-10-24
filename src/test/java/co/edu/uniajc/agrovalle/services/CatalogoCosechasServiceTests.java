package co.edu.uniajc.agrovalle.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import co.edu.uniajc.agrovalle.dto.OfertaCatalogoResponse;
import co.edu.uniajc.agrovalle.models.Agricultor;
import co.edu.uniajc.agrovalle.models.Cosecha;
import co.edu.uniajc.agrovalle.models.Finca;
import co.edu.uniajc.agrovalle.models.RolUsuario;
import co.edu.uniajc.agrovalle.ports.FincaPort;
import co.edu.uniajc.agrovalle.ports.UsuarioPort;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Comprueba la respuesta pública enriquecida que consume la interfaz web. */
@ExtendWith(MockitoExtension.class)
class CatalogoCosechasServiceTests {

  @Mock
  private CosechaService cosechas;

  @Mock
  private FincaPort fincas;

  @Mock
  private UsuarioPort usuarios;

  private CatalogoCosechasService service;

  @BeforeEach
  void prepararServicio() {
    service = new CatalogoCosechasService(cosechas, fincas, usuarios);
  }

  @Test
  void incluyeOrigenYproductorSinExponerDatosPrivados() {
    Cosecha oferta = oferta();
    when(cosechas.listar("Dagua", "Frutas")).thenReturn(List.of(oferta));
    when(fincas.findById(9)).thenReturn(Optional.of(
        new Finca(9, 7, "Finca La Esperanza", "Dagua", "Origen")));
    when(usuarios.findById(7)).thenReturn(Optional.of(
        new Agricultor(7, "Luis Rojas", "privado@example.invalid", "123", "Dagua",
            RolUsuario.AGRICULTOR, "hash-privado")));

    List<OfertaCatalogoResponse> resultado = service.listar("Dagua", "Frutas");

    assertEquals(1, resultado.size());
    OfertaCatalogoResponse respuesta = resultado.get(0);
    assertEquals("Luis Rojas", respuesta.agricultorNombre());
    assertEquals("Finca La Esperanza", respuesta.fincaNombre());
    assertEquals("Dagua", respuesta.municipio());
    assertEquals("Mango Tommy", respuesta.tipo());
  }

  @Test
  void detectaUnaReferenciaDeFincaInconsistente() {
    when(cosechas.listar(null, null)).thenReturn(List.of(oferta()));
    when(fincas.findById(9)).thenReturn(Optional.empty());

    assertThrows(IllegalStateException.class, () -> service.listar(null, null));
  }

  @Test
  void detectaUnaReferenciaDeUsuarioInconsistente() {
    when(cosechas.listar(null, null)).thenReturn(List.of(oferta()));
    when(fincas.findById(9)).thenReturn(Optional.of(
        new Finca(9, 7, "Finca La Esperanza", "Dagua", "Origen")));
    when(usuarios.findById(7)).thenReturn(Optional.empty());

    assertThrows(IllegalStateException.class, () -> service.listar(null, null));
  }

  private Cosecha oferta() {
    return new Cosecha(101, 7, 9, "Mango Tommy", "Frutas",
        new BigDecimal("250.000"), new BigDecimal("3500.00"),
        LocalDate.of(2026, 10, 24), true);
  }
}
