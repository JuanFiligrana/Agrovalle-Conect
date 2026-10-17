package co.edu.uniajc.agrovalle.services;

import co.edu.uniajc.agrovalle.dto.PrecioPromedioResponse;
import co.edu.uniajc.agrovalle.models.Cosecha;
import co.edu.uniajc.agrovalle.ports.CosechaPort;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Service;

/** Consulta de precios del prototipo; la referencia histórica de HU03 sigue pendiente. */
@Service
public class ConsultaPreciosService {

  private final CosechaPort cosechas;

  /** Recibe el contrato de ofertas sin seleccionar el adaptador que las almacena. */
  public ConsultaPreciosService(CosechaPort cosechas) {
    this.cosechas = cosechas;
  }

  /** Conserva la media de ofertas activas que utilizaba el prototipo anterior. */
  public PrecioPromedioResponse promedio(String producto) {
    List<Cosecha> ofertas = cosechas.findAll().stream()
        .filter(Cosecha::activa)
        .filter(item -> item.tipo().equalsIgnoreCase(producto.trim()))
        .toList();
    BigDecimal promedio = ofertas.stream()
        .map(Cosecha::precioCopKg)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    if (!ofertas.isEmpty()) {
      promedio = promedio.divide(BigDecimal.valueOf(ofertas.size()), 2,
          RoundingMode.HALF_UP);
    } else {
      promedio = null;
    }
    return new PrecioPromedioResponse(producto, promedio, ofertas.size(), "COP/kg");
  }
}
