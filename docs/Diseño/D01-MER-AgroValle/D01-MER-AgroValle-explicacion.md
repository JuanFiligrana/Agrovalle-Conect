# D01 Modelo entidad relación de AgroValle Connect

## Abrir y editar

Abrir D01-MER-AgroValle.drawio desde Archivo > Abrir desde > Dispositivo en diagrams.net. Las cinco pestañas inferiores muestran vistas del mismo modelo. Las figuras, atributos, conexiones y textos son elementos editables; no son una imagen pegada dentro del archivo.

## Alcance y notación

Modelo conceptual elaborado a partir de las 15 historias de BACKLOG.md y de la clase del 8 de octubre de 2026. Emplea notación Chen extendida: rectángulos para entidades, óvalos para atributos, rombos para relaciones y triángulo para especialización. El modelo relacional en tercera forma normal corresponde a D02 y será un entregable separado.

La notación (mínimo, máximo), junto a una entidad, expresa cuántas veces participa cada una de sus instancias en esa relación. Ejemplo: AGRICULTOR (1,1) — RESIDE EN — MUNICIPIO (0,N) significa que cada agricultor reside en un municipio y un municipio puede tener cero o muchos agricultores. Una entidad gris repite una entidad definida en otra vista; no crea una tabla adicional. Los identificadores están subrayados; los atributos derivados tienen borde discontinuo.

## Vistas del modelo

1. Usuarios y ubicación: USUARIO, AGRICULTOR, COMPRADOR y MUNICIPIO.
2. Producción y catálogo: FINCA, CATEGORIA, PRODUCTO y COSECHA.
3. Contacto y notificaciones: CONTACTO y NOTIFICACION.
4. Carrito, reservas y pedidos: CARRITO, RESERVA, PEDIDO y DETALLE_PEDIDO.
5. Envíos y trazabilidad: ENVIO y EVENTO_SEGUIMIENTO.

Son 16 entidades conceptuales. Las referencias repetidas en gris permiten consultar las relaciones entre áreas sin comprimir todos los atributos en una sola lámina.

## Decisiones de diseño que se deben validar con el equipo

- Cada cuenta tiene un rol, AGRICULTOR o COMPRADOR. Por ello la especialización de USUARIO es total y disjunta. Un usuario no pertenece simultáneamente a ambos subtipos en esta propuesta. AGRICULTOR y COMPRADOR heredan id_usuario; no se duplican los datos comunes. Si se requieren cuentas con varios roles, se cambia esta restricción.
- PRODUCTO es el tipo de producto del catálogo. COSECHA es la oferta concreta con finca, fecha, cantidad y precio. Los endpoints heredados llamados /productos operan sobre la oferta; esta equivalencia se documentará al implementar para no cambiar contratos sin revisión.
- El municipio del agricultor y el municipio de una finca representan ubicaciones diferentes. Los filtros del catálogo usan el municipio de la finca que origina la cosecha.
- RESERVA cumple la función de ítem de carrito. Cada reserva se refiere a una cosecha y guarda cantidad, precio aceptado y vencimiento. El plazo de reserva es 15 minutos. Se conservan reservas consumidas o vencidas para mantener trazabilidad; el carrito visible muestra solo las activas.
- Cada reserva consumida origina una línea DETALLE_PEDIDO. Esa línea guarda cantidad y precio histórico; una actualización posterior de la oferta no altera una compra confirmada. En esta propuesta no se fusionan varias reservas en una sola línea al confirmar.
- Un carrito puede originar varios pedidos a lo largo del tiempo, pero cada confirmación consume únicamente sus reservas activas. El comprador del pedido es el propietario de su carrito de origen. Los carritos que tienen pedidos no se eliminan físicamente.
- Cada pedido atiende a un solo agricultor. Todas las cosechas de las reservas activas de un carrito y de los detalles de un pedido deben corresponder al mismo agricultor. Es una restricción adicional que no queda garantizada solo por las cardinalidades.
- Cada pedido tiene una clave de idempotencia. La unicidad se comprobará por comprador y clave. Una repetición con el mismo contenido devuelve el pedido existente y no descuenta inventario otra vez.
- ENVIO es opcional para un pedido pendiente y único cuando existe. Los eventos pertenecen al PEDIDO, de modo que se puede conservar la confirmación o cancelación aunque todavía no exista un envío.
- NOTIFICACION tiene exactamente un destinatario USUARIO. Su origen es CONTACTO o EVENTO_SEGUIMIENTO, de manera excluyente. Un contacto genera un aviso interno al agricultor dueño de la oferta; determinados eventos logísticos generan avisos al comprador.
- Para HU-03 se propone interpretar una transacción completada como una línea de pedido ENTREGADO. fecha_completado registra el momento de entrega. Se promedian precios históricos unitarios de las líneas del mismo producto completadas en las últimas 24 horas, sin ponderar por kg. Si la profesora usa otra definición de transacción, se ajusta esta decisión antes de implementar el cálculo.

