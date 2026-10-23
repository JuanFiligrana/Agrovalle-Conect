# D03 · Correspondencia con HU01–HU05 y con el código

## Rutas, responsabilidades y persistencia

| HU u operación | Ruta REST | Flujo principal | Estado de datos |
|---|---|---|---|
| HU01 · registrar usuario | `POST /api/v1/auth/register` | `AuthController` → `AgricultorService` → `UsuarioPort` | PostgreSQL mediante JPA |
| Autenticación | `POST /api/v1/auth/login` y `GET /api/v1/auth/me` | `AuthController` → `AutenticacionService` → `UsuarioPort` | Cuenta persistente y JWT |
| Apoyo HU02 · finca | `POST/GET /api/v1/fincas` | `FincaController` → `FincaService` → `FincaPort` | PostgreSQL mediante JPA |
| HU02 · publicar cosecha | `POST /api/v1/productos` | `ProductoController` → `CosechaService` → `CosechaPort` | PostgreSQL mediante JPA |
| HU03 · promedio | `GET /api/v1/precios/promedio?producto=...` | `PrecioController` → `ConsultaPreciosService` → `CosechaPort` | Cosechas del incremento; histórico D02 pendiente |
| HU04 · filtrar ofertas | `GET /api/v1/productos?municipio=&categoria=` | `ProductoController` → `CosechaService` → `CosechaPort` | Consulta JPA y filtrado validado |
| HU05 · contacto | `POST /api/v1/contacto/mensaje` | `ContactoController` → `ContactoService` → `ContactoPort` | Repositorio temporal en memoria |
| Flete | `GET /api/v1/fletes/calculo` | `FleteController` → `CalculadoraFleteService` → Strategy | Sin persistencia |
| Aviso de cosecha | `GET /api/v1/notificaciones/cosechas` | `NotificacionController` → listener Observer | Colección temporal en memoria |
| Salud | `GET /api/v1/estado` | `EstadoController` | Sin persistencia |

La ruta `/productos` conserva el contrato del proyecto, aunque publica ofertas o cosechas.
En D02, `producto` es el catálogo y `cosecha` contiene cantidad, fecha, disponibilidad y
precio. La implementación JPA respeta esa separación.

## Puertos y adaptadores actuales

| Puerto | Servicio consumidor | Adaptador |
|---|---|---|
| `UsuarioPort` | Registro, autenticación y comprobación de rol | `AgricultorRepository` + `UsuarioJpaRepository` |
| `FincaPort` | Registro/listado de fincas y propiedad de la oferta | `FincaRepository` + `FincaJpaRepository` |
| `CosechaPort` | Publicación, consulta, precios y contacto | `CosechaRepository` + repositorios JPA normalizados |
| `ContactoPort` | Registro de intención de contacto | `ContactoRepository` en memoria |

Los puertos no son endpoints. Evitan que los servicios dependan de `EntityManager`, SQL o
clases HTTP. Spring selecciona una implementación concreta por constructor.

## Presentación web y alcance

| Vista | Relación funcional |
|---|---|
| Portada y catálogo | HU03/HU04; muestra origen, cantidad, COP/kg y filtros claros. |
| Acceso y registro | HU01 y autenticación; diferencia agricultor y comprador. |
| Panel del agricultor y publicación | HU02, mensajes HU05 y diseño futuro de pedidos/entregas. |
| Seguimiento | Diseño futuro de `envio` y `evento_seguimiento` de D02. |
| Panel administrativo | Diseño futuro de gestión, bloqueos, reportes y alertas. |

Las siete vistas se sirven desde `src/main/resources/static/`. Catálogo, menús, filtros y
retroalimentación del prototipo funcionan en el navegador. Una interacción visual de pedido,
reserva, entrega o reporte no acredita todavía su endpoint ni su persistencia.

## Diferencia entre incremento y objetivo D02

| Área | Incremento implementado | Objetivo posterior |
|---|---|---|
| PostgreSQL | Cinco tablas: usuarios, fincas, categorías, productos y cosechas | Completar las 17 tablas y 3 vistas de D02 mediante migraciones |
| Contacto | Regla de rol y guardado temporal | Contacto y notificación persistentes en una transacción |
| Precios | Promedio sobre cosechas disponibles | Ventana histórica de líneas entregadas en 24 horas |
| Pedidos y logística | Prototipo visual | Reservas, pedido, detalle, envío y eventos con concurrencia e idempotencia |
| Roles | Un único rol por cuenta en el contrato actual | Mantener la especialización total y disjunta de D01/D02 |

Esta matriz permite defender qué está terminado y qué es diseño sin mezclar evidencia de
interfaz con evidencia de backend.
