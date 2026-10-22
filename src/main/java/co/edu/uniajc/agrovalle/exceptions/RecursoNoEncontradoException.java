package co.edu.uniajc.agrovalle.exceptions;

/** Indica que falta un recurso requerido por un caso de uso, sin decidir su respuesta HTTP. */
public class RecursoNoEncontradoException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /** Conserva el motivo de la ausencia para que presentación lo traduzca. */
  public RecursoNoEncontradoException(String message) {
    super(message);
  }
}
