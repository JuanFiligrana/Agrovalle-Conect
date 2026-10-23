package co.edu.uniajc.agrovalle.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import co.edu.uniajc.agrovalle.dto.CosechaRequest;
import co.edu.uniajc.agrovalle.events.CosechaPublicadaEvent;
import co.edu.uniajc.agrovalle.models.Cosecha;
import co.edu.uniajc.agrovalle.models.Finca;
import co.edu.uniajc.agrovalle.models.RolUsuario;
import co.edu.uniajc.agrovalle.ports.CosechaPort;
import co.edu.uniajc.agrovalle.ports.FincaPort;
import co.edu.uniajc.agrovalle.security.UsuarioAutenticado;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** TDD de publicación, propiedad, catálogo y filtros del Sprint 2. */
@ExtendWith(MockitoExtension.class)
class CosechaServiceTests {

  private static final ZoneId ZONA_COLOMBIA = ZoneId.of("America/Bogota");

  @Mock
  private CosechaPort cosechas;

  @Mock
  private FincaPort fincas;

  @Mock
  private ApplicationEventPublisher events;

  private CosechaService service;

  @BeforeEach
  void prepararServicio() {
    service = new CosechaService(cosechas, fincas, events);
  }

  @Test
  void publicaUnaOfertaActivaUsandoLaIdentidadDelJwt() {
    UsuarioAutenticado usuario = usuario(7, RolUsuario.AGRICULTOR);
    when(fincas.findById(9)).thenReturn(Optional.of(finca(9, 7, "Dagua")));
    Cosecha guardada = cosecha(101, 9, "Mango", "Frutas", true);
    when(cosechas.save(any(Cosecha.class))).thenReturn(guardada);

    assertSame(guardada, service.publicar(solicitud(), usuario));

    ArgumentCaptor<Cosecha> oferta = ArgumentCaptor.forClass(Cosecha.class);
    verify(cosechas).save(oferta.capture());
    assertEquals(new Cosecha(0, 7, 9, "Mango", "Frutas", new BigDecimal("250.000"),
        new BigDecimal("3500.00"), LocalDate.now(ZONA_COLOMBIA).plusDays(1), true),
        oferta.getValue());
    verify(events).publishEvent(new CosechaPublicadaEvent(guardada));
  }

  @Test
  void unCompradorNoPuedePublicarUnaCosecha() {
    var error = assertThrows(ResponseStatusException.class,
        () -> service.publicar(solicitud(), usuario(7, RolUsuario.COMPRADOR)));

    assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
    verifyNoInteractions(fincas, cosechas, events);
  }

  @Test
  void noPublicaSiLaFincaNoExiste() {
    when(fincas.findById(9)).thenReturn(Optional.empty());

    var error = assertThrows(ResponseStatusException.class,
        () -> service.publicar(solicitud(), usuario(7, RolUsuario.AGRICULTOR)));

    assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
    verifyNoInteractions(cosechas, events);
  }

  @Test
  void noGuardaUnaCosechaEnUnaFincaAjena() {
    when(fincas.findById(9)).thenReturn(Optional.of(finca(9, 8, "Dagua")));

    var error = assertThrows(ResponseStatusException.class,
        () -> service.publicar(solicitud(), usuario(7, RolUsuario.AGRICULTOR)));

    assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
    verifyNoInteractions(cosechas, events);
  }

  @Test
  void rechazaUnaCategoriaFueraDelCatalogo() {
    CosechaRequest request = new CosechaRequest(9, "Mango", "Electrodomesticos",
        new BigDecimal("250"), new BigDecimal("3500"),
        LocalDate.now(ZONA_COLOMBIA).plusDays(1));

    var error = assertThrows(ResponseStatusException.class,
        () -> service.publicar(request, usuario(7, RolUsuario.AGRICULTOR)));

    assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    verifyNoInteractions(fincas, cosechas, events);
  }

  @Test
  void rechazaUnaFechaAnteriorAlDiaActualEnColombia() {
    CosechaRequest request = new CosechaRequest(9, "Mango", "Frutas",
        new BigDecimal("250"), new BigDecimal("3500"),
        LocalDate.now(ZONA_COLOMBIA).minusDays(1));

    var error = assertThrows(ResponseStatusException.class,
        () -> service.publicar(request, usuario(7, RolUsuario.AGRICULTOR)));

    assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    verifyNoInteractions(fincas, cosechas, events);
  }

  @Test
  void filtraOfertasActivasPorMunicipioConCategoria() {
    Cosecha correcta = cosecha(101, 9, "Mango", "Frutas", true);
    Cosecha otroMunicipio = cosecha(102, 10, "Banano", "Frutas", true);
    Cosecha otraCategoria = cosecha(103, 9, "Cafe", "Granos", true);
    Cosecha inactiva = cosecha(104, 9, "Mango", "Frutas", false);
    when(cosechas.findAll()).thenReturn(List.of(correcta, otroMunicipio, otraCategoria, inactiva));
    when(fincas.findById(9)).thenReturn(Optional.of(finca(9, 7, "Dagua")));
    when(fincas.findById(10)).thenReturn(Optional.of(finca(10, 7, "Cali")));

    assertEquals(List.of(correcta), service.listar(" dagua ", " frutas "));
  }

  @Test
  void unaCategoriaInvalidaProduceBadRequest() {
    var error = assertThrows(ResponseStatusException.class,
        () -> service.listar("Dagua", "Categoria inventada"));

    assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    verifyNoInteractions(cosechas, fincas, events);
  }

  @Test
  void unMunicipioInvalidoProduceBadRequest() {
    var error = assertThrows(ResponseStatusException.class,
        () -> service.listar("Bogota", "Frutas"));

    assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    verifyNoInteractions(cosechas, fincas, events);
  }

  @Test
  void permiteOmitirLosFiltrosConservandoSoloLasOfertasActivas() {
    Cosecha activa = cosecha(101, 9, "Mango", "Frutas", true);
    when(cosechas.findAll()).thenReturn(List.of(activa,
        cosecha(102, 9, "Mango", "Frutas", false)));

    assertEquals(List.of(activa), service.listar(null, " "));
    verifyNoInteractions(fincas, events);
  }

  @Test
  void obtieneUnaOfertaExistente() {
    Cosecha existente = cosecha(101, 9, "Mango", "Frutas", true);
    when(cosechas.findById(101)).thenReturn(existente);

    assertSame(existente, service.require(101));
  }

  @Test
  void informaLaAusenciaDeUnaOferta() {
    var error = assertThrows(ResponseStatusException.class, () -> service.require(404));

    assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
  }

  private CosechaRequest solicitud() {
    return new CosechaRequest(9, " Mango ", " frutas ", new BigDecimal("250.000"),
        new BigDecimal("3500.00"), LocalDate.now(ZONA_COLOMBIA).plusDays(1));
  }

  private UsuarioAutenticado usuario(long id, RolUsuario rol) {
    return new UsuarioAutenticado(id, "persona@example.invalid", rol);
  }

  private Finca finca(long id, long propietario, String municipio) {
    return new Finca(id, propietario, "Finca de prueba", municipio, "Origen de prueba");
  }

  private Cosecha cosecha(long id, long fincaId, String tipo, String categoria, boolean activa) {
    return new Cosecha(id, 7, fincaId, tipo, categoria, new BigDecimal("250.000"),
        new BigDecimal("3500.00"), LocalDate.now(ZONA_COLOMBIA).plusDays(1), activa);
  }
}
