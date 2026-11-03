# Compra, inventario y logística

## Alcance

Este incremento conecta HU03, HU09 y HU11–15 con una base relacional y con las pantallas
del comprador y del agricultor. La administración incorpora exclusivamente un resumen
de indicadores protegido: no bloquea cuentas, cambia roles ni modifica pedidos ajenos.

## Recorrido de uso

1. El agricultor inicia sesión, registra su finca y publica una cosecha.
2. El comprador abre el catálogo, consulta el detalle y reserva una cantidad en kilogramos.
3. En **Carrito** revisa cantidad, precio, subtotal y vencimiento. Puede actualizar o quitar
   reservas. Actualizar renueva los 15 minutos y toma el precio actual.
4. Confirma el pedido. Los productos de un carrito deben pertenecer al mismo agricultor.
5. En **Mis pedidos**, ambos participantes consultan sus pedidos y avisos internos.
6. El agricultor abre un pedido pendiente, indica fecha futura y ruta, y lo marca listo
   para despacho. Después registra la salida y la entrega cuando realmente ocurran.
7. El comprador puede cancelar únicamente mientras el pedido esté pendiente.
8. **Precios del mercado** muestra la media de ventas entregadas durante las últimas 24 horas.

No se realizan cobros, correos, SMS ni seguimiento GPS. El total corresponde a productos;
no incluye transporte. Las condiciones de entrega deben acordarse con el agricultor.
Registrar un estado es responsabilidad del agricultor; el sistema no verifica físicamente
la recepción ni liquida pagos.

## API

Todas las rutas privadas requieren `Authorization: Bearer <JWT>`. La identidad nunca
se acepta desde el cuerpo de la solicitud. El servidor vuelve a consultar el rol vigente.

| Método y ruta | Rol y operación |
| --- | --- |
| GET /api/v1/mis-ofertas | Agricultor: inventario propio físico, reservado y disponible |
| PUT /api/v1/productos/{id} | Propietario: cantidadFisicaKg y precioCopKg |
| GET /api/v1/carrito | Comprador: carrito abierto propio |
| POST /api/v1/carrito/items | Comprador: cosechaId y cantidadKg; reemplaza, no suma |
| DELETE /api/v1/carrito/items/{id} | Comprador: retirar oferta del carrito |
| POST /api/v1/ordenes | Comprador: carritoId y encabezado Idempotency-Key |
| GET /api/v1/ordenes | Participante: lista privada |
| GET /api/v1/ordenes/{id}/trazabilidad | Participante: pedido, líneas y eventos |
| PATCH /api/v1/ordenes/{id}/estado | Agricultor propietario: estado y programación |
| POST /api/v1/ordenes/{id}/cancelacion | Comprador propietario: cancelar pendiente |
| GET /api/v1/notificaciones/pedidos | Participante: avisos internos propios |
| GET /api/v1/administracion/resumen | Administrador autorizado: totales sin datos personales |
| GET /api/v1/precios/promedio?producto=Tomate | Pública: precios históricos, no anuncios |

Se admiten también los nombres del backlog: `id_producto`, `cantidad_kg`,
`cantidad_fisica_kg`, `precio_cop_kg`, `carrito_id` y `fecha_programada`.
La media devuelve `cantidadTransacciones` y su alias `cantidad_transacciones`.
La comparación del nombre ignora mayúsculas, espacios exteriores y tildes (Café = Cafe).

Los errores devuelven 400 para entradas inválidas, 401 para falta de sesión válida,
403 para rol o propiedad incorrectos, 404 para recursos inexistentes y 409 para
conflictos de inventario, vencimiento, idempotencia o transición.

## Reglas y consistencia

- Disponible = cantidad física − reservas activas con vencimiento posterior a la hora actual.
  Las vencidas no retienen inventario aunque permanezcan visibles para renovarlas o retirarlas.
- No hay un contador mutable de reservas: consultar varias veces una reserva vencida no
  duplica devoluciones. La confirmación vuelve a verificar el plazo después de los bloqueos.
