# AgroValle Connect — Sprint 4: planificación y verificación

**Asignatura:** Ingeniería de Software II  
**Grupo académico:** 6303B  
**Docente:** Paola Andrea Bedoya Toro  
**Fecha de elaboración de esta planificación:** 5 de noviembre de 2026  
**Estado:** planificación propuesta para el tercer corte; pendiente de ejecución de las actividades de verificación y cierre formal.  
**Referencia:** [Proyección general de siete sprints](proyeccion-sprints.md).

> **Trazabilidad e integridad:** HU-09 y HU-11 ya cuentan con implementación integrada previamente mediante el PR #56. Este documento organiza su alcance dentro del Sprint 4 proyectado, pero **no cambia las fechas históricas**, no declara un sprint realizado por el solo hecho de existir código y no atribuye retrospectivas, aprobaciones o acuerdos sin evidencia real. Los Story Points siguen siendo estimaciones por ratificar.

## 1. Objetivo del Sprint

Verificar que el agricultor pueda actualizar el precio y la cantidad física de sus propias ofertas sin violar reservas vigentes, y que un comprador autenticado pueda reservar temporalmente cosechas en un carrito con disponibilidad consistente, vencimiento a 15 minutos y protección frente a acceso ajeno o concurrencia. Demostrar el recorrido en la interfaz web y con pruebas REST.

- **Duración propuesta:** hasta dos semanas, conforme a la indicación de clase.
- **Fecha real de inicio:** por registrar.
- **Fecha real de finalización:** por registrar.
- **Capacidad de referencia:** aproximadamente 12–14 puntos por sprint; compromiso propuesto de **13 Story Points**.
- **Participantes, responsables y acuerdos:** registrar únicamente las asignaciones y reuniones que efectivamente se realicen; vincularlas con el tablero de GitHub Projects.
- **Dependencias:** Sprint 1 (registro/JWT/fincas), Sprint 2 (ofertas persistentes) y Sprint 3 proyectado (registro del comprador y detalle de cosechas).

## 2. Historias de usuario seleccionadas

| Orden | Historia | Prioridad MoSCoW | Story Points propuestos | Resultado esperado |
|---|---|---|---:|---|
| 1 | **HU-09 — Actualización de ofertas** | Must Have | **5** | El agricultor propietario actualiza cantidad física y precio, conservando la coherencia con reservas y pedidos anteriores. |
| 2 | **HU-11 — Carrito con reserva temporal** | Must Have | **8** | El comprador reserva cantidades disponibles durante 15 minutos, consulta/edita/retira ítems y no excede inventario ni permisos. |
| **Total** | **2 historias** | | **13** | **Inventario actualizado y carrito con reservas consistentes** |

Las funcionalidades de **confirmación del pedido, cancelación, despacho y seguimiento** están implementadas en el incremento de compra y logística, pero su planificación funcional corresponde a los sprints 5 y 6 proyectados. No se cuentan nuevamente como puntos del Sprint 4.

## 3. Criterios de aceptación BDD

### HU-09 — Actualización de ofertas

**Escenario 1. Modificación válida de inventario y precio**  
**Dado** un agricultor con JWT válido y una oferta propia de 20 kg físicos, de los cuales 5 kg están reservados,  
**cuando** actualiza la oferta mediante `PUT /api/v1/productos/{id}` con cantidad física de 18 kg y precio válido,  
**entonces** recibe `200 OK`, se conservan 5 kg reservados y quedan 13 kg disponibles.

**Escenario 2. Cantidad inferior a las reservas**  
**Dado** una oferta con 5 kg de reservas todavía activas,  
**cuando** el propietario intenta reducir el inventario físico a 4 kg,  
**entonces** responde `409 Conflict`, no modifica la oferta ni pierde las reservas.

**Escenario 3. Prohibición de editar una oferta ajena**  
**Dado** un agricultor autenticado que no es dueño de una oferta,  
**cuando** solicita cambiar su cantidad o precio,  
**entonces** responde `403 Forbidden` y mantiene los datos anteriores.

**Escenario 4. Precio histórico de pedidos**  
**Dado** un pedido ya confirmado al precio que tenía una oferta,  
**cuando** su propietario actualiza el precio de esa oferta,  
**entonces** el precio y total del pedido histórico permanecen sin alteraciones.

### HU-11 — Carrito con reserva temporal

**Escenario 1. Reserva disponible**  
**Dado** un comprador con JWT válido y una cosecha activa con 10 kg disponibles,  
**cuando** agrega 3 kg mediante `POST /api/v1/carrito/items`,  
**entonces** la API registra una reserva de 3 kg con vencimiento aproximado a 15 minutos, conserva los 10 kg físicos y muestra 7 kg disponibles.

