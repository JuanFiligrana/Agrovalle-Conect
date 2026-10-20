package co.edu.uniajc.agrovalle.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

/** Operaciones de persistencia generadas por Spring Data para la tabla usuarios. */
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {

  /** Comprueba duplicados antes de insertar una cuenta. */
  boolean existsByCorreoIgnoreCaseOrCedulaIgnoreCase(String correo, String cedula);
}
