package co.edu.uniajc.agrovalle.strategies;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Estrategia para municipios cercanos al centro de distribución. */
@Component
@Order(10)
public final class FleteCercano implements EstrategiaFlete {

  private static final Set<String> MUNICIPIOS = Set.of(
      "Cali", "Candelaria", "Jamundí", "Palmira", "Yumbo");

  @Override
  public boolean aplica(String municipio) {
    return MUNICIPIOS.contains(municipio);
  }

  @Override
  public String nombre() {
    return "CERCANO";
  }

  @Override
  public BigDecimal calcular(BigDecimal subtotal) {
    return subtotal.multiply(new BigDecimal("0.02"))
        .add(new BigDecimal("8000"))
        .setScale(2, RoundingMode.HALF_UP);
  }
}
