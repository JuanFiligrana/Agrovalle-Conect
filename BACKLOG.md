# Product Backlog de AgroValle Connect

Versión propuesta para Sprint 0. Las historias describen funcionalidades por desarrollar;
no se presentan como implementadas. Los puntos son estimaciones fundamentadas que el
equipo debe ratificar mediante Planning Poker. No se inventan votos ni consensos.

## Priorización y estimación
M = Must Have (esencial); S = Should Have (importante); C = Could Have (deseable);
W = Won't Have (fuera del alcance actual). No se exige usar las cuatro etiquetas.
Escala Fibonacci: 1, 2, 3, 5, 8, 13. Referencia: 3 puntos para una operación simple,
5 para varias validaciones y 8 para transacciones o concurrencia. No son horas.

| ID | Historia | MoSCoW | Story Points |
|---|---|---|---|
| HU-01 | Registro de agricultores | M | 5 |
| HU-02 | Publicación de cosechas | M | 5 |
| HU-03 | Consulta de precios regionales | S | 5 |
| HU-04 | Filtro por categoría y municipio | M | 3 |
| HU-05 | Contacto directo con el agricultor | M | 5 |
| HU-06 | Registro de compradores | M | 3 |
| HU-07 | Inicio de sesión seguro | M | 5 |
| HU-08 | Registro de fincas | M | 3 |
| HU-09 | Actualización de ofertas | M | 5 |
| HU-10 | Consulta del detalle de una cosecha | M | 3 |
| HU-11 | Carrito con reserva temporal | M | 8 |
| HU-12 | Confirmación de orden de compra | M | 8 |
| HU-13 | Programación y actualización del despacho | M | 8 |
| HU-14 | Seguimiento de la entrega | S | 5 |
| HU-15 | Cancelación de una orden pendiente | C | 5 |

## Especificación BDD

### HU-01 Registro de agricultores

Como agricultor, quiero registrarme en la plataforma, para ofrecer mis productos.

**MoSCoW:** M · **Story Points:** 5

**Fundamento:** Validación de identidad, unicidad y persistencia segura.

**Escenario: Registro válido**
- **Given** no existe una cuenta con la cédula ni el correo enviados.
- **When** se envía POST /api/v1/auth/register con nombre, correo, contraseña, cédula, ubicacion_valle y rol AGRICULTOR válidos.
- **Then** responde 201 Created con el ID y persiste la cuenta en PostgreSQL con hash seguro de contraseña, sin devolverla.

**Escenario: Cuenta duplicada**
- **Given** el correo ya pertenece a una cuenta.
- **When** se intenta otro registro con ese correo.
- **Then** responde 409 Conflict y no crea otra cuenta.

**Escenario: Datos inválidos**
- **Given** la cédula está vacía o el municipio no pertenece al catálogo del Valle.
- **When** se solicita el registro.
- **Then** responde 400 Bad Request con los campos inválidos y no persiste cambios.

### HU-02 Publicación de cosechas

Como agricultor, quiero publicar mis cosechas disponibles, para recibir solicitudes de compradores.

**MoSCoW:** M · **Story Points:** 5

**Fundamento:** Relaciona productor y finca; valida cantidad, precio y fecha.

**Escenario: Publicación válida**
- **Given** un agricultor tiene JWT válido y una finca propia registrada.
- **When** envía POST /api/v1/productos con finca_id, tipo, categoría, cantidad_kg y precio_cop_kg positivos y fecha_cosecha igual o posterior a hoy.
- **Then** responde 201 Created con ID único y guarda la oferta activa en PostgreSQL asociada al agricultor.

**Escenario: Fecha incorrecta**
- **Given** el agricultor está autenticado.
- **When** publica con fecha_cosecha anterior a hoy.
- **Then** responde 400 Bad Request y no crea la oferta.

**Escenario: Finca ajena**
- **Given** la finca pertenece a otro agricultor.
- **When** intenta publicar una cosecha en esa finca.
- **Then** responde 403 Forbidden y conserva los registros existentes.

### HU-03 Consulta de precios regionales

Como usuario, quiero consultar los precios promedio del Valle, para contar con una referencia para negociar.

**MoSCoW:** S · **Story Points:** 5

**Fundamento:** Agregación por producto y ventana temporal con redondeo definido.

**Escenario: Promedio regional**
- **Given** existen 50 transacciones completadas de Café en las últimas 24 horas con precios unitarios en COP/kg.
- **When** se consulta GET /api/v1/precios/promedio?producto=Cafe.
- **Then** responde 200 OK con la media aritmética no ponderada de esos 50 precios, redondeada a dos decimales, unidad COP/kg y cantidad_transacciones 50.

