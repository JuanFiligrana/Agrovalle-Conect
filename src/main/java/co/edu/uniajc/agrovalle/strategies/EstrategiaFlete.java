package co.edu.uniajc.agrovalle.strategies;

import java.math.BigDecimal;

/** Contrato intercambiable para calcular un flete según la zona. */
public interface EstrategiaFlete {

  /** Indica si la estrategia atiende el municipio canónico recibido. */
  boolean aplica(String municipio);

  /** Identifica la regla seleccionada para hacerla auditable. */
  String nombre();

  /** Calcula el valor en pesos a partir del subtotal de la compra. */
  BigDecimal calcular(BigDecimal subtotal);
}
