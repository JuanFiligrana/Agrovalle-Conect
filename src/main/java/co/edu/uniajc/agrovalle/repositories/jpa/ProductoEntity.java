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

/** Producto agrícola reutilizable dentro de una categoría. */
@Entity
@Table(name = "productos")
public class ProductoEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "categoria_id", nullable = false)
  private CategoriaEntity categoria;

  @Column(nullable = false, length = 100)
  private String nombre;

  /** Constructor requerido por JPA. */
  protected ProductoEntity() {}

  /** Crea un producto asociado con su categoría normalizada. */
  public ProductoEntity(CategoriaEntity categoria, String nombre) {
    this.categoria = categoria;
    this.nombre = nombre;
  }

  public Long getId() {
    return id;
  }

  public CategoriaEntity getCategoria() {
    return categoria;
  }

  public String getNombre() {
    return nombre;
  }
}
