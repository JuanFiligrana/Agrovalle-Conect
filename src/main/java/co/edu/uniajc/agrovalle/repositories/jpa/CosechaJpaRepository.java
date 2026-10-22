package co.edu.uniajc.agrovalle.repositories.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Operaciones de persistencia generadas para las cosechas publicadas. */
public interface CosechaJpaRepository extends JpaRepository<CosechaEntity, Long> {

  /** Devuelve las ofertas en un orden estable. */
  List<CosechaEntity> findAllByOrderByIdAsc();
}
