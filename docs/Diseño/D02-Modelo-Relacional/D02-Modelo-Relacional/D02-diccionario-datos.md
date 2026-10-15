# D02 Diccionario de datos

Tipos y longitudes propuestos para PostgreSQL. NN significa obligatorio; UQ significa único; UQ* indica unicidad mediante índice por expresión. Los identificadores autogenerados usan BIGINT IDENTITY. Los ID que son también FK, como los subtipos, reutilizan la identidad del padre.

## municipio

Entidad JPA prevista: `Municipio`. PK: `id_municipio`.

| Columna | Tipo PostgreSQL | Clave y nulabilidad | Referencia o significado |
|---|---|---|---|
| `id_municipio` | `BIGINT` | PK NN | Identificador generado por PostgreSQL. |
| `nombre` | `VARCHAR(120)` | UQ NN | Nombre del municipio del catálogo del Valle del Cauca. |

Reglas CHECK:
- `length(btrim(nombre)) > 0`.

## usuario

Entidad JPA prevista: `Usuario`. PK: `id_usuario`.

| Columna | Tipo PostgreSQL | Clave y nulabilidad | Referencia o significado |
|---|---|---|---|
| `id_usuario` | `BIGINT` | PK NN | Identificador generado por PostgreSQL. |
| `nombre` | `VARCHAR(120)` | NN | Nombre de la persona o cuenta comercial. |
| `correo` | `VARCHAR(254)` | UQ* NN | Único tras aplicar lower y btrim; índice por expresión. |
| `contrasena_hash` | `VARCHAR(255)` | NN | Hash BCrypt generado por el servicio; nunca contraseña en claro. |
| `activo` | `BOOLEAN` | NN | Permite desactivar una cuenta conservando su historia. |
| `fecha_registro` | `TIMESTAMPTZ` | NN | Instante de creación con zona horaria. |

Reglas CHECK:
- `length(btrim(nombre)) > 0`.
- `length(btrim(correo)) > 0`.
- `length(contrasena_hash) > 0`.

Correo único por índice lower(btrim(correo)). Especialización total y disjunta validada al registrar la cuenta.

## agricultor

Entidad JPA prevista: `Agricultor`. PK: `id_usuario`.

| Columna | Tipo PostgreSQL | Clave y nulabilidad | Referencia o significado |
|---|---|---|---|
| `id_usuario` | `BIGINT` | PK FK NN | FK → `usuario.id_usuario`. Misma identidad que Usuario; no genera otro ID. |
| `cedula` | `VARCHAR(20)` | UQ NN | Texto para conservar ceros iniciales. |
| `id_municipio` | `BIGINT` | FK NN | FK → `municipio.id_municipio`. Municipio de residencia del agricultor. |

Reglas CHECK:
- `length(btrim(cedula)) > 0`.

## comprador

Entidad JPA prevista: `Comprador`. PK: `id_usuario`.

| Columna | Tipo PostgreSQL | Clave y nulabilidad | Referencia o significado |
|---|---|---|---|
| `id_usuario` | `BIGINT` | PK FK NN | FK → `usuario.id_usuario`. Misma identidad que Usuario; subtipo comprador. |

Los datos comunes se consultan en usuario; no se repiten nombre, correo ni hash.

## finca

Entidad JPA prevista: `Finca`. PK: `id_finca`.

| Columna | Tipo PostgreSQL | Clave y nulabilidad | Referencia o significado |
|---|---|---|---|
| `id_finca` | `BIGINT` | PK NN | Identificador generado por PostgreSQL. |
| `id_agricultor` | `BIGINT` | FK NN | FK → `agricultor.id_usuario`. Propietario de la finca. |
| `id_municipio` | `BIGINT` | FK NN | FK → `municipio.id_municipio`. Municipio donde está ubicada la finca. |
| `nombre` | `VARCHAR(120)` | NN | Nombre de la finca. |
| `direccion` | `VARCHAR(255)` | NN | Dirección descriptiva del origen agrícola. |