**Escenario: Sin transacciones**
- **Given** no hay transacciones completadas del producto en ese período.
- **When** se consulta su promedio.
- **Then** responde 200 OK con promedio null y cantidad_transacciones 0.

### HU-04 Filtro por categoría y municipio

Como comprador, quiero filtrar cosechas por categoría y municipio, para encontrar ofertas locales de mi interés.

**MoSCoW:** M · **Story Points:** 3

**Fundamento:** Consulta combinada de filtros sobre ofertas activas.

**Escenario: Filtros combinados**
- **Given** existen ofertas activas de Frutas en Dagua y otras que no cumplen ambos filtros.
- **When** se consulta GET /api/v1/productos?municipio=Dagua&categoria=Frutas.
- **Then** responde 200 OK con un arreglo JSON que contiene solamente ofertas activas que cumplen ambos filtros.

**Escenario: Sin coincidencias**
- **Given** ninguna oferta cumple la combinación.
- **When** se realiza la consulta filtrada.
- **Then** responde 200 OK con un arreglo vacío.

**Escenario: Categoría inválida**
- **Given** la categoría no existe en el catálogo.
- **When** se consulta con esa categoría.
- **Then** responde 400 Bad Request e identifica el filtro inválido.

### HU-05 Contacto directo con el agricultor

Como comprador, quiero enviar una solicitud de contacto al agricultor, para acordar condiciones de compra y logística.

**MoSCoW:** M · **Story Points:** 5

**Fundamento:** Autorización, persistencia del mensaje y notificación interna.

**Escenario: Mensaje registrado**
- **Given** un comprador tiene JWT válido y existe una oferta activa.
- **When** envía POST /api/v1/contacto/mensaje con id_producto y mensaje no vacío de hasta 1000 caracteres.
- **Then** responde 200 OK con ID de interacción y confirmación de notificación interna registrada; persiste ambos registros en PostgreSQL.

**Escenario: Sin autenticación**
- **Given** no hay JWT válido.
- **When** se intenta enviar el mensaje.
- **Then** responde 401 Unauthorized sin persistir la interacción.

**Escenario: Oferta inexistente**
- **Given** id_producto no corresponde a una oferta.
- **When** se envía el mensaje con JWT válido.
- **Then** responde 404 Not Found y no registra el contacto.

### HU-06 Registro de compradores

Como comprador, quiero crear una cuenta comercial, para adquirir cosechas directamente.

**MoSCoW:** M · **Story Points:** 3

**Fundamento:** Reutiliza las validaciones de registro y diferencia el rol.

**Escenario: Cuenta comercial**
- **Given** el correo del comprador no está registrado.
- **When** envía POST /api/v1/auth/register con nombre, correo, contraseña y rol COMPRADOR válidos.
- **Then** responde 201 Created y guarda la cuenta con hash de contraseña en PostgreSQL, sin devolver la contraseña.

**Escenario: Correo duplicado**
- **Given** el correo ya existe.
- **When** se solicita el registro de comprador.
- **Then** responde 409 Conflict y no duplica la cuenta.

### HU-07 Inicio de sesión seguro

Como usuario registrado, quiero iniciar sesión, para acceder a las funciones de mi rol.

**MoSCoW:** M · **Story Points:** 5

**Fundamento:** Validación de credenciales, emisión y caducidad del JWT.

**Escenario: Credenciales válidas**
- **Given** existe una cuenta activa y su contraseña corresponde al hash almacenado.
- **When** envía POST /api/v1/auth/login con correo y contraseña correctos.
- **Then** responde 200 OK con token JWT, tipo Bearer y expires_in 3600, sin incluir la contraseña.

**Escenario: Credenciales incorrectas**
- **Given** la contraseña es incorrecta o el correo no existe.
- **When** se intenta iniciar sesión.
- **Then** responde 401 Unauthorized con mensaje genérico y no entrega token.

**Escenario: Token vencido**
- **Given** el token ya superó su fecha de expiración.
- **When** se solicita un recurso protegido.
- **Then** responde 401 Unauthorized y no realiza la operación.

### HU-08 Registro de fincas

Como agricultor, quiero registrar mis fincas por municipio, para identificar el origen de mis cosechas.

**MoSCoW:** M · **Story Points:** 3

**Fundamento:** Formulario y persistencia con catálogo de municipios y propietario.

**Escenario: Finca registrada**
- **Given** el agricultor tiene JWT válido.
- **When** envía POST /api/v1/fincas con nombre, municipio del Valle y dirección descriptiva no vacíos.
- **Then** responde 201 Created y guarda la finca en PostgreSQL con el agricultor autenticado como propietario.

**Escenario: Rol no autorizado**
- **Given** el usuario autenticado tiene rol COMPRADOR.
- **When** solicita registrar una finca.
- **Then** responde 403 Forbidden y no crea la finca.

