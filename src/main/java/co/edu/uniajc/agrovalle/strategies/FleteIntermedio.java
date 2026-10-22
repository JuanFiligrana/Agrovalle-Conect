package co.edu.uniajc.agrovalle.strategies;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

/** Estrategia para municipios ubicados a una distancia intermedia. */
public final class FleteIntermedio implements EstrategiaFlete {

  private static final Set<String> MUNICIPIOS = Set.of(
      "Dagua", "El Cerrito", "Florida", "Ginebra", "Guacarí", "La Cumbre",
      "Pradera", "Restrepo", "Vijes");

  @Override
  public boolean aplica(String municipio) {
    return MUNICIPIOS.contains(municipio);
  }

  @Override
  public String nombre() {
    return "INTERMEDIO";
  }

  @Override
  public BigDecimal calcular(BigDecimal subtotal) {
    return subtotal.multiply(new BigDecimal("0.03"))
        .add(new BigDecimal("15000"))
        .setScale(2, RoundingMode.HALF_UP);
  }
}
