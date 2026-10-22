package co.edu.uniajc.agrovalle.repositories.jpa;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Operaciones de persistencia generadas para el catálogo de categorías. */
public interface CategoriaJpaRepository extends JpaRepository<CategoriaEntity, Long> {

  /** Localiza una categoría sin depender de mayúsculas y minúsculas. */
  Optional<CategoriaEntity> findByNombreIgnoreCase(String nombre);
}
