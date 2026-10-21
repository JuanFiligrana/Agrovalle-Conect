package co.edu.uniajc.agrovalle.repositories;

import co.edu.uniajc.agrovalle.models.Finca;
import co.edu.uniajc.agrovalle.ports.FincaPort;
import co.edu.uniajc.agrovalle.repositories.jpa.FincaEntity;
import co.edu.uniajc.agrovalle.repositories.jpa.FincaJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** Adaptador que persiste las fincas mediante Spring Data JPA. */
@Repository
public class FincaRepository implements FincaPort {

  private final FincaJpaRepository repository;

  /** Recibe el repositorio JPA generado por Spring Data. */
  public FincaRepository(FincaJpaRepository repository) {
    this.repository = repository;
  }

  /** Guarda una finca y obtiene su identificador. */
  @Override
  public Finca save(Finca finca) {
    FincaEntity entity = new FincaEntity(finca.agricultorId(), finca.nombre(),
        finca.municipio(), finca.descripcion());
    return toDomain(repository.saveAndFlush(entity));
  }

  /** Busca una finca por su identificador. */
  @Override
  public Optional<Finca> findById(long id) {
    return repository.findById(id).map(this::toDomain);
  }

  /** Devuelve únicamente las fincas del agricultor solicitado. */
  @Override
  public List<Finca> findAllByAgricultorId(long agricultorId) {
    return repository.findAllByAgricultorIdOrderByIdAsc(agricultorId).stream()
        .map(this::toDomain)
        .toList();
  }

  private Finca toDomain(FincaEntity entity) {
    return new Finca(entity.getId(), entity.getAgricultorId(), entity.getNombre(),
        entity.getMunicipio(), entity.getDescripcion());
  }
}
