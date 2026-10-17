package co.edu.uniajc.agrovalle.exceptions;

/** Indica una restricción de rol o propiedad sin acoplar el negocio al protocolo HTTP. */
public class OperacionNoPermitidaException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /** Conserva la regla incumplida para que presentación la traduzca. */
  public OperacionNoPermitidaException(String message) {
    super(message);
  }
}
