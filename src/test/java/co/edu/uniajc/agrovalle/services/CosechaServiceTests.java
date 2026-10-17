package co.edu.uniajc.agrovalle.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import co.edu.uniajc.agrovalle.dto.CosechaRequest;
import co.edu.uniajc.agrovalle.exceptions.OperacionNoPermitidaException;
import co.edu.uniajc.agrovalle.exceptions.RecursoNoEncontradoException;
import co.edu.uniajc.agrovalle.models.Agricultor;
import co.edu.uniajc.agrovalle.models.Cosecha;
import co.edu.uniajc.agrovalle.models.Finca;
import co.edu.uniajc.agrovalle.models.RolUsuario;
import co.edu.uniajc.agrovalle.ports.CosechaPort;
import co.edu.uniajc.agrovalle.ports.FincaPort;
import co.edu.uniajc.agrovalle.ports.UsuarioPort;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Regresión de publicación, propiedad y filtros usando contratos de datos aislados. */
@ExtendWith(MockitoExtension.class)
class CosechaServiceTests {

  @Mock
  private CosechaPort cosechas;

  @Mock
  private FincaPort fincas;

  @Mock
  private UsuarioPort usuarios;

  private CosechaService service;

  @BeforeEach
  void prepararServicio() {
    service = new CosechaService(cosechas, fincas, usuarios);
  }

  @Test
  void publicaUnaOfertaActivaEnLaFincaDelAgricultor() {
    when(usuarios.findById(7)).thenReturn(Optional.of(cuenta(RolUsuario.AGRICULTOR)));
    when(fincas.findById(9)).thenReturn(Optional.of(finca(9, 7, "Dagua")));
    Cosecha guardada = cosecha(101, 9, "Mango", "Frutas", true);
    when(cosechas.save(any(Cosecha.class))).thenReturn(guardada);

    assertSame(guardada, service.publicar(solicitud()));

    ArgumentCaptor<Cosecha> captor = ArgumentCaptor.forClass(Cosecha.class);
    verify(cosechas).save(captor.capture());
    assertEquals(new Cosecha(0, 7, 9, "Mango", "Frutas", new BigDecimal("250.000"),
        new BigDecimal("3500.00"), LocalDate.of(2030, 1, 1), true), captor.getValue());
  }

  @Test
  void noPublicaParaUnaCuentaInexistente() {
    when(usuarios.findById(7)).thenReturn(Optional.empty());

    var error = assertThrows(RecursoNoEncontradoException.class,
        () -> service.publicar(solicitud()));

    assertEquals("No existe el usuario 7", error.getMessage());
    verifyNoInteractions(fincas, cosechas);
  }

  @Test
  void unCompradorNoPuedePublicarUnaCosecha() {
    when(usuarios.findById(7)).thenReturn(Optional.of(cuenta(RolUsuario.COMPRADOR)));

    assertThrows(OperacionNoPermitidaException.class, () -> service.publicar(solicitud()));

    verifyNoInteractions(fincas, cosechas);
  }

  @Test
  void noPublicaSiLaFincaNoExiste() {
    when(usuarios.findById(7)).thenReturn(Optional.of(cuenta(RolUsuario.AGRICULTOR)));
    when(fincas.findById(9)).thenReturn(Optional.empty());

    var error = assertThrows(RecursoNoEncontradoException.class,
        () -> service.publicar(solicitud()));

    assertEquals("No existe la finca", error.getMessage());
    verifyNoInteractions(cosechas);
  }

  @Test
  void noGuardaUnaCosechaEnUnaFincaAjena() {
    when(usuarios.findById(7)).thenReturn(Optional.of(cuenta(RolUsuario.AGRICULTOR)));
    when(fincas.findById(9)).thenReturn(Optional.of(finca(9, 8, "Dagua")));

    var error = assertThrows(OperacionNoPermitidaException.class,
        () -> service.publicar(solicitud()));

    assertEquals("La finca no pertenece al agricultor", error.getMessage());
    verifyNoInteractions(cosechas);
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
  void permiteOmitirLosFiltrosConservandoSoloLasOfertasActivas() {
    Cosecha activa = cosecha(101, 9, "Mango", "Frutas", true);
    when(cosechas.findAll()).thenReturn(List.of(activa,
        cosecha(102, 9, "Mango", "Frutas", false)));

    assertEquals(List.of(activa), service.listar(null, " "));
    verifyNoInteractions(fincas, usuarios);
  }

  @Test
  void unaOfertaSinFincaNoCoincideConElMunicipioSolicitado() {
    when(cosechas.findAll()).thenReturn(List.of(cosecha(101, 9, "Mango", "Frutas", true)));
    when(fincas.findById(9)).thenReturn(Optional.empty());

    assertEquals(List.of(), service.listar("Dagua", null));
  }

  @Test
  void obtieneUnaOfertaExistente() {
    Cosecha existente = cosecha(101, 9, "Mango", "Frutas", true);
    when(cosechas.findById(101)).thenReturn(existente);

    assertSame(existente, service.require(101));
  }

  @Test
  void informaLaAusenciaDeUnaOferta() {
    var error = assertThrows(RecursoNoEncontradoException.class, () -> service.require(404));

    assertEquals("No existe la cosecha 404", error.getMessage());
  }

  private CosechaRequest solicitud() {
    return new CosechaRequest(7, 9, " Mango ", " Frutas ", new BigDecimal("250.000"),
        new BigDecimal("3500.00"), LocalDate.of(2030, 1, 1));
  }

  private Agricultor cuenta(RolUsuario rol) {
    return new Agricultor(7, "Persona de prueba", "persona@example.invalid", "documento-7",
        "Dagua", rol, "hash-de-prueba");
  }

  private Finca finca(long id, long propietario, String municipio) {
    return new Finca(id, propietario, "Finca de prueba", municipio, "Origen de prueba");
  }

  private Cosecha cosecha(long id, long fincaId, String tipo, String categoria, boolean activa) {
    return new Cosecha(id, 7, fincaId, tipo, categoria, new BigDecimal("250.000"),
        new BigDecimal("3500.00"), LocalDate.of(2030, 1, 1), activa);
  }
}
