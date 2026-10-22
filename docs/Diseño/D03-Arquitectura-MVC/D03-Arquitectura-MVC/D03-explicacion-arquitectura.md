# D03 · Explicación de la arquitectura

## Propósito y base del diseño

Separar la interfaz, las decisiones del negocio y el almacenamiento de AgroValle Connect, usando Spring Boot y el esquema PostgreSQL definido en D02. Se conserva la organización de **cuatro capas** indicada en la transcripción: presentación, lógica de negocio, acceso a datos y persistencia física.

La base revisada es la rama `chore/sprint-0-completo` del repositorio, commit `0727be3ad29af86c39fc07ccad1a145143289643`. Sus repositorios almacenan datos en `ConcurrentHashMap`; el driver PostgreSQL declarado en Maven no equivale a una conexión activa. El diagrama es el diseño objetivo para evolucionar ese prototipo.

## Las cuatro capas

| Capa | Elementos y responsabilidad | Límite de responsabilidad |
|---|---|---|
| 1. Presentación y API REST | Vista HTML/CSS/JavaScript; controladores REST; DTO de entrada y salida; validación de formato; traducción de errores a HTTP. | Envía y recibe solicitudes. Delega las reglas de negocio y el almacenamiento. |
| 2. Lógica de negocio y dominio | Servicios de aplicación, reglas, entidades del dominio y contratos de repositorio. El servicio define la unidad transaccional del caso de uso. | Decide si una operación está permitida. Los contratos expresan qué datos necesita, sin imponer una implementación SQL. |
| 3. Acceso a datos | Adaptadores que implementan los contratos; interfaces Spring Data JPA; Hibernate, consultas y proyecciones; uso de los mapeos de D02. | Materializa lecturas y escrituras. La decisión de autorización permanece en el servicio. |
| 4. Persistencia relacional | Motor PostgreSQL, esquema `agrovalle`, tablas, vistas, índices y restricciones de D02. | Conserva datos y asegura la integridad declarativa. Las reglas entre filas que requieren transacciones las coordina la aplicación. |

Los contratos de repositorio se ubican en la capa 2 porque expresan las necesidades de los casos de uso. Sus implementaciones están en la capa 3. Esa decisión permite aplicar DIP sin introducir una quinta capa.

## Dónde están la vista, el controlador y el modelo

**Vista:** se ejecuta en el navegador. La página existente está en `src/main/resources/static/index.html`; muestra información y consulta el estado del servicio. Los formularios completos de HU01–05 son una ampliación prevista, relacionada con los mockups de D07.

**Controlador:** son las clases `AuthController`, `FincaController`, `ProductoController`, `PrecioController`, `ContactoController` y `EstadoController`, dentro de presentación. Spring MVC resuelve rutas y parámetros; el controlador valida el contrato, llama al servicio y devuelve una respuesta. `ApiExceptionHandler` centraliza la traducción de errores a HTTP.

**Modelo:** abarca el estado y las reglas del dominio en la capa 2 y el acceso a esos datos en la capa 3. PostgreSQL conserva su representación relacional en la capa 4. El término «modelo» no se limita al paquete llamado `models`, ni significa que el navegador se conecte a la base de datos.

En esta aplicación REST, la vista consume JSON. El controlador REST no tiene que devolver una plantilla HTML para mantener esta separación de responsabilidades.

## Comunicación y dependencias

1. El navegador envía una petición HTTP a `/api/v1/...` y recibe JSON. El puerto local configurado en el prototipo es 8081; la topología, protocolos y puertos se detallarán en D04.
2. El controlador convierte los datos del contrato y delega el caso de uso en un servicio.
3. El servicio valida reglas y usa contratos como `CosechaPort` o `ContactoPort`, recibidos por constructor.
4. Spring conecta esos contratos con adaptadores de acceso a datos. El adaptador usa repositorios JPA o proyecciones, y Hibernate accede a PostgreSQL mediante JDBC.
5. El resultado se convierte en DTO de respuesta y vuelve al navegador. Las entidades persistentes no se serializan completas como contrato público; esto evita exponer campos internos y asociaciones innecesarias.

Las flechas continuas del dibujo indican uso o delegación. Las discontinuas con triángulo vacío indican que un adaptador implementa un contrato; no representan el sentido de una solicitud HTTP.

Las entidades propuestas en D02 se conservan en `models` con sus anotaciones JPA. Esta opción comparte el modelo entre negocio y acceso a datos; no pretende un dominio completamente independiente de Jakarta Persistence. Los contratos sí separan a los servicios de la implementación concreta de almacenamiento.

## Organización de paquetes propuesta

Se conserva la raíz `co.edu.uniajc.agrovalle`.

