# Product Backlog - AgroValle Connect

A continuación se presentan las 15 Historias de Usuario del proyecto, especificadas con criterios BDD, priorización MoSCoW y estimadas en Story Points (Fibonacci).

## Historias Base (Proporcionadas en Sprint 0)

| ID | Historia de Usuario | Priorización | Estimación (Fibo) |
|---|---|---|---|
| **HU-01** | Registro de Agricultores | Must Have | 5 Points |
| **HU-02** | Publicación de Productos | Must Have | 5 Points |
| **HU-03** | Visualización de Precios Regionales | Should Have | 3 Points |
| **HU-04** | Filtro de Categorías y Municipios | Must Have | 3 Points |
| **HU-05** | Contacto Directo / Intención de Compra | Must Have | 5 Points |

## Historias de Usuario Nuevas (Sprint 0 - Elaboración Propia)

### HU-06: Gestión del Carrito de Compras
*   **Historia:** Como Comprador, quiero añadir productos a un carrito de compras temporal, para poder agrupar varias compras antes de confirmar el pedido final.
*   **Priorización (MoSCoW):** S (Should Have)
*   **Estimación (Fibonacci):** 8 Points
*   **Escenario BDD:**
    *   **Given** un comprador autenticado con JWT que visualiza un producto activo con stock disponible.
    *   **When** hace una petición POST a `/api/v1/carrito/agregar` con el `id_producto` y la cantidad deseada.
    *   **Then** el sistema reserva temporalmente el stock en PostgreSQL, calcula el subtotal y retorna status 200 OK con el detalle del carrito actualizado.

### HU-07: Emisión de Órdenes de Compra
*   **Historia:** Como Comprador, quiero generar una orden de compra formal a partir de mi carrito, para iniciar el proceso logístico y de pago con el agricultor.
*   **Priorización (MoSCoW):** M (Must Have)
*   **Estimación (Fibonacci):** 13 Points
*   **Escenario BDD:**
    *   **Given** un comprador con un carrito de compras no vacío y sesión válida.
    *   **When** envía una petición POST a `/api/v1/ordenes/checkout` confirmando los términos.
    *   **Then** el sistema crea la orden en base de datos con estado "PENDIENTE", descuenta el inventario definitivo y retorna status 201 Created con el ID de la orden.

### HU-08: Confirmación de Alistamiento (Productor)
*   **Historia:** Como Agricultor, quiero confirmar que un pedido está listo para ser despachado, para que el comprador reciba una notificación de alistamiento.
*   **Priorización (MoSCoW):** M (Must Have)
*   **Estimación (Fibonacci):** 5 Points
*   **Escenario BDD:**
    *   **Given** un agricultor autenticado y una orden asignada a él en estado "PENDIENTE".
    *   **When** hace una petición PUT a `/api/v1/ordenes/{id}/estado` con el valor "LISTO_PARA_DESPACHO".
    *   **Then** el sistema actualiza el estado en PostgreSQL y envía una notificación automática al comprador (status 200 OK).

### HU-09: Trazabilidad del Despacho
*   **Historia:** Como Comprador, quiero ver el historial de estados de mi orden (Pendiente, En camino, Entregado), para hacer seguimiento logístico a mi compra.
*   **Priorización (MoSCoW):** S (Should Have)
*   **Estimación (Fibonacci):** 8 Points
*   **Escenario BDD:**
    *   **Given** un usuario autenticado que posee una orden activa con ID válido.
    *   **When** realiza una petición GET a `/api/v1/ordenes/{id}/trazabilidad`.
    *   **Then** el sistema retorna un array JSON ordenado cronológicamente con los eventos logísticos de la orden y status 200 OK.

