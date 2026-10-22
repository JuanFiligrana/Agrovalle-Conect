package co.edu.uniajc.agrovalle.strategies;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Estrategia de respaldo para los demás municipios válidos del Valle. */
public final class FleteLejano implements EstrategiaFlete {

  @Override
  public boolean aplica(String municipio) {
    return true;
  }

  @Override
  public String nombre() {
    return "LEJANO";
  }

  @Override
  public BigDecimal calcular(BigDecimal subtotal) {
    return subtotal.multiply(new BigDecimal("0.04"))
        .add(new BigDecimal("22000"))
        .setScale(2, RoundingMode.HALF_UP);
  }
}
