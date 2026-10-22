package co.edu.uniajc.agrovalle.security;

import co.edu.uniajc.agrovalle.models.Agricultor;
import co.edu.uniajc.agrovalle.models.RolUsuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

/** Emite y valida tokens firmados para las solicitudes autenticadas. */
@Service
public class TokenJwtService {

  private final SecretKey signingKey;
  private final long expirationSeconds;

  /** Construye la clave desde una variable Base64 y configura la vigencia del token. */
  public TokenJwtService(
      @Value("${security.jwt.secret}") String encodedSecret,
      @Value("${security.jwt.expiration-seconds:3600}") long expirationSeconds) {
    if (encodedSecret == null || encodedSecret.isBlank()) {
      throw new IllegalStateException("JWT_SECRET es obligatorio");
    }
    if (expirationSeconds <= 0) {
      throw new IllegalStateException("JWT_EXPIRATION_SECONDS debe ser positivo");
    }
    this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(encodedSecret));
    this.expirationSeconds = expirationSeconds;
  }

  /** Emite un token con la vigencia configurada. */
  public String generar(Agricultor usuario) {
    return generar(usuario, Duration.ofSeconds(expirationSeconds));
  }

  /** Emite un token con una vigencia explícita para comprobar casos límite. */
  public String generar(Agricultor usuario, Duration vigencia) {
    Instant issuedAt = Instant.now();
    Instant expiresAt = issuedAt.plus(vigencia);
    return Jwts.builder()
        .subject(Long.toString(usuario.id()))
        .claim("correo", usuario.correo())
        .claim("rol", usuario.rol().name())
        .issuedAt(Date.from(issuedAt))
        .expiration(Date.from(expiresAt))
        .signWith(signingKey)
        .compact();
  }

  /** Verifica firma, expiración y datos mínimos de identidad. */
  public UsuarioAutenticado validar(String token) {
    try {
      Claims claims = Jwts.parser()
          .verifyWith(signingKey)
          .build()
          .parseSignedClaims(token)
          .getPayload();
      String subject = claims.getSubject();
      String correo = claims.get("correo", String.class);
      String role = claims.get("rol", String.class);
      if (subject == null || correo == null || role == null) {
        throw new IllegalArgumentException("Faltan datos de identidad");
      }
      return new UsuarioAutenticado(Long.parseLong(subject), correo,
          RolUsuario.valueOf(role));
    } catch (JwtException | IllegalArgumentException exception) {
      throw new BadCredentialsException("Token inválido o vencido", exception);
    }
  }

  /** Expone la vigencia para que el cliente pueda renovar su sesión. */
  public long expirationSeconds() {
    return expirationSeconds;
  }
}
