package co.edu.uniajc.agrovalle.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import co.edu.uniajc.agrovalle.ports.PrecioPort;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Ventana determinista y redondeo del promedio histórico de HU03. */
@ExtendWith(MockitoExtension.class)
class ConsultaPreciosServiceTests {
  private static final Clock RELOJ = Clock.fixed(
      Instant.parse("2026-10-30T12:00:00Z"), ZoneOffset.UTC);
  @Mock
  private PrecioPort precios;
  private ConsultaPreciosService service;

  @BeforeEach
  void prepararServicio() {
    service = new ConsultaPreciosService(precios, RELOJ);
  }

  @Test
  void consultaUltimasVeinticuatroHorasYcalculaMediaNoPonderada() {
    historial("cafe", List.of(new BigDecimal("18500"), new BigDecimal("19000")));
    var respuesta = service.promedio(" cafe ");
    assertEquals(new BigDecimal("18750.00"), respuesta.promedio());
    assertEquals(2, respuesta.cantidadTransacciones());
    assertEquals("COP/kg", respuesta.unidad());
    assertEquals("cafe", respuesta.producto());
  }

  @Test
  void redondeaElPromedioConDosDecimalesUsandoHalfUp() {
    historial("Cafe", List.of(new BigDecimal("1.00"), new BigDecimal("1.01")));
    assertEquals(new BigDecimal("1.01"), service.promedio("Cafe").promedio());
  }

  @Test
  void devuelvePromedioNuloConConteoCeroSiNoHayCoincidencias() {
    historial("Cafe", List.of());
    var respuesta = service.promedio("Cafe");
    assertNull(respuesta.promedio());
    assertEquals(0, respuesta.cantidadTransacciones());
  }

  private void historial(String producto, List<BigDecimal> valores) {
    var hasta = OffsetDateTime.now(RELOJ);
    when(precios.preciosFinalizados(producto, hasta.minusHours(24), hasta)).thenReturn(valores);
  }
}
