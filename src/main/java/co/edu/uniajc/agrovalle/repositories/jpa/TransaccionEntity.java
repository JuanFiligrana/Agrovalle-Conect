package co.edu.uniajc.agrovalle.repositories.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Registro de precio pactado; su producto y venta original conservan claves foráneas. */
@Entity
@Table(name = "transacciones")
public class TransaccionEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "producto_id", nullable = false)
  private ProductoEntity producto;

  @Column(name = "producto", nullable = false, length = 100)
  private String nombreProducto;

  @Column(name = "precio_cop_kg", nullable = false, precision = 14, scale = 2)
  private BigDecimal precioUnitarioCopKg;

  @Column(nullable = false, length = 20)
  private String estado;

  @Column(name = "finalizada_en")
  private OffsetDateTime fechaFinalizacion;

  /** Constructor de hidratación requerido por JPA. */
  protected TransaccionEntity() {}

  public Long getId() {
    return id;
  }

  public ProductoEntity getProducto() {
    return producto;
  }

  public String getNombreProducto() {
    return nombreProducto;
  }

  public BigDecimal getPrecioUnitarioCopKg() {
    return precioUnitarioCopKg;
  }

  public String getEstado() {
    return estado;
  }

  public OffsetDateTime getFechaFinalizacion() {
    return fechaFinalizacion;
  }
}
