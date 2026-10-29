package co.edu.uniajc.agrovalle.repositories.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/** Una notificación interna por contacto, con lectura persistente. */
@Entity
@Table(name = "notificaciones_contacto")
public class NotificacionContactoEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(optional = false)
  @JoinColumn(name = "contacto_id", nullable = false, unique = true)
  private ContactoEntity contacto;

  @ManyToOne(optional = false)
  @JoinColumn(name = "destinatario_id", nullable = false)
  private UsuarioEntity destinatario;

  @Column(nullable = false)
  private boolean leida;

  /** Constructor requerido por JPA. */
  protected NotificacionContactoEntity() {}

  /** Las nuevas notificaciones comienzan sin leer. */
  public NotificacionContactoEntity(ContactoEntity contacto, UsuarioEntity destinatario) {
    this.contacto = contacto;
    this.destinatario = destinatario;
  }

  public Long getId() {
    return id;
  }

  public ContactoEntity getContacto() {
    return contacto;
  }

  public UsuarioEntity getDestinatario() {
    return destinatario;
  }

  public boolean isLeida() {
    return leida;
  }
}
