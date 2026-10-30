package co.edu.uniajc.agrovalle.ports;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/** Historial de ventas finalizadas, independiente de las ofertas publicadas. */
public interface PrecioPort {
  List<BigDecimal> preciosFinalizados(String producto, OffsetDateTime desde,
      OffsetDateTime hasta);
}
