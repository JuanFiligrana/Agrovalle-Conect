package co.edu.uniajc.agrovalle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Punto de entrada de AgroValle Connect. */
@SpringBootApplication
public class AgrovalleApplication {

  /**
   * Inicia el servidor web.
   *
   * @param args argumentos de arranque
   */
  public static void main(String[] args) {
    SpringApplication.run(AgrovalleApplication.class, args);
  }

  /** Proporciona el algoritmo de hash usado para no guardar contraseñas en claro. */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
