package co.edu.uniajc.agrovalle.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import co.edu.uniajc.agrovalle.dto.ContactoRequest;
import co.edu.uniajc.agrovalle.exceptions.OperacionNoPermitidaException;
import co.edu.uniajc.agrovalle.exceptions.RecursoNoEncontradoException;
import co.edu.uniajc.agrovalle.models.Agricultor;
import co.edu.uniajc.agrovalle.models.Contacto;
import co.edu.uniajc.agrovalle.models.Cosecha;
import co.edu.uniajc.agrovalle.models.NotificacionContacto;
import co.edu.uniajc.agrovalle.models.RolUsuario;
import co.edu.uniajc.agrovalle.ports.ContactoPort;
import co.edu.uniajc.agrovalle.ports.CosechaPort;
import co.edu.uniajc.agrovalle.ports.UsuarioPort;
import co.edu.uniajc.agrovalle.security.UsuarioAutenticado;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Comprueba permisos y escritura del contacto sin iniciar Spring ni una base de datos. */
@ExtendWith(MockitoExtension.class)
class ContactoServiceTests {

  @Mock
  private ContactoPort contactos;

  @Mock
  private UsuarioPort usuarios;

  @Mock
  private CosechaPort cosechas;

  private ContactoService service;

  @BeforeEach
  void prepararServicio() {
    service = new ContactoService(contactos, usuarios, cosechas);
  }

  @Test
  void registraElMensajeDeUnCompradorConElIdDevueltoPorElAlmacenamiento() {
    when(usuarios.findById(8)).thenReturn(Optional.of(cuenta(RolUsuario.COMPRADOR)));
    when(cosechas.findById(101)).thenReturn(oferta());
    when(contactos.save(any(Contacto.class)))
        .thenReturn(new Contacto(548, 8, 101, "Quiero conocer la disponibilidad", "REGISTRADO"));

    var respuesta = service.registrar(solicitud(), identidad());

    assertEquals(548, respuesta.id());
    assertEquals(101, respuesta.productoId());
    assertEquals("REGISTRADO", respuesta.estado());
    assertEquals(true, respuesta.notificacionRegistrada());
    ArgumentCaptor<Contacto> captor = ArgumentCaptor.forClass(Contacto.class);
    verify(contactos).save(captor.capture());
    assertEquals(new Contacto(0, 8, 101, "Quiero conocer la disponibilidad", "REGISTRADO"),
        captor.getValue());
  }

  @Test
  void noRegistraUnaSolicitudDeUnaCuentaInexistente() {
    when(usuarios.findById(8)).thenReturn(Optional.empty());

    var error = assertThrows(RecursoNoEncontradoException.class,
        () -> service.registrar(solicitud(), identidad()));

    assertEquals("No existe el usuario 8", error.getMessage());
    verifyNoInteractions(contactos, cosechas);
  }

  @Test
  void noRegistraSolicitudesDeUnAgricultor() {
    when(usuarios.findById(8)).thenReturn(Optional.of(cuenta(RolUsuario.AGRICULTOR)));

    assertThrows(OperacionNoPermitidaException.class,
        () -> service.registrar(solicitud(), identidad()));

    verifyNoInteractions(contactos, cosechas);
  }

  @Test
  void noGuardaUnContactoParaUnaOfertaInexistente() {
    when(usuarios.findById(8)).thenReturn(Optional.of(cuenta(RolUsuario.COMPRADOR)));

    var error = assertThrows(RecursoNoEncontradoException.class,
        () -> service.registrar(solicitud(), identidad()));

    assertEquals("No existe la cosecha 101", error.getMessage());
    verifyNoInteractions(contactos);
  }

  @Test
  void noDevuelveExitoSiFallaElAlmacenamientoDelContacto() {
    when(usuarios.findById(8)).thenReturn(Optional.of(cuenta(RolUsuario.COMPRADOR)));
    when(cosechas.findById(101)).thenReturn(oferta());
    var fallo = new IllegalStateException("Almacenamiento de prueba no disponible");
    when(contactos.save(any(Contacto.class))).thenThrow(fallo);

    assertSame(fallo, assertThrows(IllegalStateException.class,
        () -> service.registrar(solicitud(), identidad())));
  }

  private ContactoRequest solicitud() {
    return new ContactoRequest(101, " Quiero conocer la disponibilidad ");
  }

  @Test
  void rechazaRolDelTokenAntesDeConsultarDatos() {
    var agricultor = new UsuarioAutenticado(7, "agricultor@example.invalid",
        RolUsuario.AGRICULTOR);
    assertThrows(OperacionNoPermitidaException.class,
        () -> service.registrar(solicitud(), agricultor));
    verifyNoInteractions(contactos, usuarios, cosechas);
  }

  @Test
  void rechazaUnaOfertaInactiva() {
    when(usuarios.findById(8)).thenReturn(Optional.of(cuenta(RolUsuario.COMPRADOR)));
    Cosecha base = oferta();
    when(cosechas.findById(101)).thenReturn(new Cosecha(base.id(), base.agricultorId(),
        base.fincaId(), base.tipo(), base.categoria(), base.cantidadKg(), base.precioCopKg(),
        base.fechaCosecha(), false));
    assertThrows(RecursoNoEncontradoException.class,
        () -> service.registrar(solicitud(), identidad()));
    verifyNoInteractions(contactos);
  }

  @Test
  void consultaSoloLaBandejaDeLaIdentidadVerificada() {
    service.listar(identidad());
    verify(contactos).findByUsuario(8);
  }

  @Test
  void marcaLaNotificacionPropiaComoLeida() {
    when(contactos.findNotificacion(1)).thenReturn(Optional.of(
        new NotificacionContacto(1, 2, 8, false)));
    service.marcarLeida(1, identidad());
    verify(contactos).marcarLeida(1, 8);
  }

  @Test
  void impideLeerLaNotificacionAjena() {
    when(contactos.findNotificacion(1)).thenReturn(Optional.of(
        new NotificacionContacto(1, 2, 7, false)));
    assertThrows(OperacionNoPermitidaException.class, () -> service.marcarLeida(1, identidad()));
  }

  @Test
  void informaUnaNotificacionInexistente() {
    when(contactos.findNotificacion(1)).thenReturn(Optional.empty());
    assertThrows(RecursoNoEncontradoException.class, () -> service.marcarLeida(1, identidad()));
  }

  private UsuarioAutenticado identidad() {
    return new UsuarioAutenticado(8, "comprador@example.invalid", RolUsuario.COMPRADOR);
  }

  private Agricultor cuenta(RolUsuario rol) {
    return new Agricultor(8, "Persona de prueba", "comprador@example.invalid", "documento-8",
        "Cali", rol, "hash-de-prueba");
  }

  private Cosecha oferta() {
    return new Cosecha(101, 7, 9, "Mango", "Frutas", new BigDecimal("250"),
        new BigDecimal("3500"), LocalDate.of(2030, 1, 1), true);
  }
}
