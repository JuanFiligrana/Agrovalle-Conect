package co.edu.uniajc.agrovalle.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/** Devuelve errores de autenticación en el mismo formato JSON que el resto de la API. */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  /** Recibe el serializador compartido por Spring Boot. */
  public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /** Responde sin redirecciones cuando falta un token o este no es válido. */
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    Map<String, String> body = new LinkedHashMap<>();
    body.put("status", HttpStatus.UNAUTHORIZED.toString());
    body.put("message", "Token ausente, inválido o vencido");
    objectMapper.writeValue(response.getWriter(), body);
  }
}
