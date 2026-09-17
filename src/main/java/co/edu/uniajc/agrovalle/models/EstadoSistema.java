package co.edu.uniajc.agrovalle.models;

/**
 * Contrato de respuesta del estado del servicio.
 *
 * @param aplicacion nombre del producto
 * @param estado disponibilidad del servicio
 * @param etapa etapa del proyecto
 */
public record EstadoSistema(String aplicacion, String estado, String etapa) {}