Reglas CHECK:
- `length(btrim(nombre)) > 0`.
- `length(btrim(direccion)) > 0`.

## categoria

Entidad JPA prevista: `Categoria`. PK: `id_categoria`.

| Columna | Tipo PostgreSQL | Clave y nulabilidad | Referencia o significado |
|---|---|---|---|
| `id_categoria` | `BIGINT` | PK NN | Identificador generado por PostgreSQL. |
| `nombre` | `VARCHAR(80)` | UQ NN | Categoría del catálogo de productos. |

Reglas CHECK:
- `length(btrim(nombre)) > 0`.

## producto

Entidad JPA prevista: `Producto`. PK: `id_producto`.

| Columna | Tipo PostgreSQL | Clave y nulabilidad | Referencia o significado |
|---|---|---|---|
| `id_producto` | `BIGINT` | PK NN | Identificador generado por PostgreSQL. |
| `id_categoria` | `BIGINT` | FK NN | FK → `categoria.id_categoria`.  |
| `nombre` | `VARCHAR(120)` | UQ NN | Tipo de producto, por ejemplo Café o Papa; no es la oferta individual. |

Reglas CHECK:
- `length(btrim(nombre)) > 0`.

## cosecha

Entidad JPA prevista: `Cosecha`. PK: `id_cosecha`.

| Columna | Tipo PostgreSQL | Clave y nulabilidad | Referencia o significado |
|---|---|---|---|
| `id_cosecha` | `BIGINT` | PK NN | Identificador generado por PostgreSQL. |
| `id_finca` | `BIGINT` | FK NN | FK → `finca.id_finca`.  |
| `id_producto` | `BIGINT` | FK NN | FK → `producto.id_producto`.  |
| `precio_cop_kg` | `NUMERIC(14,2)` | NN | Precio actual en COP por kilogramo; positivo. |
| `cantidad_fisica_kg` | `NUMERIC(14,3)` | NN | Stock físico; admite cero después de las ventas. |
| `fecha_cosecha` | `DATE` | NN | Fecha agrícola; se valida en el momento de publicar. |
| `activa` | `BOOLEAN` | NN | Oferta visible en catálogo mientras esté activa. |

Reglas CHECK:
- `precio_cop_kg > 0`.
- `cantidad_fisica_kg >= 0`.

Agricultor y municipio se obtienen por finca; categoría se obtiene por producto. Disponibilidad es derivada.

## carrito

Entidad JPA prevista: `Carrito`. PK: `id_carrito`.

| Columna | Tipo PostgreSQL | Clave y nulabilidad | Referencia o significado |
|---|---|---|---|
| `id_carrito` | `BIGINT` | PK NN | Identificador generado por PostgreSQL. |
| `id_comprador` | `BIGINT` | FK NN | FK → `comprador.id_usuario`.  |
| `fecha_creacion` | `TIMESTAMPTZ` | NN |  |
| `estado` | `VARCHAR(12)` | NN | ABIERTO o CERRADO; las reservas históricas se conservan. |

Reglas CHECK:
- `estado IN ('ABIERTO','CERRADO')`.

## reserva

Entidad JPA prevista: `Reserva`. PK: `id_reserva`.

| Columna | Tipo PostgreSQL | Clave y nulabilidad | Referencia o significado |
|---|---|---|---|
| `id_reserva` | `BIGINT` | PK NN | Identificador generado por PostgreSQL. |
| `id_carrito` | `BIGINT` | FK NN | FK → `carrito.id_carrito`.  |
| `id_cosecha` | `BIGINT` | FK NN | FK → `cosecha.id_cosecha`.  |
| `cantidad_kg` | `NUMERIC(14,3)` | NN | Cantidad temporalmente comprometida; positiva. |
| `precio_aceptado_cop_kg` | `NUMERIC(14,2)` | NN | Precio que aceptó el comprador al reservar. |
| `fecha_creacion` | `TIMESTAMPTZ` | NN |  |
| `vence_en` | `TIMESTAMPTZ` | NN | Exactamente 15 minutos después de la creación. |
| `estado` | `VARCHAR(12)` | NN | ACTIVA, CONSUMIDA o VENCIDA. |

