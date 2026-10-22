package co.edu.uniajc.agrovalle.services;

import co.edu.uniajc.agrovalle.dto.FleteResponse;
import co.edu.uniajc.agrovalle.strategies.EstrategiaFlete;
import co.edu.uniajc.agrovalle.validation.MunicipiosValle;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Contexto del patrón Strategy que elige una regla de flete por municipio. */
@Service
public class CalculadoraFleteService {

  private final List<EstrategiaFlete> estrategias;

  /** Recibe estrategias ordenadas, desde las específicas hasta la de respaldo. */
  public CalculadoraFleteService(List<EstrategiaFlete> estrategias) {
    this.estrategias = List.copyOf(estrategias);
  }

  /** Valida los datos, selecciona una estrategia y devuelve el cálculo auditable. */
  public FleteResponse calcular(String municipio, BigDecimal subtotal) {
    String municipioCanonico = MunicipiosValle.canonicalizar(municipio)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "municipio: debe pertenecer al catálogo del Valle del Cauca"));
    if (subtotal == null || subtotal.signum() <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "subtotal: debe ser mayor que cero");
    }
    EstrategiaFlete estrategia = estrategias.stream()
        .filter(item -> item.aplica(municipioCanonico))
        .findFirst()
        .orElseThrow();
    return new FleteResponse(municipioCanonico, estrategia.nombre(),
        estrategia.calcular(subtotal));
  }
}