**Escenario 2. Stock insuficiente y concurrencia**  
**Dado** una oferta con 2 kg disponibles,  
**cuando** se intentan reservar 3 kg, o varios compradores intentan simultáneamente reservar más que la disponibilidad total,  
**entonces** la API rechaza las reservas incompatibles con `409 Conflict` sin sobreasignar inventario.

**Escenario 3. Edición y eliminación del carrito**  
**Dado** una reserva activa en el carrito propio,  
**cuando** el comprador cambia la cantidad o elimina el ítem,  
**entonces** se recalcula la disponibilidad; al editar, la cantidad reemplaza la previa y renueva el vencimiento, sin sumar reservas duplicadas.

**Escenario 4. Reserva vencida**  
**Dado** una reserva no confirmada cuya vigencia de 15 minutos ha terminado,  
**cuando** se consulta nuevamente la disponibilidad,  
**entonces** esa reserva ya no bloquea inventario y el carrito no puede confirmar la reserva vencida sin renovarla. Consultas repetidas no duplican liberaciones.

**Escenario 5. Seguridad y propiedad del carrito**  
**Dado** un visitante sin JWT, un agricultor o un comprador distinto del propietario del carrito,  
**cuando** intenta acceder o modificar reservas privadas,  
**entonces** recibe el rechazo correspondiente (`401` o `403`) y no accede a datos de terceros.

## 4. Alcance técnico existente y trazabilidad

| Historia | Rutas principales ya documentadas | Artefactos existentes | Comprobación pendiente de registrar como Sprint 4 |
|---|---|---|---|
| **HU-09** | `GET /api/v1/mis-ofertas`, `PUT /api/v1/productos/{id}` | `ComercioController`, `ComercioService`, `ComercioRepository`, vista `agricultor.html` | Edición válida, mínimo reservado, autorización y conservación del precio del pedido anterior |
| **HU-11** | `GET /api/v1/carrito`, `POST /api/v1/carrito/items`, `DELETE /api/v1/carrito/items/{id}` | `ComercioService`, `ComercioRepository`, `carrito.html`, `assets/commerce.js` | Reservas 15 min, vencimiento, cambio/eliminación, roles, concurrencia y disponibilidad |