Reglas CHECK:
- `cantidad_kg > 0`.
- `precio_aceptado_cop_kg > 0`.
- `vence_en = fecha_creacion + INTERVAL '15 minutes'`.
- `estado IN ('ACTIVA','CONSUMIDA','VENCIDA')`.

## pedido

Entidad JPA prevista: `Pedido`. PK: `id_pedido`.

| Columna | Tipo PostgreSQL | Clave y nulabilidad | Referencia o significado |
|---|---|---|---|
| `id_pedido` | `BIGINT` | PK NN | Identificador generado por PostgreSQL. |
| `id_carrito` | `BIGINT` | FK NN | FK → `carrito.id_carrito`. Carrito de origen; permite conocer al comprador sin duplicarlo. |
| `id_agricultor` | `BIGINT` | FK NN | FK → `agricultor.id_usuario`. Único agricultor que atiende este pedido. |
| `fecha_confirmacion` | `TIMESTAMPTZ` | NN |  |
| `estado` | `VARCHAR(24)` | NN |  |
| `fecha_completado` | `TIMESTAMPTZ` | Admite NULL | Obligatoria solo cuando el pedido está ENTREGADO. |

Reglas CHECK:
- `estado IN ('PENDIENTE','LISTO_PARA_DESPACHO','EN_CAMINO','ENTREGADO','CANCELADA')`.
- `((estado = 'ENTREGADO' AND fecha_completado IS NOT NULL) OR (estado <> 'ENTREGADO' AND fecha_completado IS NULL))`.
- `fecha_completado >= fecha_confirmacion`.

No repite id_comprador ni total. Clave de reintento en idempotencia_pedido; total se calcula en una vista.

## detalle_pedido

Entidad JPA prevista: `DetallePedido`. PK: `id_detalle`.

| Columna | Tipo PostgreSQL | Clave y nulabilidad | Referencia o significado |
|---|---|---|---|
| `id_detalle` | `BIGINT` | PK NN | Identificador generado por PostgreSQL. |
| `id_pedido` | `BIGINT` | FK NN | FK → `pedido.id_pedido`.  |
| `id_reserva` | `BIGINT` | FK UQ NN | FK → `reserva.id_reserva`. Una reserva consumida origina como máximo un detalle. |
| `cantidad_kg` | `NUMERIC(14,3)` | NN | Cantidad histórica confirmada, inmutable tras confirmar. |
| `precio_unitario_cop_kg` | `NUMERIC(14,2)` | NN | Precio histórico de la venta, no el precio actual de la oferta. |

Reglas CHECK:
- `cantidad_kg > 0`.
- `precio_unitario_cop_kg > 0`.

La cosecha se obtiene por reserva. El subtotal se calcula; no se almacena una copia del precio actual de cosecha.

## envio

Entidad JPA prevista: `Envio`. PK: `id_envio`.

| Columna | Tipo PostgreSQL | Clave y nulabilidad | Referencia o significado |
|---|---|---|---|
| `id_envio` | `BIGINT` | PK NN | Identificador generado por PostgreSQL. |
| `id_pedido` | `BIGINT` | FK UQ NN | FK → `pedido.id_pedido`. A lo sumo un envío por pedido. |
| `fecha_programada` | `TIMESTAMPTZ` | NN | Fecha futura validada al programar, no con CHECK dependiente de hoy. |
| `ruta_descriptiva` | `VARCHAR(500)` | NN | Descripción logística; no representa cálculo automático de rutas. |

Reglas CHECK:
- `length(btrim(ruta_descriptiva)) > 0`.

## evento_seguimiento

Entidad JPA prevista: `EventoSeguimiento`. PK: `id_evento`.

