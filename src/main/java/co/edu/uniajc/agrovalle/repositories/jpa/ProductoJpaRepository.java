package co.edu.uniajc.agrovalle.repositories.jpa;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Operaciones de persistencia generadas para los productos agrícolas. */
public interface ProductoJpaRepository extends JpaRepository<ProductoEntity, Long> {

  /** Localiza un producto por nombre dentro de una categoría. */
  Optional<ProductoEntity> findByNombreIgnoreCaseAndCategoriaId(String nombre,
      long categoriaId);
}
