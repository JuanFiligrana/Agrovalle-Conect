package co.edu.uniajc.agrovalle.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import co.edu.uniajc.agrovalle.models.Cosecha;
import co.edu.uniajc.agrovalle.ports.CosechaPort;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Caracteriza el cálculo existente; estas pruebas no representan ventas históricas de HU03. */
@ExtendWith(MockitoExtension.class)
class ConsultaPreciosServiceTests {

  @Mock
  private CosechaPort cosechas;

  private ConsultaPreciosService service;

  @BeforeEach
  void prepararServicio() {
    service = new ConsultaPreciosService(cosechas);
  }

  @Test
  void conservaElPromedioDeOfertasActivasDelProductoSinPonderarCantidades() {
    when(cosechas.findAll()).thenReturn(List.of(
        oferta(1, "Cafe", "18500", "1", true),
        oferta(2, "CAFE", "19000", "100", true),
        oferta(3, "Cafe", "90000", "1", false),
        oferta(4, "Mango", "3500", "250", true)));

    var respuesta = service.promedio(" cafe ");

    assertEquals(new BigDecimal("18750.00"), respuesta.promedio());
    assertEquals(2, respuesta.cantidadTransacciones());
    assertEquals("COP/kg", respuesta.unidad());
    assertEquals(" cafe ", respuesta.producto());
  }

  @Test
  void redondeaElPromedioConDosDecimalesUsandoHalfUp() {
    when(cosechas.findAll()).thenReturn(List.of(
        oferta(1, "Cafe", "1.00", "1", true),
        oferta(2, "Cafe", "1.01", "1", true)));

    assertEquals(new BigDecimal("1.01"), service.promedio("Cafe").promedio());
  }

  @Test
  void devuelvePromedioNuloConConteoCeroSiNoHayCoincidencias() {
    when(cosechas.findAll()).thenReturn(List.of(oferta(1, "Mango", "3500", "250", true)));

    var respuesta = service.promedio("Cafe");

    assertNull(respuesta.promedio());
    assertEquals(0, respuesta.cantidadTransacciones());
  }

  private Cosecha oferta(long id, String producto, String precio, String cantidad, boolean activa) {
    return new Cosecha(id, 7, 9, producto, "Categoria de prueba", new BigDecimal(cantidad),
        new BigDecimal(precio), LocalDate.of(2030, 1, 1), activa);
  }
}