- Un bloqueo de la cuenta serializa las operaciones de cada comprador. Los bloqueos de
  ofertas se toman por identificador ascendente para las operaciones con varios productos.
- Cada pedido tiene una clave única por comprador y un carrito único. El primer éxito
  devuelve 201; repetir la misma clave y carrito devuelve 200 con el mismo pedido.
  La misma clave con otro carrito o un carrito ya confirmado con otra clave devuelve 409.
- Precio y cantidad se copian a las líneas del pedido. Editar posteriormente la oferta
  no modifica el pedido. Los precios reservados se mantienen hasta renovar o vencer.
- Confirmar descuenta inventario, consume reservas y crea pedido, líneas, evento y aviso
  dentro de una sola transacción. Cualquier fallo revierte todas esas escrituras.
- Cancelar bloquea el pedido y devuelve inventario solo al pasar de PENDIENTE a CANCELADA.
  Repetir la cancelación devuelve el estado existente sin duplicar existencias.
- Despacho: PENDIENTE → LISTO_PARA_DESPACHO → EN_CAMINO → ENTREGADO.
  La primera transición exige fecha futura con zona horaria y ruta no vacía.
- Una línea entregada genera una única transacción completada. El promedio no pondera por
  cantidad; usa BigDecimal, redondeo HALF_UP a dos decimales y ventana inclusiva de 24 horas.
  Sin coincidencias devuelve promedio nulo y conteo cero.
- Las fechas comerciales usan UTC con precisión de microsegundos, compatible con PostgreSQL.
  La interfaz muestra Colombia; el formulario de programación interpreta UTC−05:00.

## Persistencia y seguridad

La API conserva la arquitectura controlador → servicio → puerto → adaptador. El adaptador
de comercio usa JdbcTemplate dentro del gestor transaccional JPA ya configurado, con
consultas parametrizadas y bloqueos SELECT FOR UPDATE. El esquema se amplía sin borrar
cuentas, fincas, ofertas ni contactos existentes.

Tablas añadidas: carritos, reservas, pedidos, pedido_items, eventos_pedido, avisos_pedido y
transacciones. Hay claves foráneas, restricciones de cantidades/precios y unicidad para
carrito-pedido, idempotencia, líneas y ventas finalizadas.

El registro público solo admite AGRICULTOR y COMPRADOR. ADMINISTRADOR no se puede obtener
desde el formulario, la API de registro ni modificando el rol del navegador. No se incluyen
usuarios administradores, contraseñas predeterminadas ni un endpoint para otorgar ese rol.
Su aprovisionamiento requiere una decisión explícita del responsable del sistema.

## Verificación reproducible

- `sh ./mvnw -B verify`: compilación Java 17, Checkstyle, pruebas H2 y umbrales JaCoCo existentes.
- `sh ./mvnw -B test -Dspring.profiles.active=ci-postgres`, con las variables de CI:
  mismas pruebas contra PostgreSQL y prueba HTTP de reinicio.
- `node --check src/main/resources/static/assets/site.js` y
  `node --check src/main/resources/static/assets/commerce.js`: sintaxis del cliente.
- `node --test src/test/js/commerce.test.cjs`: pruebas del comportamiento del cliente,
  con API y DOM simulados; no sustituyen una revisión visual en navegador.

ComercioAcceptanceTests cubre propiedad, roles, contratos, expiración, inventario,
confirmación/reintentos, cancelación, estados, avisos y precios. ComercioConcurrencyTests
ejecuta solicitudes simultáneas con conexiones diferentes. FlujoPostgresqlTests comprueba
persistencia tras reiniciar y fuerza un fallo intermedio de confirmación en la base
desechable de CI para comprobar rollback íntegro.

Los resultados de ejecución se consultan en la solicitud de integración #56 y sus checks.
Una prueba automática no constituye aprobación de la docente ni revisión de un compañero.
