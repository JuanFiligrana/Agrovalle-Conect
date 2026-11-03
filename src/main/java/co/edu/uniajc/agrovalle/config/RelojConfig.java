package co.edu.uniajc.agrovalle.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Reloj inyectable para reservas, ventanas de precios y trazabilidad. */
@Configuration
public class RelojConfig {
  /** Proporciona la hora UTC del servidor. */
  @Bean
  public Clock relojComercial() {
    return Clock.systemUTC();
  }
}
