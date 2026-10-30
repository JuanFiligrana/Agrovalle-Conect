package co.edu.uniajc.agrovalle.services;

import co.edu.uniajc.agrovalle.dto.PrecioPromedioResponse;
import co.edu.uniajc.agrovalle.ports.PrecioPort;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Service;

/** Media aritmética no ponderada de ventas completadas en las últimas 24 horas. */
@Service
public class ConsultaPreciosService {

  private final PrecioPort precios;
  private final Clock reloj;

  /** Inyecta el historial y un reloj que permite verificar los límites temporales. */
  public ConsultaPreciosService(PrecioPort precios, Clock reloj) {
    this.precios = precios;
    this.reloj = reloj;
  }

  /** Cuenta ventas, no kilogramos, y devuelve null cuando no hay historial reciente. */
  public PrecioPromedioResponse promedio(String producto) {
    var hasta = OffsetDateTime.now(reloj);
    var valores = precios.preciosFinalizados(producto.trim(), hasta.minusHours(24), hasta);
    BigDecimal promedio = valores.isEmpty() ? null
        : valores.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(valores.size()), 2, RoundingMode.HALF_UP);
    return new PrecioPromedioResponse(producto.trim(), promedio, valores.size(), "COP/kg");
  }
}
