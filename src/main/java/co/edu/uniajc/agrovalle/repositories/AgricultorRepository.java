package co.edu.uniajc.agrovalle.repositories;

import co.edu.uniajc.agrovalle.models.Agricultor;
import co.edu.uniajc.agrovalle.ports.UsuarioPort;
import co.edu.uniajc.agrovalle.repositories.jpa.UsuarioEntity;
import co.edu.uniajc.agrovalle.repositories.jpa.UsuarioJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** Adaptador que persiste las cuentas mediante Spring Data JPA. */
@Repository
public class AgricultorRepository implements UsuarioPort {

  private final UsuarioJpaRepository repository;

  /** Recibe el repositorio JPA generado por Spring Data. */
  public AgricultorRepository(UsuarioJpaRepository repository) {
    this.repository = repository;
  }

  /** Guarda la cuenta en la tabla usuarios y obtiene su identificador. */
  @Override
  public Agricultor save(Agricultor agricultor) {
    UsuarioEntity entity = new UsuarioEntity(agricultor.nombre(), agricultor.correo(),
        agricultor.cedula(), agricultor.ubicacionValle(), agricultor.rol(),
        agricultor.contrasenaHash());
    return toDomain(repository.saveAndFlush(entity));
  }

  /** Busca una cuenta por su identificador. */
  @Override
  public Optional<Agricultor> findById(long id) {
    return repository.findById(id).map(this::toDomain);
  }

  /** Busca una cuenta sin distinguir mayúsculas en el correo. */
  @Override
  public Optional<Agricultor> findByCorreo(String correo) {
    return repository.findByCorreoIgnoreCase(correo).map(this::toDomain);
  }

  /** Detecta correos o cédulas registrados sin distinguir mayúsculas. */
  @Override
  public boolean existsByCorreoOrCedula(String correo, String cedula) {
    return repository.existsByCorreoIgnoreCaseOrCedulaIgnoreCase(correo, cedula);
  }

  private Agricultor toDomain(UsuarioEntity entity) {
    return new Agricultor(entity.getId(), entity.getNombre(), entity.getCorreo(),
        entity.getCedula(), entity.getMunicipio(), entity.getRol(),
        entity.getContrasenaHash());
  }
}
