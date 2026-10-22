package co.edu.uniajc.agrovalle.repositories.jpa;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Operaciones de persistencia generadas por Spring Data para la tabla usuarios. */
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {

  /** Recupera la cuenta usada durante el inicio de sesión. */
  Optional<UsuarioEntity> findByCorreoIgnoreCase(String correo);

  /** Comprueba duplicados antes de insertar una cuenta. */
  boolean existsByCorreoIgnoreCaseOrCedulaIgnoreCase(String correo, String cedula);
}
