# D02 Normalización y transformación del MER

## Propósito

El modelo relacional transforma el MER D01 en tablas con PK, FK, restricciones y tipos para PostgreSQL. Conserva 16 entidades de negocio y añade una tabla técnica, idempotencia_pedido, para implementar la unicidad de la clave de confirmación por comprador. Son 17 tablas base y tres vistas de cálculo. Las vistas y las entidades repetidas en gris en el diagrama no son tablas de negocio adicionales.

## Paso de modelo conceptual a relacional

- Cada entidad del MER produce una tabla. Cada relación 1:N se implementa poniendo una FK en el lado N.
- La especialización USUARIO → AGRICULTOR / COMPRADOR se transforma mediante una tabla padre y dos tablas hijas. La PK de cada hija es, al mismo tiempo, FK hacia usuario.id_usuario. El municipio del agricultor se conserva separado del municipio de sus fincas.
- Las asociaciones N:M entre compradores y cosechas se resuelven mediante hechos de negocio: contacto y reserva. Un contacto no es una columna repetida ni una lista de mensajes dentro de usuario.
- La relación entre pedido y cosecha se materializa mediante detalle_pedido y su reserva de origen. Un pedido admite varios detalles; cada detalle proviene de una reserva única.
- UNIQUE(envio.id_pedido) expresa que un pedido tiene como máximo un envío. UNIQUE(detalle_pedido.id_reserva) impide confirmar la misma reserva en dos líneas.

## Primera forma normal

Todas las columnas contienen un valor de su dominio, cada fila se identifica por una clave y no hay grupos repetidos como producto1/producto2 ni listas de productos separadas por comas. Los productos comprados se representan con filas de detalle_pedido; los eventos logísticos, con filas de evento_seguimiento.

## Segunda forma normal

Las tablas con PK simple no admiten dependencias parciales respecto de esa PK. Además se consideran las claves candidatas: correo normalizado en usuario, cédula en agricultor, id_reserva en detalle_pedido e id_pedido en envio, entre otras.

La PK compuesta de idempotencia_pedido es (id_comprador, clave). La clave de reintento puede repetirse entre compradores y un comprador puede enviar varias claves. El id_pedido, la huella y la fecha dependen de la combinación completa, no de una sola parte. id_pedido es además clave candidata por UNIQUE y NOT NULL.

## Tercera forma normal

Para las dependencias funcionales del dominio adoptado, todo determinante no trivial es una superclave o determina un atributo primo. No basta agregar IDs artificiales: se revisan las dependencias entre datos del negocio y se eliminan copias que generarían anomalías.

| Dependencia que produciría una copia innecesaria | Solución aplicada |
|---|---|
| id_finca → id_municipio y propietario | cosecha guarda id_finca; no vuelve a guardar municipio ni agricultor. |
| id_producto → id_categoria | cosecha guarda id_producto; la categoría se consulta mediante producto. |
| id_cosecha → finca → agricultor | contacto guarda id_cosecha; no duplica el agricultor receptor. |
| id_carrito → id_comprador | pedido guarda id_carrito; el comprador se obtiene por el carrito. |
| id_reserva → id_cosecha | detalle_pedido guarda id_reserva; la cosecha se obtiene por reserva. |
| cantidad y precio → subtotal; detalles → total | subtotal y total se calculan; no se mantienen columnas editables duplicadas. |
| id_municipio → nombre del municipio | el nombre está en municipio, no copiado en finca o agricultor. |

Las cantidades y precios de reserva y detalle_pedido corresponden a hechos en momentos diferentes: aceptación de una reserva y confirmación de una compra. La instantánea confirmada se conserva inmutable. No se toma el precio actual de cosecha como si fuera el precio de una venta pasada.

## Dependencias y claves por tabla

La notación K → resto significa que la clave K determina el resto de los atributos de esa tabla. Se deben conservar las reglas semánticas indicadas; la elección de una PK artificial no reemplaza este análisis.

- **municipio**: `(id_municipio) → resto`. Claves candidatas alternativas: (nombre).
- **usuario**: `(id_usuario) → resto`. Claves candidatas alternativas: correo normalizado.
- **agricultor**: `(id_usuario) → resto`. Claves candidatas alternativas: (cedula).
- **comprador**: `(id_usuario) → resto`.
- **finca**: `(id_finca) → resto`.
- **categoria**: `(id_categoria) → resto`. Claves candidatas alternativas: (nombre).
- **producto**: `(id_producto) → resto`. Claves candidatas alternativas: (nombre).
- **cosecha**: `(id_cosecha) → resto`.
- **carrito**: `(id_carrito) → resto`.
- **reserva**: `(id_reserva) → resto`.
- **pedido**: `(id_pedido) → resto`.
- **detalle_pedido**: `(id_detalle) → resto`. Claves candidatas alternativas: (id_reserva).
- **envio**: `(id_envio) → resto`. Claves candidatas alternativas: (id_pedido).
- **evento_seguimiento**: `(id_evento) → resto`.
- **contacto**: `(id_contacto) → resto`.
- **notificacion**: `(id_notificacion) → resto`.
- **idempotencia_pedido**: `(id_comprador, clave) → resto`. Claves candidatas alternativas: (id_pedido).

