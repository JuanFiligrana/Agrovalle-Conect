package co.edu.uniajc.agrovalle.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import co.edu.uniajc.agrovalle.models.Agricultor;
import co.edu.uniajc.agrovalle.models.RolUsuario;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

/** Pruebas de integración del adaptador de usuarios y sus restricciones únicas. */
@DataJpaTest
@Import(AgricultorRepository.class)
class UsuarioPersistenceTests {

  @Autowired
  private AgricultorRepository repository;

  @Test
  void guardaCuentaLuegoPermiteConsultarla() {
    String identificador = UUID.randomUUID().toString();
    Agricultor saved = repository.save(usuario(
        identificador + "@agrovalle.test", identificador));

    assertThat(saved.id()).isPositive();
    assertThat(repository.findById(saved.id())).contains(saved);
    assertThat(repository.existsByCorreoOrCedula(saved.correo(), "otra-cedula")).isTrue();
  }

  @Test
  void normalizaElCorreoAntesDePersistir() {
    String identificador = UUID.randomUUID().toString();
    Agricultor saved = repository.save(usuario(
        "  MAYUSCULA-" + identificador + "@AGROVALLE.TEST  ", identificador));

    assertThat(saved.correo()).isEqualTo(
        "mayuscula-" + identificador + "@agrovalle.test");
  }

  @Test
  void laBaseDeDatosRechazaCorreosDuplicados() {
    String identificador = UUID.randomUUID().toString();
    String correo = identificador + "@agrovalle.test";
    repository.save(usuario(correo, identificador + "-1"));

    assertThatThrownBy(() -> repository.save(usuario(correo.toUpperCase(),
        identificador + "-2")))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  void laBaseDeDatosRechazaCedulasDuplicadas() {
    String identificador = UUID.randomUUID().toString();
    repository.save(usuario(identificador + "-1@agrovalle.test", identificador));

    assertThatThrownBy(() -> repository.save(usuario(
        identificador + "-2@agrovalle.test", identificador)))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  private Agricultor usuario(String correo, String cedula) {
    return new Agricultor(0, "Persona de prueba", correo, cedula, "Cali",
        RolUsuario.AGRICULTOR, "$2a$10$hashDePruebaNoReal");
  }
}