| Columna | Tipo PostgreSQL | Clave y nulabilidad | Referencia o significado |
|---|---|---|---|
| `id_evento` | `BIGINT` | PK NN | Identificador generado por PostgreSQL. |
| `id_pedido` | `BIGINT` | FK NN | FK → `pedido.id_pedido`.  |
| `estado` | `VARCHAR(24)` | NN |  |
| `fecha_evento` | `TIMESTAMPTZ` | NN |  |
| `descripcion` | `VARCHAR(500)` | Admite NULL | Detalle opcional del evento histórico. |

Reglas CHECK:
- `estado IN ('PENDIENTE','LISTO_PARA_DESPACHO','EN_CAMINO','ENTREGADO','CANCELADA')`.

## contacto

Entidad JPA prevista: `Contacto`. PK: `id_contacto`.

| Columna | Tipo PostgreSQL | Clave y nulabilidad | Referencia o significado |
|---|---|---|---|
| `id_contacto` | `BIGINT` | PK NN | Identificador generado por PostgreSQL. |
| `id_comprador` | `BIGINT` | FK NN | FK → `comprador.id_usuario`.  |
| `id_cosecha` | `BIGINT` | FK NN | FK → `cosecha.id_cosecha`.  |
| `mensaje` | `VARCHAR(1000)` | NN | Mensaje obligatorio, de hasta 1000 caracteres. |
| `fecha_contacto` | `TIMESTAMPTZ` | NN |  |
| `estado` | `VARCHAR(12)` | NN | ENVIADO, LEIDO o CONTESTADO. |

Reglas CHECK:
- `length(btrim(mensaje)) > 0`.
- `estado IN ('ENVIADO','LEIDO','CONTESTADO')`.

El agricultor receptor se obtiene por cosecha → finca; no se duplica id_agricultor.

## notificacion

Entidad JPA prevista: `Notificacion`. PK: `id_notificacion`.

| Columna | Tipo PostgreSQL | Clave y nulabilidad | Referencia o significado |
|---|---|---|---|
| `id_notificacion` | `BIGINT` | PK NN | Identificador generado por PostgreSQL. |
| `id_destinatario` | `BIGINT` | FK NN | FK → `usuario.id_usuario`.  |
| `id_contacto` | `BIGINT` | FK UQ | FK → `contacto.id_contacto`. Origen opcional, exclusivo respecto del evento. |
| `id_evento` | `BIGINT` | FK | FK → `evento_seguimiento.id_evento`. Origen opcional, exclusivo respecto del contacto. |
| `tipo` | `VARCHAR(32)` | NN |  |
| `contenido` | `TEXT` | NN | Texto del aviso interno dirigido a una cuenta. |
| `fecha_creacion` | `TIMESTAMPTZ` | NN |  |
| `leida` | `BOOLEAN` | NN |  |

Reglas CHECK:
- `((id_contacto IS NOT NULL AND id_evento IS NULL) OR (id_contacto IS NULL AND id_evento IS NOT NULL))`.
- `length(btrim(tipo)) > 0`.
- `length(btrim(contenido)) > 0`.

CHECK XOR: exactamente un origen. UNIQUE(id_contacto): como máximo un aviso por contacto.

## idempotencia_pedido

Entidad JPA prevista: `IdempotenciaPedido`. PK: `id_comprador, clave`.

| Columna | Tipo PostgreSQL | Clave y nulabilidad | Referencia o significado |
|---|---|---|---|
| `id_comprador` | `BIGINT` | PK FK NN | FK → `comprador.id_usuario`.  |
| `clave` | `VARCHAR(128)` | PK NN | Clave de reintento enviada por el comprador. |
| `id_pedido` | `BIGINT` | FK UQ NN | FK → `pedido.id_pedido`. Pedido ya creado para esta combinación de comprador y clave. |
| `huella_solicitud` | `CHAR(64)` | NN | SHA-256 de la solicitud canonizada; detecta cambios de contenido. |
| `fecha_creacion` | `TIMESTAMPTZ` | NN |  |

Reglas CHECK:
- `length(btrim(clave)) > 0`.
- `huella_solicitud ~ '^[0-9a-f]{64}$'`.

Tabla técnica que permite UNIQUE por comprador y clave sin repetir id_comprador dentro de pedido.