| Ubicación | Contenido |
|---|---|
| `controllers` | Rutas REST y traducción de excepciones de negocio a HTTP. |
| `dto` | Contratos de petición y respuesta, sin exponer contraseñas ni entidades completas. |
| `services` | Casos de uso y unidades transaccionales. |
| `models` | Modelo del dominio y entidades JPA del diseño D02. |
| `ports` — nuevo | Interfaces pequeñas que los servicios necesitan para acceder a datos. |
| `repositories.jpa` — nuevo | Adaptadores y repositorios Spring Data JPA. |
| `repositories.memory` — reorganización prevista | Implementaciones temporales o dobles de prueba que cumplan los mismos contratos. |
| `config` — según necesidad | Selección de implementaciones y configuración técnica. |
| `resources/static` | Vista HTML/CSS/JavaScript. |

Si hay implementaciones en memoria y JPA activas, seleccionar una mediante configuración, perfil o calificador. No registrar dos candidatos ambiguos para el mismo contrato.

## Aplicación de SRP y DIP

**CosechaService:** conservar publicación y validación de ofertas. Extraer la consulta histórica de precios a `ConsultaPreciosService`; así un cambio en el cálculo del promedio no obliga a modificar la publicación. El servicio recibe `CosechaPort`, `FincaPort` y `UsuarioPort`; las excepciones del negocio se traducen a HTTP en presentación.

**ContactoService:** conservar la validación del comprador y de la oferta y la coordinación del registro. Delegar escrituras en `ContactoPort` y `NotificacionPort`; registrar ambos dentro de una transacción. Una falla al crear el aviso debe revertir también el contacto. El destinatario se deriva de la oferta y su finca, según D02.

Estos son dos componentes candidatos a refactorización para el siguiente incremento. El repositorio actual inyecta clases concretas y usa `ResponseStatusException` en servicios; todavía hay que sustituir ese acoplamiento. D03 define la propuesta, sin afirmar que la refactorización ya esté ejecutada ni probada.

## Patrones y mecanismos del diseño

| Patrón o mecanismo | Aplicación propuesta y estado |
|---|---|
| Repository | Contratos de negocio pequeños más adaptadores JPA. Los repositorios actuales en memoria son el punto de partida. |
| DTO | Ya hay DTO en el prototipo; completar respuestas uniformes en los endpoints que devuelven modelos directamente. |
| Data Mapper | Hibernate representa entidades como filas mediante el mapeo de D02. La conversión DTO ↔ dominio es una responsabilidad distinta. |
| Singleton administrado por Spring | Servicios y adaptadores compartidos mediante el contenedor. No guardar el usuario, el carrito ni datos de una solicitud en campos mutables compartidos. Es por bean y contenedor, no una instancia global por toda la JVM. |
| Builder | Previsto para construir un `Pedido` con sus detalles históricos al confirmar reservas. La transacción y las validaciones siguen siendo responsabilidad del servicio. No forma parte del código implementado del prototipo. |

Repository, DTO y Data Mapper son patrones de aplicación/persistencia; Builder es un patrón de creación. La mención de varios patrones en clase no significa que todos sean patrones GoF ni que ya estén implementados.

## Transacciones y consistencia

El límite transaccional se coloca en el servicio del caso de uso cuando participa más de un repositorio. Para el registro, D02 necesita crear `usuario` y exactamente un subtipo como una unidad. Para contacto, necesita registrar contacto y notificación como una unidad.

La confirmación de un pedido debe coordinar reservas, stock, pedido, detalles e idempotencia; un `CHECK` aislado no ejecuta esa coordinación. El diseño exige bloqueo o actualización condicional para evitar sobreventa. El esquema, los estados y las restricciones específicas están en D02; sus reglas transaccionales deben implementarse y probarse después.

## Alcance

Esta entrega documenta la arquitectura. Continúan pendientes la integración de Spring Data JPA/PostgreSQL, la implementación de contratos y adaptadores, los formularios, la autenticación verificable y las reglas transaccionales. La presencia de una cabecera `Authorization` en el prototipo no acredita validación JWT. Las pruebas de flujo, integración y concurrencia corresponden a la ejecución técnica posterior.

## Fuentes de referencia

- Transcripción de clase compartida: cuatro capas, separación visible de vista/controlador, PostgreSQL y patrones de diseño.
- [Código base revisado](https://github.com/JuanFiligrana/Agrovalle-Conect/tree/0727be3ad29af86c39fc07ccad1a145143289643): controladores, servicios, repositorios, vista y configuración.
- D01 y D02 del mismo Sprint de diseño: dominio, esquema y mapeo previsto.
- [Spring Framework 6.2: controladores anotados](https://docs.spring.io/spring-framework/reference/6.2/web/webmvc/mvc-controller.html).
- [Spring Boot 3.5: inyección de dependencias](https://docs.spring.io/spring-boot/3.5/reference/using/spring-beans-and-dependency-injection.html).
- [Spring Data JPA 3.5: transacciones](https://docs.spring.io/spring-data/jpa/reference/3.5/jpa/transactions.html).
- [Spring Framework 6.2: alcance de los beans](https://docs.spring.io/spring-framework/reference/6.2/core/beans/factory-scopes.html).

Las asignaciones concretas de clases, contratos y carpetas son decisiones de diseño del proyecto; no se presentan como nombres impuestos por la profesora.
