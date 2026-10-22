package co.edu.uniajc.agrovalle.repositories.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Oferta persistente que une una finca con un producto normalizado. */
@Entity
@Table(name = "cosechas")
public class CosechaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "finca_id", nullable = false)
  private FincaEntity finca;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "producto_id", nullable = false)
  private ProductoEntity producto;

  @Column(name = "cantidad_kg", nullable = false, precision = 14, scale = 3)
  private BigDecimal cantidadKg;

  @Column(name = "precio_cop_kg", nullable = false, precision = 14, scale = 2)
  private BigDecimal precioCopKg;

  @Column(name = "fecha_cosecha", nullable = false)
  private LocalDate fechaCosecha;

  @Column(nullable = false)
  private boolean activa;

  /** Constructor requerido por JPA. */
  protected CosechaEntity() {}

  /** Crea una oferta persistente con sus relaciones resueltas. */
  public CosechaEntity(FincaEntity finca, ProductoEntity producto, BigDecimal cantidadKg,
      BigDecimal precioCopKg, LocalDate fechaCosecha, boolean activa) {
    this.finca = finca;
    this.producto = producto;
    this.cantidadKg = cantidadKg;
    this.precioCopKg = precioCopKg;
    this.fechaCosecha = fechaCosecha;
    this.activa = activa;
  }

  public Long getId() {
    return id;
  }

  public FincaEntity getFinca() {
    return finca;
  }

  public ProductoEntity getProducto() {
    return producto;
  }

  public BigDecimal getCantidadKg() {
    return cantidadKg;
  }

  public BigDecimal getPrecioCopKg() {
    return precioCopKg;
  }

  public LocalDate getFechaCosecha() {
    return fechaCosecha;
  }

  public boolean isActiva() {
    return activa;
  }
}
