package co.edu.uniajc.agrovalle.repositories.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Operaciones de persistencia generadas para la tabla de fincas. */
public interface FincaJpaRepository extends JpaRepository<FincaEntity, Long> {

  /** Ordena las fincas del propietario para ofrecer una respuesta estable. */
  List<FincaEntity> findAllByAgricultorIdOrderByIdAsc(long agricultorId);
}
