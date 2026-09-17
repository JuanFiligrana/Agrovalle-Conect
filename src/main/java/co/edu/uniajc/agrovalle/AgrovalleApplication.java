package co.edu.uniajc.agrovalle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
}
