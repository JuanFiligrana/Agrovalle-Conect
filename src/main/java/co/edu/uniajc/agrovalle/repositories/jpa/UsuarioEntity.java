package co.edu.uniajc.agrovalle.repositories.jpa;

import co.edu.uniajc.agrovalle.models.RolUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Locale;

/** Representación relacional de una cuenta de AgroValle Connect. */
@Entity
@Table(name = "usuarios", uniqueConstraints = {
    @UniqueConstraint(name = "uk_usuarios_correo", columnNames = "correo"),
    @UniqueConstraint(name = "uk_usuarios_cedula", columnNames = "cedula")
})
public class UsuarioEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 120)
  private String nombre;

  @Column(nullable = false, length = 254)
  private String correo;

  @Column(nullable = false, length = 50)
  private String cedula;

  @Column(nullable = false, length = 100)
  private String municipio;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private RolUsuario rol;

  @Column(name = "contrasena_hash", nullable = false, length = 100)
  private String contrasenaHash;

  /** Constructor requerido por JPA. */
  protected UsuarioEntity() {}

  /** Crea una cuenta nueva; PostgreSQL asigna el identificador. */
  public UsuarioEntity(String nombre, String correo, String cedula, String municipio,
      RolUsuario rol, String contrasenaHash) {
    this.nombre = nombre.trim();
    this.correo = correo.trim().toLowerCase(Locale.ROOT);
    this.cedula = cedula.trim().toLowerCase(Locale.ROOT);
    this.municipio = municipio.trim();
    this.rol = rol;
    this.contrasenaHash = contrasenaHash;
  }

  public Long getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }

  public String getCorreo() {
    return correo;
  }

  public String getCedula() {
    return cedula;
  }

  public String getMunicipio() {
    return municipio;
  }

  public RolUsuario getRol() {
    return rol;
  }

  public String getContrasenaHash() {
    return contrasenaHash;
  }
}
