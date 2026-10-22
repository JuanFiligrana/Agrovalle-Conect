package co.edu.uniajc.agrovalle.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Convierte un Bearer JWT válido en la identidad de la solicitud. */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String BEARER_PREFIX = "Bearer ";
  private final TokenJwtService tokenService;
  private final RestAuthenticationEntryPoint authenticationEntryPoint;

  /** Recibe el servicio de tokens y el manejador uniforme de errores. */
  public JwtAuthenticationFilter(TokenJwtService tokenService,
      RestAuthenticationEntryPoint authenticationEntryPoint) {
    this.tokenService = tokenService;
    this.authenticationEntryPoint = authenticationEntryPoint;
  }

  /** Solo procesa las rutas que requieren identidad durante esta historia. */
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI().substring(request.getContextPath().length());
    boolean protectedPath = "/api/v1/auth/me".equals(path)
        || "/api/v1/fincas".equals(path)
        || path.startsWith("/api/v1/fincas/")
        || "/api/v1/notificaciones/cosechas".equals(path)
        || HttpMethod.POST.matches(request.getMethod()) && "/api/v1/productos".equals(path);
    return !protectedPath;
  }

  /** Valida el token y registra al usuario en Spring Security. */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
      filterChain.doFilter(request, response);
      return;
    }
    try {
      UsuarioAutenticado usuario = tokenService.validar(
          authorization.substring(BEARER_PREFIX.length()).trim());
      var authentication = new UsernamePasswordAuthenticationToken(usuario, null,
          List.of(new SimpleGrantedAuthority("ROLE_" + usuario.rol().name())));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      filterChain.doFilter(request, response);
    } catch (AuthenticationException exception) {
      SecurityContextHolder.clearContext();
      authenticationEntryPoint.commence(request, response, exception);
    }
  }
}
