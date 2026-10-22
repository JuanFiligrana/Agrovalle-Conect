# R01 · Refactorización de servicios con SRP y DIP

> **Nota de vigencia:** este informe describe el estado histórico de R01 y sus pruebas con
> Java 25. Desde entonces el repositorio incorporó autenticación JWT, adaptadores
> JPA/PostgreSQL y la aplicación web responsive; la configuración vigente usa Java 17. Los
> pendientes y resultados citados abajo se mantienen como evidencia de aquel incremento,
> no como diagnóstico del código actual.

## Problema y resultado

`CosechaService` reunía publicación, filtros y cálculo de precios, y recibía repositorios y servicios concretos. `ContactoService` dependía de otros dos servicios concretos y de un repositorio. Ambos elegían directamente respuestas HTTP desde las reglas del caso de uso.

La consulta de precios se separa en `ConsultaPreciosService`. Los servicios refactorizados reciben interfaces pequeñas de datos por constructor; sus errores de negocio se traducen a HTTP en presentación. Se conservan las rutas, los DTO y el comportamiento observable del prototipo.

Base de trabajo: commit `4c4506d07758ee1f4e292b545052de71ac37d79e`, rama `chore/sprint-0-completo`. Rama prevista para el cambio: `refactor/r01-servicios-srp-dip`. Tarea del tablero: R01, actualización del Sprint 1 solicitada en clase.

## Cambios y principios

| Componente | Antes | Después | Principio |
|---|---|---|---|
| CosechaService | Publicación, consulta de ofertas y promedio; repositorios y servicio de usuarios concretos. | Publicación y consulta de ofertas, mediante CosechaPort, FincaPort y UsuarioPort. | SRP y DIP. |
| ContactoService | Coordina contacto, pero llama a AgricultorService/CosechaService y repositorio concretos; decide errores HTTP. | Consulta UsuarioPort/CosechaPort y escribe mediante ContactoPort; comunica errores del caso de uso. | DIP y separación de responsabilidades de negocio/presentación. |
| ConsultaPreciosService | El cálculo estaba dentro de CosechaService. | Caso de uso dedicado al promedio actual del prototipo, con CosechaPort como dependencia. | SRP y DIP. |
| ApiExceptionHandler | Traduce ResponseStatusException y validaciones. | También traduce RecursoNoEncontradoException a 404 y OperacionNoPermitidaException a 403. | Responsabilidad HTTP en presentación. |
| Repositorios en memoria | Clases concretas usadas directamente por los servicios originales. | Conservan sus almacenes e implementan los contratos requeridos por R01. | Los adaptadores dependen de abstracciones. |

La inyección de constructor ya existía; el cambio de DIP consiste en depender de contratos en vez de implementaciones concretas. No se añaden inyecciones de campos ni un localizador de servicios. Los servicios mantienen `@Service` para su integración con Spring; retirar HTTP del negocio no equivale a afirmar que todo el dominio sea independiente del framework.

## Puertos y adaptadores actuales

| Puerto del negocio | Operaciones de este incremento | Implementación en memoria |
|---|---|---|
| UsuarioPort | findById | AgricultorRepository. El modelo actual Agricultor también representa cuentas COMPRADOR mediante su rol. |
| FincaPort | findById | FincaRepository. |
| CosechaPort | save, findAll, findById | CosechaRepository. |
| ContactoPort | save | ContactoRepository. |

Los contratos se encuentran en `co.edu.uniajc.agrovalle.ports`. Las dos excepciones nuevas solo dependen de Java. `CosechaPort.findById` conserva por compatibilidad el retorno null del repositorio actual; los demás puertos de consulta conservan Optional. La escritura y el flujo de alta de cuentas/fincas originales continúan disponibles.

Cada interfaz tiene un único bean concreto en este incremento; no hay dos adaptadores competidores ni un selector de implementación nuevo. Los puertos de lectura evitan incorporar operaciones que estos consumidores no necesitan.

R01 aplica una parte de D03 y D06. Los adaptadores JPA, CatalogoPort, PrecioPort histórico y NotificacionPort aún necesitan implementarse con las funcionalidades correspondientes. `findAll` permite conservar las consultas del prototipo; no se presenta como la consulta SQL histórica diseñada para HU03. `AgricultorService` y `FincaService` mantienen su código previo.

## Compatibilidad y pendientes funcionales

Las operaciones REST existentes se conservan. `PrecioController` mantiene `GET /api/v1/precios/promedio?producto=...` y delega en el nuevo servicio. Los errores de los dos servicios conservan sus estados y el formato actual `status`/`message`; el formato objetivo `code`/`message`/`fields` continúa pendiente.

Esta tarea caracteriza y conserva el prototipo. No cambia deliberadamente estas limitaciones:

