package co.edu.uniajc.agrovalle.repositories;

import co.edu.uniajc.agrovalle.models.Cosecha;
import co.edu.uniajc.agrovalle.ports.CosechaPort;
import co.edu.uniajc.agrovalle.repositories.jpa.CategoriaEntity;
import co.edu.uniajc.agrovalle.repositories.jpa.CategoriaJpaRepository;
import co.edu.uniajc.agrovalle.repositories.jpa.CosechaEntity;
import co.edu.uniajc.agrovalle.repositories.jpa.CosechaJpaRepository;
import co.edu.uniajc.agrovalle.repositories.jpa.FincaJpaRepository;
import co.edu.uniajc.agrovalle.repositories.jpa.ProductoEntity;
import co.edu.uniajc.agrovalle.repositories.jpa.ProductoJpaRepository;
import java.util.Collection;
import org.springframework.stereotype.Repository;

/** Adaptador que persiste las cosechas y sus catálogos mediante JPA. */
@Repository
public class CosechaRepository implements CosechaPort {

  private final CosechaJpaRepository cosechas;
  private final FincaJpaRepository fincas;
  private final CategoriaJpaRepository categorias;
  private final ProductoJpaRepository productos;

  /** Recibe los repositorios normalizados que forman una publicación. */
  public CosechaRepository(CosechaJpaRepository cosechas, FincaJpaRepository fincas,
      CategoriaJpaRepository categorias, ProductoJpaRepository productos) {
    this.cosechas = cosechas;
    this.fincas = fincas;
    this.categorias = categorias;
    this.productos = productos;
  }

  /** Resuelve finca, categoría y producto antes de persistir una oferta. */
  @Override
  public Cosecha save(Cosecha cosecha) {
    var finca = fincas.findById(cosecha.fincaId())
        .orElseThrow(() -> new IllegalStateException("La finca validada dejó de existir"));
    CategoriaEntity categoria = categorias.findByNombreIgnoreCase(cosecha.categoria())
        .orElseGet(() -> categorias.saveAndFlush(new CategoriaEntity(cosecha.categoria())));
    ProductoEntity producto = productos.findByNombreIgnoreCaseAndCategoriaId(
        cosecha.tipo(), categoria.getId())
        .orElseGet(() -> productos.saveAndFlush(new ProductoEntity(categoria, cosecha.tipo())));
    CosechaEntity entity = new CosechaEntity(finca, producto, cosecha.cantidadKg(),
        cosecha.precioCopKg(), cosecha.fechaCosecha(), cosecha.activa());
    return toDomain(cosechas.saveAndFlush(entity));
  }

  /** Devuelve las ofertas persistidas en orden de creación. */
  @Override
  public Collection<Cosecha> findAll() {
    return cosechas.findAllByOrderByIdAsc().stream().map(this::toDomain).toList();
  }

  /** Busca una oferta por identificador; devuelve null si no existe. */
  @Override
  public Cosecha findById(long id) {
    return cosechas.findById(id).map(this::toDomain).orElse(null);
  }

  private Cosecha toDomain(CosechaEntity entity) {
    var finca = entity.getFinca();
    var producto = entity.getProducto();
    return new Cosecha(entity.getId(), finca.getAgricultorId(), finca.getId(),
        producto.getNombre(), producto.getCategoria().getNombre(), entity.getCantidadKg(),
        entity.getPrecioCopKg(), entity.getFechaCosecha(), entity.isActiva());
  }
}