**Evidencia histórica:** [PR #56: compra, logística e historial de precios](https://github.com/JuanFiligrana/Agrovalle-Conect/pull/56), fusionado antes de crear esta planificación. La [documentación técnica de compra y logística](compra-logistica.md) describe transacciones, restricciones y reglas del inventario. El incremento también contiene funciones de pedidos y logística que **no se acreditan como historias adicionales del Sprint 4**.

**Pruebas existentes para revisar:**

- `ComercioAcceptanceTests`: `reservaActualizaConfirmaSinDuplicarYconservaPrecio`, `reservaVencidaLiberaDisponibilidadPeroNoPermiteConfirmar`, `editarReservaReemplazaCantidadYquitarLaLibera`, `prohibeAccesoAjenoRolesIncorrectosYcarritosMezclados` y `validaCantidadesClavesYcierraAdministracionAlPublico`.
- `ComercioConcurrencyTests`: `dosCompradoresNoPuedenReservarElMismoInventarioCompleto`.
- `src/test/js/commerce.test.cjs`: pruebas de reserva autenticada, vencimiento y carrito vacío.
- `FlujoPostgresqlTests`: antecedentes de persistencia y transacciones sobre una base PostgreSQL **desechable de CI**.

**Aclaración:** la existencia de una prueba o de un check exitoso anterior no acredita por sí misma una Review, una Retrospective ni la aceptación del Sprint 4 en una fecha posterior.

## 5. Actividades propuestas y tablero Scrumban

| Tarea | Actividad verificable | Evidencia requerida | Estado al planificar |
|---|---|---|---|
| S4-01 | Vincular HU-09 y HU-11 a los archivos, pruebas y PR #56 | Enlaces trazables en GitHub Projects | Pendiente de asociar al sprint |
| S4-02 | Comprobar cambio de cantidad/precio, restricciones y permisos | Pruebas REST y captura de inventario del agricultor | Pendiente de registrar |
| S4-03 | Comprobar creación, edición y eliminación de reservas | Pruebas REST, navegador y estado de carrito | Pendiente de registrar |
| S4-04 | Comprobar vencimiento de 15 minutos y acceso por rol | Resultado de pruebas y escenarios de error | Pendiente de registrar |
| S4-05 | Comprobar concurrencia sin sobrerreserva | Resultado de `ComercioConcurrencyTests` | Pendiente de asociar |
| S4-06 | Verificar CI, Checkstyle, JUnit, JavaScript y JaCoCo de un commit identificado | URL de ejecución verde, reporte de cobertura | Pendiente de asociar |
| S4-07 | Registrar demostración, Sprint Review y Sprint Retrospective reales | Actas, observaciones y acciones de mejora | Pendiente |

Tablero: [GitHub Projects — AgroValle Connect](https://github.com/users/JuanFiligrana/projects/1). Mantener los límites **En curso ≤ 3** y **Pruebas/Revisión ≤ 2**. Las tarjetas se marcarán como terminadas tras verificar sus criterios reales, no simplemente por copiar esta planificación.

## 6. Demostración propuesta del Sprint 4

1. Arrancar la aplicación con Java 17 y PostgreSQL en el entorno de prueba; abrir `http://localhost:8081/`.
2. Ingresar como agricultor, abrir **Mis ofertas** y mostrar cantidad física, cantidad reservada y cantidad disponible.
3. Editar precio y cantidad de una oferta propia; verificar el resultado y probar un cambio inválido por debajo de las reservas activas.
4. Iniciar sesión como comprador, consultar el catálogo, reservar una cantidad y abrir **Carrito**.
5. Modificar una reserva y retirar un ítem, mostrando cómo cambia la disponibilidad.
6. Mostrar mediante pruebas automatizadas el rechazo por falta de inventario, por rol o identidad y por reserva vencida.
7. Mostrar pruebas de concurrencia, el PR #56 de implementación y el check de GitHub Actions correspondiente al commit revisado.
8. Presentar el resultado en el tablero y registrar las conclusiones de la Review y Retrospective cuando realmente se realicen.

**No se realizan pagos reales, seguimiento GPS ni despacho automático.** Las pantallas existentes de órdenes y entregas no amplían el compromiso de 13 puntos del Sprint 4.

## 7. Definition of Done (criterios de terminado)

- [ ] Se verificaron y documentaron los criterios BDD de HU-09 y HU-11, incluidos los errores y roles.
- [ ] La edición de inventario conserva la cantidad reservada y los precios de pedidos históricos.
- [ ] El carrito respeta inventario disponible, vencimiento de 15 minutos y privacidad.
- [ ] Se comprobó el comportamiento ante solicitudes concurrentes de compradores.
- [ ] La interfaz se demostró con datos de prueba reales (no valores inventados en capturas).
- [ ] Checkstyle, JUnit y pruebas del cliente en verde; cobertura JaCoCo superior o igual al 60 % de instrucciones, ramas y líneas; enlaces al resultado.
- [ ] Las tarjetas del tablero enlazan historias, tareas y evidencias, respetando WIP.
- [ ] Sprint Review registrada con fecha, participantes y resultados auténticos.
- [ ] Sprint Retrospective registrada con impedimentos, acuerdos y acciones de mejora reales.
- [ ] Puntos aceptados y velocidad del Sprint 4 registrados **después** de verificar los criterios; no declarar 13/13 de forma anticipada.

## 8. Riesgos y trabajo complementario para el tercer corte

- **Estimación:** las 13 unidades son una propuesta del backlog y requieren ratificación del equipo; no equivalen a horas.
- **Planificación retrospectiva:** el PR #56 demuestra implementación anterior, no una ejecución formal del Sprint 4. Conservar fechas y autoría reales.
- **Concurrencia:** verificar el comportamiento en PostgreSQL con datos desechables de prueba, sin alterar información de producción.
- **Alcance de tercer corte:** Dockerfile, Docker Compose, entorno de staging, integración/despliegue continuo y mejoras de arquitectura hexagonal/JWT fueron anunciados para trabajo posterior. Deben planearse como tareas técnicas identificables si la docente las exige; **no afirmar que ya se realizaron** ni sumarlas automáticamente como historias HU-09/HU-11.
- **Continuidad:** Sprint 5 proyectado aborda HU-12 y HU-15; aunque su código ya exista, sus verificaciones y ceremonias se acreditan por separado.

## 9. Siguiente cierre documental

Al ejecutar las comprobaciones de este plan, elaborar `docs/sprint-4-review.md` y `docs/sprint-4-retrospective.md` con resultados, fecha, personas que realmente participaron, evidencias y decisiones. Actualizar la velocidad del equipo y, si procede, [la proyección general](proyeccion-sprints.md). No inventar reuniones ni aprobaciones.

## 10. Referencias de GitHub

- [Product Backlog](../BACKLOG.md)
- [Proyección de sprints](proyeccion-sprints.md)
- [Sprint 3](sprint-3-planning.md)
- [Compra y logística](compra-logistica.md)
- [PR #56 — Implementación de compra y logística](https://github.com/JuanFiligrana/Agrovalle-Conect/pull/56)
- [Tablero GitHub Projects](https://github.com/users/JuanFiligrana/projects/1)
- [CI vigente](https://github.com/JuanFiligrana/Agrovalle-Conect/actions/workflows/ci.yml)

**Integridad académica:** planificación propuesta ≠ sprint ejecutado. El cierre y la aceptación solo se registrarán con evidencias verdaderas.