## Reglas de integridad y comportamiento

- Correo de USUARIO y cédula de AGRICULTOR únicos. Campos obligatorios y catálogo de municipios del Valle del Cauca conforme a HU-01 y HU-08.
- contrasena_hash contiene un hash BCrypt; no se almacena ni devuelve la contraseña en texto plano.
- Cantidades positivas en kg con hasta tres decimales; precios positivos en COP/kg con dos decimales; cálculos con BigDecimal.
- Cantidad disponible = cantidad física − suma de reservas activas que no hayan vencido. Una oferta no puede reducir su cantidad física por debajo de lo reservado.
- Confirmar consume reservas y descuenta cantidad física en una sola transacción. Cancelar un pedido PENDIENTE devuelve el inventario exactamente una vez. Vencer una reserva libera disponibilidad sin incrementar cantidad física.
- Estados del pedido: PENDIENTE, LISTO_PARA_DESPACHO, EN_CAMINO, ENTREGADO y CANCELADA, conservando los literales propuestos en el backlog. Transiciones: PENDIENTE → LISTO_PARA_DESPACHO → EN_CAMINO → ENTREGADO, o PENDIENTE → CANCELADA.
- subtotal_cop = cantidad_kg × precio_unitario_cop_kg. total_cop = suma de subtotales. Si se materializan en D02, se debe definir cómo conservar su consistencia.
- La trazabilidad se ordena por fecha_evento; las fechas de negocio usan America/Bogota y los eventos conservan zona horaria.
- La información de las cuentas se protege por autenticación, rol y propiedad. Las relaciones del MER no sustituyen los controles de autorización.
- No se añaden pagos reales, GPS continuo ni cálculo automático de rutas: quedan fuera del MVP definido en BACKLOG.md.

## Trazabilidad con las historias

| Historia | Entidades y relaciones principales |
|---|---|
| HU-01 | USUARIO, AGRICULTOR, MUNICIPIO; especialización y residencia |
| HU-02 | AGRICULTOR, FINCA, PRODUCTO, CATEGORIA, COSECHA |
| HU-03 | PRODUCTO, COSECHA, DETALLE_PEDIDO, PEDIDO ENTREGADO; promedio derivado |
| HU-04 | COSECHA → FINCA → MUNICIPIO y COSECHA → PRODUCTO → CATEGORIA |
| HU-05 | COMPRADOR, CONTACTO, COSECHA, AGRICULTOR, NOTIFICACION, USUARIO |
| HU-06 | USUARIO y COMPRADOR |
| HU-07 | USUARIO; correo, hash, activo y rol derivado del subtipo |
| HU-08 | AGRICULTOR, FINCA, MUNICIPIO |
| HU-09 | COSECHA, RESERVA, DETALLE_PEDIDO; cantidad y precio histórico |
| HU-10 | COSECHA, FINCA, MUNICIPIO, PRODUCTO, CATEGORIA y AGRICULTOR |
| HU-11 | COMPRADOR, CARRITO, RESERVA, COSECHA |
| HU-12 | CARRITO, RESERVA, PEDIDO, DETALLE_PEDIDO, AGRICULTOR |
| HU-13 | PEDIDO, ENVIO, EVENTO_SEGUIMIENTO, NOTIFICACION |
| HU-14 | PEDIDO, CARRITO, COMPRADOR, ENVIO, EVENTO_SEGUIMIENTO |
| HU-15 | PEDIDO, DETALLE_PEDIDO, COSECHA, EVENTO_SEGUIMIENTO |

## Revisión y uso en el tablero

Guardar los archivos de esta carpeta en docs/diseno del repositorio. Enlazar el archivo .drawio, las cinco imágenes y esta explicación desde D01. El modelo representa el diseño propuesto, no una afirmación de que las tablas o funcionalidades ya estén implementadas. Abrir y revisar las cinco vistas antes de dar por terminada la tarea.

Fuentes: BACKLOG.md de JuanFiligrana/Agrovalle-Conect, documento de planificación del Sprint 1 y transcripción de la clase de Paola Andrea Bedoya Toro del 8 de octubre de 2026.