### HU-09 Actualización de ofertas

Como agricultor, quiero modificar precio y cantidad de mis ofertas, para mantener información confiable.

**MoSCoW:** M · **Story Points:** 5

**Fundamento:** Propiedad de la oferta y coherencia con reservas activas.

**Escenario: Cambio válido**
- **Given** el agricultor es propietario de una oferta con 20 kg físicos y 5 kg reservados.
- **When** envía PUT /api/v1/productos/{id} con precio positivo y cantidad_fisica_kg 18.
- **Then** responde 200 OK y persiste cantidad física 18, reservada 5 y disponible 13, sin cambiar precios de órdenes ya confirmadas.

**Escenario: Cantidad incompatible**
- **Given** la oferta tiene 5 kg reservados.
- **When** solicita reducir la cantidad física a 4 kg.
- **Then** responde 409 Conflict y no modifica la oferta.

**Escenario: Oferta ajena**
- **Given** la oferta pertenece a otro agricultor.
- **When** un agricultor distinto intenta modificarla.
- **Then** responde 403 Forbidden y no altera sus datos.

### HU-10 Consulta del detalle de una cosecha

Como comprador, quiero ver el detalle de una oferta, para evaluar sus condiciones antes de comprar.

**MoSCoW:** M · **Story Points:** 3

**Fundamento:** Lectura pública con contrato de respuesta y disponibilidad.

**Escenario: Oferta visible**
- **Given** existe una oferta activa.
- **When** se consulta GET /api/v1/productos/{id}.
- **Then** responde 200 OK con ID, productor, finca, municipio, categoría, precio COP/kg, cantidad disponible y fecha de cosecha, sin datos privados de la cuenta.

**Escenario: Oferta no disponible**
- **Given** la oferta no existe o está inactiva.
- **When** se consulta su identificador.
- **Then** responde 404 Not Found.

### HU-11 Carrito con reserva temporal

Como comprador, quiero agregar cosechas a mi carrito, para asegurar temporalmente la disponibilidad antes de confirmar.

**MoSCoW:** M · **Story Points:** 8

**Fundamento:** Reserva atómica, expiración y control de concurrencia.

**Escenario: Reserva válida**
- **Given** el comprador tiene JWT válido y hay 10 kg disponibles.
- **When** envía POST /api/v1/carrito/items con id_producto y cantidad_kg 3.
- **Then** responde 200 OK con subtotal, precio unitario y vencimiento a 15 minutos; guarda una reserva de 3 kg y deja 7 kg disponibles sin descontar todavía el stock físico.

**Escenario: Stock insuficiente**
- **Given** quedan 2 kg disponibles.
- **When** se solicitan 3 kg.
- **Then** responde 409 Conflict y no altera carrito ni inventario, incluso ante solicitudes concurrentes.

**Escenario: Reserva vencida**
- **Given** una reserva no confirmada supera los 15 minutos.
- **When** el sistema procesa su vencimiento o vuelve a consultar disponibilidad.
- **Then** libera la reserva una sola vez y devuelve esa cantidad al stock disponible.

### HU-12 Confirmación de orden de compra

Como comprador, quiero confirmar mi carrito, para formalizar un pedido directo.

**MoSCoW:** M · **Story Points:** 8

**Fundamento:** Transacción de inventario y orden con control de confirmaciones repetidas.

**Escenario: Orden confirmada**
- **Given** el carrito contiene reservas vigentes de un solo agricultor y precios aceptados.
- **When** envía POST /api/v1/ordenes con carrito_id y una clave de idempotencia única.
- **Then** responde 201 Created con orden PENDIENTE y total; descuenta cantidades del stock físico, consume las reservas y vacía el carrito en una transacción PostgreSQL sin descontar dos veces.

**Escenario: Confirmación repetida**
- **Given** la clave de idempotencia ya produjo una orden.
- **When** repite la misma petición y contenido.
- **Then** responde 200 OK con la orden existente y no vuelve a descontar inventario.

**Escenario: Reserva vencida**
- **Given** al menos una reserva venció antes de confirmar.
- **When** se intenta crear la orden.
- **Then** responde 409 Conflict, no crea orden parcial y solicita actualizar el carrito.

### HU-13 Programación y actualización del despacho

Como agricultor, quiero registrar el alistamiento y despacho de mis pedidos, para coordinar la entrega con el comprador.

**MoSCoW:** M · **Story Points:** 8

**Fundamento:** Máquina de estados, propiedad de la orden y eventos logísticos.

