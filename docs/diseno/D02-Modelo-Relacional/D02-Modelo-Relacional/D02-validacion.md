# D02 · Validación del esquema SQL

Se ejecutó el DDL y se probaron sus vistas en una base efímera con PostgreSQL 18.6 mediante PGlite 0.5.10. No se ejecutó en una base del proyecto ni se modificó el backend.

Resultado: **19 comprobaciones aprobadas de 19**.

| Comprobación | Resultado |
|---|---|
| 17 tablas base creadas | Aprobada |
| Correo normalizado único | Aprobada |
| FK de finca sin agricultor | Aprobada |
| Precio positivo | Aprobada |
| Reserva con cantidad positiva | Aprobada |
| Reserva dura 15 minutos | Aprobada |
| Disponibilidad resta reservas vigentes | Aprobada |
| Entrega exige fecha completado | Aprobada |
| Confirmar no vuelve a restar la reserva consumida en la vista | Aprobada |
| Precio promedio histórico no ponderado y ventana 24h | Aprobada |
| Sin ventas devuelve null y cero | Aprobada |
| Total histórico del pedido | Aprobada |
| Una reserva no origina dos detalles | Aprobada |
| Notificación sin origen | Aprobada |
| Notificación con dos orígenes | Aprobada |
| Un aviso por contacto | Aprobada |
| Idempotencia única por comprador y clave | Aprobada |
| Máximo un envío por pedido | Aprobada |
| FK de destinatario obligatoria | Aprobada |

## Alcance

La comprobación cubre la creación de tablas, restricciones estructurales y consultas calculadas. No certifica concurrencia, control de acceso ni los servicios transaccionales pendientes. Las reglas que requieren transacciones se detallan en D02-normalizacion.md.
