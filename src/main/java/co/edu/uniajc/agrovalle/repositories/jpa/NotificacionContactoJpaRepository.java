package co.edu.uniajc.agrovalle.repositories.jpa;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Consultas de mensajes privados y actualización idempotente de lectura. */
public interface NotificacionContactoJpaRepository
    extends JpaRepository<NotificacionContactoEntity, Long> {

  /** Solo devuelve mensajes en los que participa la identidad autenticada. */
  @Query("""
      select n from NotificacionContactoEntity n
      where n.destinatario.id = :usuarioId or n.contacto.comprador.id = :usuarioId
      order by n.id desc
      """)
  List<NotificacionContactoEntity> findPropias(@Param("usuarioId") long usuarioId,
      Pageable pageable);

  /** La condición de propietario se conserva también en la escritura SQL. */
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      update NotificacionContactoEntity n set n.leida = true
      where n.id = :id and n.destinatario.id = :destinatarioId
      """)
  int marcarLeida(@Param("id") long id, @Param("destinatarioId") long destinatarioId);
}