**Escenario: Alistamiento programado**
- **Given** el agricultor es dueño de una orden PENDIENTE.
- **When** envía PATCH /api/v1/ordenes/{id}/estado con LISTO_PARA_DESPACHO, fecha programada futura y ruta descriptiva.
- **Then** responde 200 OK, persiste el estado y la programación, agrega un evento con fecha y registra una notificación interna al comprador.

**Escenario: Avance permitido**
- **Given** la orden está LISTO_PARA_DESPACHO.
- **When** el agricultor la actualiza a EN_CAMINO.
- **Then** responde 200 OK y agrega el evento de salida; desde EN_CAMINO permite pasar a ENTREGADO.

**Escenario: Transición inválida**
- **Given** la orden está CANCELADA.
- **When** se intenta cambiarla a EN_CAMINO.
- **Then** responde 409 Conflict y conserva su estado.

### HU-14 Seguimiento de la entrega

Como comprador, quiero consultar la trazabilidad de mi pedido, para conocer el avance del despacho.

**MoSCoW:** S · **Story Points:** 5

**Fundamento:** Historial cronológico y control de acceso por propietario.

**Escenario: Seguimiento autorizado**
- **Given** el comprador tiene JWT válido y una orden propia con eventos logísticos.
- **When** consulta GET /api/v1/ordenes/{id}/trazabilidad.
- **Then** responde 200 OK con estado actual, programación y eventos ordenados de más antiguo a más reciente, actualizados al momento de la consulta.

**Escenario: Orden ajena**
- **Given** el comprador no es dueño de la orden.
- **When** solicita su trazabilidad.
- **Then** responde 403 Forbidden y no muestra datos del otro comprador.

### HU-15 Cancelación de una orden pendiente

Como comprador, quiero cancelar una orden antes del alistamiento, para corregir una compra realizada por error.

**MoSCoW:** C · **Story Points:** 5

**Fundamento:** Restitución de stock y operación idempotente por estado.

**Escenario: Cancelación válida**
- **Given** el comprador es dueño de una orden PENDIENTE que descontó 3 kg de stock físico.
- **When** envía POST /api/v1/ordenes/{id}/cancelacion.
- **Then** responde 200 OK, marca CANCELADA, devuelve 3 kg al stock físico y registra el evento en una transacción PostgreSQL.

**Escenario: Cancelación repetida**
- **Given** la orden ya está CANCELADA.
- **When** se repite la solicitud por su propietario.
- **Then** responde 200 OK con el estado existente y no vuelve a incrementar el inventario.

**Escenario: Alistamiento iniciado**
- **Given** la orden está LISTO_PARA_DESPACHO, EN_CAMINO o ENTREGADO.
- **When** se solicita cancelar.
- **Then** responde 409 Conflict y no modifica orden ni inventario.

## Reglas comunes y dependencias
- Contratos REST propuestos; la implementación debe respetar validación, autenticación
  y autorización. Operaciones protegidas sin token válido devuelven 401 y un rol o
  propietario no autorizado recibe 403. Los JSON de error usan code, message y fields.
- Cantidades en kg con hasta tres decimales; precios en COP/kg con dos decimales,
  calculados con BigDecimal. Fechas de negocio en America/Bogota; eventos con zona horaria.
- Hoy significa la fecha de Colombia. El catálogo de municipios y categorías se
  versionará al implementar; no se asume una integración externa disponible.
- Stock disponible = stock físico menos reservas activas. La confirmación consume la
  reserva y descuenta el stock físico en la misma transacción; no hay doble descuento.
- Para el primer incremento, un carrito reúne ofertas de un mismo agricultor. Mezclar
  agricultores devuelve 409; consolidación de varios agricultores queda fuera del MVP.
- HU-01/HU-06 preceden a HU-07; HU-07 habilita operaciones protegidas; HU-08 precede
  a HU-02. HU-11 depende de ofertas activas; HU-12 depende de HU-11. HU-13/HU-14/HU-15
  dependen de una orden. HU-03 necesita transacciones completadas para tener datos reales.
- Cada historia debe ser entregable y testeable. Las dependencias se hacen explícitas
  y se pueden usar fixtures de prueba. Las historias de 8 puntos deben dividirse si
  el equipo considera que no caben en un sprint; la prioridad C de HU-15 no bloquea pedidos.
- Fuera del MVP: pasarela de pago, rastreo GPS continuo y rutas calculadas automáticamente.
  La trazabilidad es por eventos; el pago no está implementado ni simulado como real.

## Planning Poker pendiente de ratificación
1. Leer la historia, aclarar alcance, dependencias y escenarios de error.
2. Cada integrante elige una carta Fibonacci de forma independiente.
3. Revelar, discutir los extremos y volver a votar hasta acordar el tamaño.
4. Registrar fecha, asistentes, punto final y motivo si cambió la propuesta.
Usar docs/estimacion.md para registrar acuerdos reales.