### HU-10: Calificación del Agricultor
*   **Historia:** Como Comprador, quiero calificar a un agricultor tras recibir un pedido, para generar confianza y reputación dentro del ecosistema del Valle.
*   **Priorización (MoSCoW):** C (Could Have)
*   **Estimación (Fibonacci):** 3 Points
*   **Escenario BDD:**
    *   **Given** una orden en estado "ENTREGADO" y un comprador autenticado.
    *   **When** el usuario envía un POST a `/api/v1/calificaciones` con una puntuación (1-5) y un comentario breve.
    *   **Then** el sistema asocia la calificación al perfil del agricultor y recalcula su promedio en la base de datos (status 201 Created).

### HU-11: Dashboard de Ventas (Agricultor)
*   **Historia:** Como Agricultor, quiero ver un resumen gráfico de mis ventas mensuales y productos más populares, para tomar mejores decisiones sobre mis próximas cosechas.
*   **Priorización (MoSCoW):** S (Should Have)
*   **Estimación (Fibonacci):** 13 Points
*   **Escenario BDD:**
    *   **Given** un agricultor autenticado en el sistema.
    *   **When** solicita el endpoint GET `/api/v1/dashboard/ventas?mes=09&anio=2026`.
    *   **Then** el sistema procesa los datos agregados y retorna un JSON (status 200 OK) con métricas de ingresos totales y top 3 de productos vendidos.

### HU-12: Recuperación de Contraseña
*   **Historia:** Como Usuario (Agricultor o Comprador), quiero solicitar el restablecimiento de mi contraseña mediante un correo electrónico, para recuperar el acceso a mi cuenta si la olvido.
*   **Priorización (MoSCoW):** S (Should Have)
*   **Estimación (Fibonacci):** 5 Points
*   **Escenario BDD:**
    *   **Given** que un usuario no autenticado ingresa su correo en la vista de recuperación.
    *   **When** hace una petición POST a `/api/v1/auth/recuperar-password` con un correo existente.
    *   **Then** el sistema genera un token temporal de un solo uso y simula el envío de un correo de recuperación, retornando status 200 OK.

### HU-13: Edición de Ofertas Publicadas
*   **Historia:** Como Agricultor, quiero modificar el precio o la cantidad de una oferta ya publicada, para ajustar mis publicaciones a la realidad del mercado (por ej. si se dañó parte de la cosecha).
*   **Priorización (MoSCoW):** M (Must Have)
*   **Estimación (Fibonacci):** 3 Points
*   **Escenario BDD:**
    *   **Given** un agricultor autenticado que es propietario de una oferta activa.
    *   **When** envía una petición PUT a `/api/v1/productos/{id}` modificando `precio` y `cantidad`.
    *   **Then** el sistema valida los nuevos datos, actualiza el registro en PostgreSQL y retorna la oferta modificada (status 200 OK).

### HU-14: Cancelación de Orden (Comprador)
*   **Historia:** Como Comprador, quiero cancelar una orden antes de que el agricultor la empiece a alistar, para corregir errores de compra.
*   **Priorización (MoSCoW):** C (Could Have)
*   **Estimación (Fibonacci):** 8 Points
*   **Escenario BDD:**
    *   **Given** una orden en estado "PENDIENTE" y un comprador dueño de la orden.
    *   **When** el comprador hace una petición DELETE lógico a `/api/v1/ordenes/{id}`.
    *   **Then** el sistema verifica el estado, marca la orden como "CANCELADA", libera el inventario reservado y retorna status 200 OK.

### HU-15: Historial de Compras (Comprador)
*   **Historia:** Como Comprador, quiero ver un listado de todas las compras que he realizado históricamente, para llevar control de mis gastos.
*   **Priorización (MoSCoW):** S (Should Have)
*   **Estimación (Fibonacci):** 3 Points
*   **Escenario BDD:**
    *   **Given** un comprador con token JWT válido.
    *   **When** hace una petición GET a `/api/v1/usuarios/me/compras`.
    *   **Then** el sistema retorna una lista paginada en JSON con las órdenes asociadas a ese usuario (status 200 OK).