## Unicidad de reintentos sin duplicar comprador

La HU-12 necesita que repetir una confirmación no vuelva a descontar stock. D01 propuso unicidad por comprador y clave. Guardar id_comprador además de id_carrito en pedido repetiría el comprador que ya determina el carrito. Para evitar esa dependencia transitiva, la asociación de clave y resultado se almacena en idempotencia_pedido:

`(id_comprador, clave) → id_pedido, huella_solicitud, fecha_creacion`.

La huella se calcula sobre la solicitud canonizada. Una repetición válida recupera el mismo pedido; un contenido diferente para la misma clave debe rechazarse. Al implementar, la asociación y el pedido se crean en una misma transacción; el comprador debe coincidir con el dueño del carrito del pedido.

## Integridad implementada en el DDL estructural

- Todas las tablas tienen PK y los vínculos usan FK con tipos compatibles.
- Las FK usan RESTRICT para preservar pedidos y trazabilidad. La eliminación de cuentas se representa con activo=false.
- El correo tiene índice único sobre lower(btrim(correo)); las cédulas y los nombres de catálogos son únicos según las restricciones declaradas.
- Precios y cantidades de líneas son positivos; el stock físico admite cero.
- Las reservas vencen exactamente 15 minutos después de su creación.
- Un pedido ENTREGADO tiene fecha_completado y los demás no; la fecha no precede a la confirmación.
- Una notificación tiene exactamente un origen: id_contacto o id_evento. Una FK opcional admite NULL y se indica en el diagrama.

## Reglas que requieren la operación transaccional

Estas reglas no quedan garantizadas solo por una FK o un CHECK de una fila. Se especifican para la implementación del Sprint 1, mediante servicios transaccionales, bloqueo de filas y, cuando corresponda, restricciones diferidas o triggers:

1. Crear exactamente un subtipo por usuario y hacerlo atómicamente; impedir que una cuenta sea agricultor y comprador simultáneamente. JOINED por sí solo no garantiza esa regla en PostgreSQL.
2. Verificar que el catálogo contenga únicamente municipios autorizados del Valle del Cauca. No se incluyen datos geográficos inventados en el DDL.
3. Mantener todas las reservas activas de un carrito y todos los detalles de un pedido vinculados a un mismo agricultor.
4. Bloquear la cosecha antes de reservar o reducir su cantidad física para que las reservas activas nunca superen el stock. Antes de calcular, procesar vencimientos de forma idempotente.
5. Al confirmar, comprobar reservas vigentes, propietario y precios aceptados; crear pedido, detalles e idempotencia; consumir reservas y descontar cantidad física una sola vez. Cada pedido confirmado debe tener al menos un detalle.
6. Impedir que cambien el propietario del carrito de un pedido, los datos de una reserva consumida o el precio y cantidad de sus detalles históricos.
7. Registrar al menos un evento por pedido y validar la máquina de estados. Al cancelar PENDIENTE, restituir el inventario una sola vez.
8. Crear el aviso interno junto con el contacto y verificar el destinatario correcto. Para eventos, el destinatario se decide según el negocio.
9. Comprobar que la programación del envío y la fecha de cosecha sean válidas en el momento de la operación. No se usa un CHECK con CURRENT_DATE para una regla que cambia con el tiempo.

El DDL adjunto implementa la estructura y las restricciones declarativas anteriores. No se presenta como implementación de estas operaciones de negocio ni sustituye su código y sus pruebas.

## Datos calculados

- v_disponibilidad: stock físico menos reservas ACTIVA cuyo vencimiento es posterior al instante actual.
- v_total_pedido: suma de subtotales, redondeando cada línea a dos decimales en COP antes de sumar.
- v_precios_24h: promedio no ponderado de precios históricos por línea de pedido ENTREGADO en las últimas 24 horas. Esta interpretación de transacción completada conserva la decisión documentada en D01. Devuelve NULL y cantidad 0 cuando no hay ventas.

Las consultas se calculan al leer. No se crean columnas base para disponibilidad, promedio, subtotal o total.

## Fuentes y correspondencia

- D01-MER-AgroValle.drawio y su explicación: modelo conceptual de referencia.
- BACKLOG.md de AgroValle Connect: las 15 historias y sus contratos BDD.
- Clase de Paola Andrea Bedoya Toro del 8 de octubre de 2026: modelo relacional en tercera forma normal, PK, FK, integridad y mapeo JPA.
- PostgreSQL 18, [Constraints](https://www.postgresql.org/docs/18/ddl-constraints.html): PK, FK, UNIQUE, NOT NULL y CHECK. Los CHECK evalúan condiciones de la fila y no garantizan reglas sobre otras filas o tablas.
