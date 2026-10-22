package co.edu.uniajc.agrovalle.repositories.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Categoría normalizada que agrupa los productos agrícolas. */
@Entity
@Table(name = "categorias")
public class CategoriaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 80, unique = true)
  private String nombre;

  /** Constructor requerido por JPA. */
  protected CategoriaEntity() {}

  /** Crea una categoría canónica nueva. */
  public CategoriaEntity(String nombre) {
    this.nombre = nombre;
  }

  public Long getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }
}
