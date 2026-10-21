package co.edu.uniajc.agrovalle.repositories.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Representación relacional de una finca propiedad de un agricultor. */
@Entity
@Table(name = "fincas")
public class FincaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "agricultor_id", nullable = false)
  private Long agricultorId;

  @Column(nullable = false, length = 120)
  private String nombre;

  @Column(nullable = false, length = 100)
  private String municipio;

  @Column(length = 500)
  private String descripcion;

  /** Constructor requerido por JPA. */
  protected FincaEntity() {}

  /** Crea una finca nueva; PostgreSQL asigna el identificador. */
  public FincaEntity(long agricultorId, String nombre, String municipio,
      String descripcion) {
    this.agricultorId = agricultorId;
    this.nombre = nombre;
    this.municipio = municipio;
    this.descripcion = descripcion;
  }

  public Long getId() {
    return id;
  }

  public Long getAgricultorId() {
    return agricultorId;
  }

  public String getNombre() {
    return nombre;
  }

  public String getMunicipio() {
    return municipio;
  }

  public String getDescripcion() {
    return descripcion;
  }
}
