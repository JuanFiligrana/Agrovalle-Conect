package co.edu.uniajc.agrovalle.repositories.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

/** Mensaje persistente asociado al comprador autenticado y a una cosecha existente. */
@Entity
@Table(name = "interacciones_contacto")
public class ContactoEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "comprador_id", nullable = false)
  private UsuarioEntity comprador;

  @ManyToOne(optional = false)
  @JoinColumn(name = "cosecha_id", nullable = false)
  private CosechaEntity cosecha;

  @Column(nullable = false, length = 1000)
  private String mensaje;

  @Column(name = "fecha_creacion", nullable = false)
  private OffsetDateTime fechaCreacion;

  /** Constructor requerido por JPA. */
  protected ContactoEntity() {}

  /** Crea la solicitud sin aceptar un remitente indicado por el navegador. */
  public ContactoEntity(UsuarioEntity comprador, CosechaEntity cosecha,
      String mensaje, OffsetDateTime fechaCreacion) {
    this.comprador = comprador;
    this.cosecha = cosecha;
    this.mensaje = mensaje;
    this.fechaCreacion = fechaCreacion;
  }

  public Long getId() {
    return id;
  }

  public UsuarioEntity getComprador() {
    return comprador;
  }

  public CosechaEntity getCosecha() {
    return cosecha;
  }

  public String getMensaje() {
    return mensaje;
  }

  public OffsetDateTime getFechaCreacion() {
    return fechaCreacion;
  }
}
