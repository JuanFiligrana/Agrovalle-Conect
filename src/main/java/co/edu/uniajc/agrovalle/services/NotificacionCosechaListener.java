package co.edu.uniajc.agrovalle.services;

import co.edu.uniajc.agrovalle.events.CosechaPublicadaEvent;
import co.edu.uniajc.agrovalle.models.NotificacionCosecha;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/** Observador que transforma publicaciones en avisos consultables por el agricultor. */
@Service
public class NotificacionCosechaListener {

  private final AtomicLong sequence = new AtomicLong();
  private final List<NotificacionCosecha> notificaciones = new CopyOnWriteArrayList<>();

  /** Reacciona al evento sin acoplar el servicio de cosechas al canal de aviso. */
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void alPublicar(CosechaPublicadaEvent event) {
    var cosecha = event.cosecha();
    notificaciones.add(new NotificacionCosecha(sequence.incrementAndGet(),
        cosecha.agricultorId(), cosecha.id(),
        "Cosecha de " + cosecha.tipo() + " publicada correctamente"));
  }

  /** Devuelve únicamente los avisos del usuario autenticado. */
  public List<NotificacionCosecha> listar(long agricultorId) {
    return notificaciones.stream()
        .filter(item -> item.agricultorId() == agricultorId)
        .toList();
  }
}
