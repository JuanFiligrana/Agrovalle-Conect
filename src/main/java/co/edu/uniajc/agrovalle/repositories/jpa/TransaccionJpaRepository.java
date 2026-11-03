package co.edu.uniajc.agrovalle.repositories.jpa;

import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Consulta JPA del historial por estado y período inclusivo, con orden determinista. */
public interface TransaccionJpaRepository extends JpaRepository<TransaccionEntity, Long> {
  /** Recupera únicamente registros en el estado y ventana indicados. */
  List<TransaccionEntity> findByEstadoAndFechaFinalizacionBetweenOrderByIdAsc(
      String estado, OffsetDateTime desde, OffsetDateTime hasta);
}
