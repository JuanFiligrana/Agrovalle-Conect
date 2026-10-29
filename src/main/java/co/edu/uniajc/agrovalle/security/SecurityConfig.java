package co.edu.uniajc.agrovalle.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/** Configura autenticación sin sesión para la API REST. */
@Configuration
public class SecurityConfig {

  /** Protege la consulta de identidad y mantiene públicas las rutas existentes. */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
      JwtAuthenticationFilter jwtFilter,
      RestAuthenticationEntryPoint authenticationEntryPoint) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(errors ->
            errors.authenticationEntryPoint(authenticationEntryPoint))
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(HttpMethod.POST, "/api/v1/productos").authenticated()
            .requestMatchers("/api/v1/auth/me", "/api/v1/fincas",
                "/api/v1/fincas/**", "/api/v1/notificaciones/cosechas",
                "/api/v1/contacto", "/api/v1/contacto/**").authenticated()
            .anyRequest().permitAll())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
