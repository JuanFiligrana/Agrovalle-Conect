# D03 · Explicación de la arquitectura

## Propósito

AgroValle Connect separa la interfaz, los casos de uso, el acceso a datos y la persistencia
física en las cuatro capas solicitadas. El diagrama combina el incremento implementado con
el modelo objetivo D02, y distingue expresamente los módulos futuros.

## Las cuatro capas

| Capa | Elementos actuales | Responsabilidad |
|---|---|---|
| 1. Presentación y API REST | Siete páginas HTML, `site.css`, `site.js`, controladores REST, DTO y `ApiExceptionHandler` | Presentar la aplicación web, validar el formato y traducir resultados a HTTP/JSON. |
| 2. Lógica de negocio y dominio | Servicios, modelos y puertos `UsuarioPort`, `FincaPort`, `CosechaPort` y `ContactoPort` | Aplicar reglas, autorización y límites transaccionales sin depender de SQL. |
| 3. Acceso a datos | Adaptadores en `repositories`, interfaces Spring Data en `repositories.jpa` y entidades JPA | Convertir dominio a filas y ejecutar lecturas o escrituras. |
| 4. Persistencia relacional | PostgreSQL y el DDL de `src/main/resources/schema.sql` | Conservar usuarios, fincas, categorías, productos y cosechas con claves y restricciones. |

Los puertos se ubican en la capa 2 porque expresan las necesidades de los servicios. Sus
implementaciones se ubican en la capa 3. Así se aplica inversión de dependencias sin crear
una quinta capa artificial.

## Vista, controlador y modelo

La **vista** se ejecuta en el navegador. `src/main/resources/static/` contiene una portada
pública orientada al comprador, catálogo, acceso/registro, seguimiento y vistas separadas
para agricultor y administrador. Los módulos aún no terminados en backend se muestran como
prototipo navegable, no como operaciones persistentes.

Los **controladores** exponen autenticación, fincas, productos, precios, contacto, flete,
notificaciones y estado. Spring MVC resuelve rutas y parámetros; cada controlador delega el
caso de uso y `ApiExceptionHandler` transforma los errores.

El **modelo** abarca el dominio y los servicios de la capa 2, más el acceso a datos de la
capa 3. PostgreSQL conserva la representación relacional en la capa 4. El navegador nunca
se conecta directamente a la base de datos.

## Flujo de una solicitud

1. El navegador envía HTTP a `/api/v1/...` y recibe JSON.
2. El controlador valida el contrato y delega en un servicio.
3. El servicio comprueba identidad, rol y reglas, y usa un puerto recibido por constructor.
4. El adaptador implementa ese puerto mediante Spring Data JPA o, en el caso temporal de
   contacto, mediante memoria.
5. Hibernate usa JDBC para PostgreSQL y el resultado vuelve como DTO.

Las flechas continuas del dibujo indican uso o delegación. Las discontinuas con triángulo
vacío indican implementación de un contrato; no representan el sentido de una petición HTTP.

## Estado de materialización

- `AgricultorRepository` implementa `UsuarioPort` mediante `UsuarioJpaRepository`.
- `FincaRepository` implementa `FincaPort` mediante `FincaJpaRepository`.
- `CosechaRepository` implementa `CosechaPort` y coordina finca, categoría, producto y
  cosecha normalizados.
- `ContactoRepository` implementa `ContactoPort` en memoria y continúa como deuda técnica.
- `ConsultaPreciosService` consulta las cosechas del incremento; la ventana histórica sobre
  pedidos entregados pertenece al modelo D02 futuro.
- La autenticación JWT valida identidad y rol en las rutas protegidas.
- Strategy calcula el flete y Observer registra avisos de cosecha sin acoplar el servicio a
  un destinatario concreto.

## Persistencia incremental y modelo objetivo

El DDL ejecutable del incremento contiene cinco tablas. D02 conserva el diseño objetivo de
17 tablas y 3 vistas para compradores especializados, carrito/reservas, pedidos, entregas,
eventos de seguimiento, contacto persistente, notificaciones e idempotencia.

Esta diferencia es deliberada: el Sprint 2 materializa HU02/HU04 y mantiene el resto como
diseño. Incorporar una pantalla en el prototipo no sustituye la entidad, migración, servicio,
seguridad y pruebas que necesita un módulo terminado.

## Organización de paquetes

| Ubicación | Contenido |
|---|---|
| `controllers` | Rutas REST y traducción de solicitudes. |
| `models` | Contratos de entrada/salida y objetos del dominio del incremento. |
| `services` | Casos de uso y reglas. |
| `ports` | Interfaces pequeñas requeridas por los servicios. |
| `repositories` | Adaptadores concretos de persistencia. |
| `repositories.jpa` | Entidades e interfaces Spring Data JPA. |
| `security` | Filtro JWT, configuración y respuesta de autenticación. |
| `strategies` | Algoritmos intercambiables para calcular flete. |
| `events` | Evento de publicación y observadores. |
| `resources/static` | Aplicación web responsive. |

## Transacciones y trabajo futuro

Los límites transaccionales corresponden al servicio del caso de uso cuando interviene más
de un repositorio. Pedidos, reservas, stock, detalles, envíos e idempotencia deberán
implementarse con transacciones y pruebas de concurrencia antes de declararse terminados.
También falta trasladar contacto y notificaciones a PostgreSQL y conectar la consulta de
precios con ventas entregadas.

## Fuentes de la entrega

- Guías del bloque 02 y del Sprint de diseño: MVC en cuatro capas, PostgreSQL y patrones.
- Código actual del repositorio: controladores, servicios, puertos, adaptadores y vista.
- D01/D02: modelo de dominio y esquema relacional objetivo.
