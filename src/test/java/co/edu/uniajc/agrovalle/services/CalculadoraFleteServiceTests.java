package co.edu.uniajc.agrovalle.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.edu.uniajc.agrovalle.strategies.FleteCercano;
import co.edu.uniajc.agrovalle.strategies.FleteIntermedio;
import co.edu.uniajc.agrovalle.strategies.FleteLejano;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Verifica la selección intercambiable de estrategias de flete. */
class CalculadoraFleteServiceTests {

  private final CalculadoraFleteService service = new CalculadoraFleteService(
      List.of(new FleteCercano(), new FleteIntermedio(), new FleteLejano()));

  @Test
  void aplicaEstrategiaCercanaParaCali() {
    var resultado = service.calcular("Cali", new BigDecimal("100000"));

    assertEquals("CERCANO", resultado.estrategia());
    assertEquals(new BigDecimal("10000.00"), resultado.valorCop());
  }

  @Test
  void aplicaEstrategiaIntermediaParaDagua() {
    var resultado = service.calcular("Dagua", new BigDecimal("100000"));

    assertEquals("INTERMEDIO", resultado.estrategia());
    assertEquals(new BigDecimal("18000.00"), resultado.valorCop());
  }

  @Test
  void aplicaEstrategiaLejanaParaElRestoDelValle() {
    var resultado = service.calcular("Sevilla", new BigDecimal("100000"));

    assertEquals("LEJANO", resultado.estrategia());
    assertEquals(new BigDecimal("26000.00"), resultado.valorCop());
  }

  @Test
  void rechazaMunicipiosAjenosAlValleYSubtotalesNoPositivos() {
    var municipio = assertThrows(ResponseStatusException.class,
        () -> service.calcular("Bogota", new BigDecimal("100000")));
    var subtotal = assertThrows(ResponseStatusException.class,
        () -> service.calcular("Cali", BigDecimal.ZERO));

    assertEquals(HttpStatus.BAD_REQUEST, municipio.getStatusCode());
    assertEquals(HttpStatus.BAD_REQUEST, subtotal.getStatusCode());
  }
}
