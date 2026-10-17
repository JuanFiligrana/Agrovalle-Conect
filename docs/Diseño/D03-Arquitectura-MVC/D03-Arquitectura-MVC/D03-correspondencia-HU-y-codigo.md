# D03 · Correspondencia con HU01–05 y con el prototipo

## Alcance de la matriz

Las rutas se obtuvieron del código de la rama `chore/sprint-0-completo`, commit `0727be3ad29af86c39fc07ccad1a145143289643`. Los contratos/adaptadores nuevos son propuestas. Se conserva el Sprint 1 largo con HU01–05; esta matriz no cambia historias, puntos ni tareas del tablero.

## Rutas, responsabilidades y datos

| HU / operación | Ruta actual | Controlador → servicio objetivo | Contratos de datos propuestos | Tablas o vistas D02 |
|---|---|---|---|---|
| HU01 · registrar usuario | `POST /api/v1/auth/register` | `AuthController` → `AgricultorService` adaptado al registro de subtipos | `UsuarioPort` | `usuario`, `agricultor`, `comprador`, `municipio` |
| Apoyo a HU02 · registrar finca | `POST /api/v1/fincas` | `FincaController` → `FincaService` | `FincaPort`, `UsuarioPort` | `finca`, `agricultor`, `municipio` |
| HU02 · publicar oferta/cosecha | `POST /api/v1/productos` | `ProductoController` → `CosechaService` | `CosechaPort`, `FincaPort`, `UsuarioPort` | `cosecha`, `finca`, `producto`, `categoria` |
| HU03 · consultar promedio | `GET /api/v1/precios/promedio?producto=...` | `PrecioController` → `ConsultaPreciosService` nuevo | `PrecioPort` | `v_precios_24h`; utiliza ventas históricas del modelo D02 |
| HU04 · filtrar ofertas | `GET /api/v1/productos?municipio=...&categoria=...` | `ProductoController` → `CosechaService` | `CosechaPort` | `cosecha` → `finca` → `municipio`; `cosecha` → `producto` → `categoria` |
| HU05 · registrar intención de contacto | `POST /api/v1/contacto/mensaje` | `ContactoController` → `ContactoService` | `ContactoPort`, `NotificacionPort`, `UsuarioPort`, `CosechaPort` | `contacto`, `notificacion`; destinatario derivado de `cosecha` → `finca` → `agricultor` |
| Salud del sistema · Sprint 0 | `GET /api/v1/estado` | `EstadoController` | Sin repositorio | Respuesta técnica; no consulta PostgreSQL |

La ruta `/productos` del prototipo publica **ofertas/cosechas**, mientras que `producto` en D02 es el catálogo de tipos de producto. En el contacto, `productoId` del contrato actual identifica esa oferta/cosecha. Documentar esta correspondencia y mantener compatibilidad, o versionar cualquier cambio de nombre del contrato.

## Contratos de repositorio

| Contrato nuevo | Responsabilidad | Adaptador objetivo |
|---|---|---|
| `UsuarioPort` | Consultar usuarios/roles, comprobar duplicados y persistir el registro con su subtipo. | `UsuarioJpaAdapter` |
| `FincaPort` | Consultar y registrar fincas; recuperar su propietario y municipio. | `FincaJpaAdapter` |
| `CosechaPort` | Guardar una oferta, buscar por ID y consultar ofertas activas filtradas. La proyección de consulta puede incluir el propietario derivado de la finca. | `CosechaJpaAdapter` |
| `PrecioPort` | Devolver el promedio histórico y el número de líneas de venta de la ventana de 24 horas. | `ConsultaPrecioJpaAdapter` |
| `ContactoPort` | Persistir y consultar intenciones de contacto. | `ContactoJpaAdapter` |
| `NotificacionPort` | Persistir el aviso y su destinatario/origen según las restricciones D02. | `NotificacionJpaAdapter` |

Los puertos no son endpoints HTTP. Sus parámetros y resultados deben expresar los datos necesarios para el negocio; evitan exponer `EntityManager`, consultas SQL o clases de respuesta HTTP a los servicios. En la implementación se concretarán los tipos y firmas completos. Los nombres y operaciones de la hoja 02 son una especificación visual resumida, no interfaces Java listas para compilar.

## Qué existe y qué cambia

| Parte | Estado observado | Evolución de diseño |
|---|---|---|
| Presentación | Página estática y consulta de salud. | Añadir formularios y vistas para las HU, usando los mockups D07. |
| Controladores REST | Existen las seis clases y las rutas de la matriz. | Mantener contratos, delegar reglas y homogeneizar DTO de salida. |
| Servicios | Existen `AgricultorService`, `FincaService`, `CosechaService` y `ContactoService`. | Introducir contratos, separar precios y quitar decisiones HTTP del negocio. |
| Repositorios | Cuatro clases concretas en memoria. | Adaptadores JPA y contratos; conservar implementaciones temporales solo como alternativa controlada. |
| Modelos | Registros del prototipo; la clase `Agricultor` también representa cuentas de comprador mediante un rol. | Adoptar `Usuario`, `Agricultor` y `Comprador` y las asociaciones JPA previstas en D02. |
| HU03 | `CosechaService.promedio` promedia precios de ofertas activas. | Consultar la ventana histórica D02 sobre líneas de pedidos entregados; conservar precio histórico, promedio no ponderado y ausencia de ventas. |
| HU05 | Se registra el contacto; no se crea una notificación persistente. | Crear contacto y aviso como una unidad transaccional y comprobar identidad y permisos. |
| Autenticación | La ruta de contacto comprueba que exista una cabecera de autorización. | Verificar autenticación, rol e identidad del actor; no confiar solo en IDs recibidos del cliente. |
| PostgreSQL | Driver declarado, sin configuración de datasource ni integración JPA. | Agregar la integración y configurar credenciales fuera de los archivos versionados. |

El promedio de 24 horas no ponderado por cantidad es la decisión explícita de D02 para el contrato de HU03. No debe reemplazarse silenciosamente por el promedio de ofertas activas del prototipo.

## Extensión coherente con D02

El modelo incluye carrito, reservas, pedidos, detalles, envíos, eventos e idempotencia para el alcance posterior. Sus controladores y servicios no existen en el prototipo revisado. Cuando se implementen, seguirán las mismas cuatro capas; sus rutas todavía no se fijan en este documento.

## Criterios de revisión del artefacto D03

- Se identifican exactamente cuatro capas y sus responsabilidades.
- Vista y controlador son elementos distintos dentro de presentación.
- Se distinguen reglas de negocio, contratos y acceso físico a datos.
- Los adaptadores implementan los contratos; los servicios dependen de las abstracciones.
- La matriz conserva los endpoints que existen y aclara los cambios previstos.
- Las entidades y tablas corresponden al diseño D02.
- Se diferencian diseño, código existente y trabajo de implementación pendiente.

Estos criterios revisan la documentación de arquitectura. No sustituyen pruebas del backend ni acreditan que las HU estén terminadas.