- Los datos se guardan en memoria y se pierden al reiniciar. El driver PostgreSQL no constituye una conexión JPA.
- El promedio sigue utilizando precios de ofertas activas. HU03 requiere líneas históricas de pedidos entregados en las últimas 24 horas; las pruebas de caracterización no acreditan ese requisito.
- El contacto comprueba presencia de Authorization en el controlador. No valida firma, caducidad ni identidad JWT; los IDs de persona de los DTO siguen pendientes de sustituirse por la identidad autenticada.
- El contacto aún no persiste una notificación interna transaccional ni comprueba el estado activo de la oferta. Las reglas del diseño siguen pendientes.
- Continúan pendientes los catálogos y los formularios completos de D07.

Los tests utilizan `Bearer demo-r01` solo para ejercer el comportamiento previo del prototipo; no presentan ese texto como un JWT válido. Tampoco se incorporan pruebas que afirmen como aceptable el contacto con ofertas inactivas.

## Verificación

Se conserva una copia del código base para comparar `clean verify` con el cambio. La evidencia ejecutada se registra en [el resumen R01](evidencias/r01-resumen.json). El log de la versión refactorizada se adjunta en [r01-verify.txt](evidencias/r01-verify.txt).

Se añaden pruebas unitarias de publicación en finca propia, rol incorrecto, recursos ausentes, filtros combinados y ofertas activas. Las pruebas de contacto comprueban los permisos, el mensaje enviado al almacenamiento y la propagación de un fallo de escritura sin devolver una respuesta exitosa. Las pruebas del nuevo servicio de precios caracterizan media simple, redondeo HALF_UP y resultado null/0 sin ofertas coincidentes.

Las pruebas HTTP adicionales comprueban el enlace real de los adaptadores en Spring, los errores JSON de finca ajena y recursos ausentes, el rol de contacto y los contratos de la consulta de precios. Las cinco pruebas previas permanecen en la suite. La verificación usa JDK 25, Maven Wrapper, Checkstyle, JUnit 5 y JaCoCo con el umbral original del 60 %, sin desactivar controles.

El alcance de esa evidencia es ejecución local en Linux. El resultado remoto de GitHub Actions, la aprobación de un compañero y una ejecución en Windows se registran cuando ocurran; no se deducen del resultado local.

### Resultados ejecutados

| Comprobación | Base del Sprint 1 | Después de R01 |
|---|---:|---:|
| Pruebas ejecutadas | 5 | 29 |
| Fallos / errores / omitidas | 0 / 0 / 0 | 0 / 0 / 0 |
| Infracciones de Checkstyle | 0 | 0 |
| Líneas cubiertas / total | 157 / 175 | 181 / 191 |
| Cobertura de líneas JaCoCo | 89,71 % | 94,76 % |

El ciclo de la versión refactorizada terminó con `BUILD SUCCESS` y generó el JAR.
El log conserva avisos de Jansi y Guava del lanzador Maven 3.9.9 con Java 25, y un
aviso de autoacoplamiento de Mockito durante las pruebas. La configuración existente
`mock-maker-subclass` se conserva; no se afirma ausencia de todos los avisos de las
dependencias. Esos mensajes no son infracciones de Checkstyle ni fallos de las pruebas.

## Integración y cierre de la tarea

1. Crear `refactor/r01-servicios-srp-dip` partiendo de `chore/sprint-0-completo`.
2. Aplicar los archivos del cambio conservando sus rutas `src/`, `docs/` y `.github/workflows/ci.yml`.
3. Revisar el diff y abrir un PR con base `chore/sprint-0-completo` y comparación `refactor/r01-servicios-srp-dip`.
4. El workflow mantiene los controles anteriores y añade esa rama de integración a sus bases de PR permitidas. Así el PR enfocado de R01 puede ejecutar el check build sin incluir de golpe todo el historial del Sprint 1 contra main.
5. Enlazar el PR y su check real en R01, mover a revisión respetando WIP y obtener aprobación de otro integrante.
6. Integrar cuando los requisitos estén cumplidos; registrar fecha/hora real y evidencia antes de mover R01 a Hechos.

La preparación del paquete o una compilación correcta no acredita por sí sola que la revisión, la integración o el cierre de la tarea ya ocurrieron.

## Referencias

- [Arquitectura vigente](arquitectura.md), [Sprint 1](sprint1.md), [DoD](dod.md) y [políticas Kanban](kanban-policies.md).
- D03 y D06: separación de negocio, contratos de datos y adaptadores del diseño objetivo.
- [Inyección de dependencias en Spring Framework 6.2](https://docs.spring.io/spring-framework/reference/6.2/core/beans/dependencies/factory-collaborators.html).
- [Traducción de excepciones en Spring MVC 6.2](https://docs.spring.io/spring-framework/reference/6.2/web/webmvc/mvc-controller/ann-exceptionhandler.html).
