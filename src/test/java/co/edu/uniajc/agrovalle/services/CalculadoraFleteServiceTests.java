package co.edu.uniajc.agrovalle.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/** Verifica la selección intercambiable de estrategias de flete. */
class CalculadoraFleteServiceTests {

  private final CalculadoraFleteService service = new CalculadoraFleteService();

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
}
