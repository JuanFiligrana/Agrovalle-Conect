# AgroValle Connect — Sprint 3: planificación y verificación

**Asignatura:** Ingeniería de Software II  
**Grupo académico:** 6303B  
**Docente:** Paola Andrea Bedoya Toro  
**Fecha de elaboración de este documento:** 5 de noviembre de 2026  
**Estado:** planificación propuesta para el tercer corte; pendiente de validación y cierre formal.  
**Referencia:** [Proyección general de siete sprints](proyeccion-sprints.md).

> **Criterio de trazabilidad:** este documento NO reetiqueta como trabajo realizado en el Sprint 3 las implementaciones que se integraron anteriormente. Distingue el código existente, su evidencia pública y las actividades de comprobación, revisión y retrospectiva todavía pendientes. La estimación corresponde al backlog publicado y debe ratificarse por el equipo.

## 1. Objetivo del Sprint

Ofrecer y verificar un recorrido comprensible para un comprador: crear una cuenta con rol COMPRADOR, consultar el detalle público de una cosecha activa, enviar un mensaje al agricultor autenticándose con JWT y comprobar que el agricultor pueda consultar dicha solicitud, manteniendo la privacidad y la persistencia de los datos.

**Duración propuesta:** hasta dos semanas.  
**Fecha real de inicio:** por registrar.  
**Fecha real de finalización:** por registrar.  
**Capacidad de referencia:** 12 puntos por sprint; compromiso propuesto de **11 Story Points**.  
**Responsables y acuerdos:** se asignarán según la participación real del equipo y el tablero GitHub Projects; no se atribuyen reuniones, votaciones ni aprobaciones que no estén documentadas.

## 2. Historias del Sprint 3

| Orden | Historia de usuario | Prioridad MoSCoW | Story Points propuestos | Resultado esperado |
|---|---|---|---:|---|
| 1 | HU-06 — Registro de compradores | Must Have | 3 | Crear una cuenta COMPRADOR sin duplicar correos y almacenar contraseña protegida |
| 2 | HU-10 — Consulta del detalle de una cosecha | Must Have | 3 | Mostrar públicamente datos pertinentes de una oferta activa sin exponer información privada |
| 3 | HU-05 — Contacto directo con el agricultor | Must Have | 5 | Guardar un mensaje autenticado y una notificación, accesibles a sus participantes |
| **Total** | **3 historias** | | **11** | **Flujo comprador → detalle → contacto** |

**Dependencias:** las tres historias reutilizan el registro y la autenticación del Sprint 1 y las ofertas publicadas durante el Sprint 2. HU-05 requiere una oferta activa y una cuenta de comprador; HU-10 permite consultar el detalle antes de enviar el mensaje.

## 3. Criterios de aceptación BDD para comprobar

### HU-06 — Registro de compradores

**Escenario: registro correcto**  
**Dado** un correo no registrado y datos válidos de una nueva cuenta,  
**cuando** se realiza `POST /api/v1/auth/register` con rol `COMPRADOR`,  
**entonces** la API responde `201 Created`, guarda la cuenta y protege la contraseña mediante hash.

**Escenario: correo duplicado**  
**Dado** un correo ya registrado,  
**cuando** se intenta crear una segunda cuenta con ese correo,  
**entonces** responde `409 Conflict` y no duplica la cuenta.

### HU-10 — Detalle de una cosecha

**Escenario: consulta pública**  
**Dado** una cosecha activa previamente publicada,  
**cuando** se realiza `GET /api/v1/productos/{id}`,  
**entonces** responde `200 OK` con productor, finca, municipio, categoría, precio, disponibilidad y fecha pertinente, sin datos privados del usuario.

**Escenario: oferta inexistente o inactiva**  
**Dado** una cosecha que no existe o no está activa,  
**cuando** se consulta su identificador,  
**entonces** responde `404 Not Found`.

### HU-05 — Contacto con agricultor

**Escenario: mensaje válido y persistente**  
**Dado** un comprador con JWT válido y una cosecha activa,  
**cuando** envía `POST /api/v1/contacto/mensaje` con la oferta y un mensaje válido,  
**entonces** responde `200 OK`, persiste la interacción y registra la notificación del agricultor en una misma transacción.

**Escenario: seguridad y validación**  
**Dado** una solicitud sin JWT, con rol no permitido o con un mensaje inválido,  
**cuando** se intenta enviar el contacto,  
**entonces** el sistema devuelve el error apropiado (`401`, `403` o `400`) sin crear una interacción inválida.

**Escenario: privacidad de la bandeja**  
**Dado** una interacción registrada entre comprador y agricultor,  
**cuando** cada participante consulta `GET /api/v1/contacto/mensajes`,  
**entonces** solo ve las interacciones en las que participa. El agricultor destinatario puede marcar su aviso como leído mediante `PATCH /api/v1/contacto/notificaciones/{id}/lectura`.

## 4. Trabajo existente y evidencia (sin atribución retroactiva)

| Historia | Estado de la implementación antes de esta planificación | Fuente verificable | Verificación propia del Sprint 3 |
|---|---|---|---|
| HU-06 | La aplicación ya permite crear cuentas COMPRADOR utilizando la ruta de registro | `README.md`, `BACKLOG.md` y aplicación integrada en `main` | Comprobar registro válido, duplicado y acceso por rol; adjuntar captura o prueba |
| HU-10 | Detalle público de cosechas incorporado en el incremento posterior al Sprint 2 | [PR #55](https://github.com/JuanFiligrana/Agrovalle-Conect/pull/55), `docs/contacto-hu05.md` | Verificar respuesta 200/404, datos y pantalla de detalle |
| HU-05 | Contacto, mensajes y notificaciones persistentes incorporados posteriormente al Sprint 2 | [PR #55](https://github.com/JuanFiligrana/Agrovalle-Conect/pull/55), `docs/contacto-hu05.md` | Verificar JWT/roles, persistencia, bandeja, lectura y errores |

El PR #55 fue fusionado antes de elaborar este plan. Las pruebas publicadas en ese cambio son antecedentes técnicos, **no** constituyen por sí solas la ceremonia o aprobación del Sprint 3. La incorporación de HU-06 debe rastrearse con su prueba y commit específico antes de cerrar la historia documentalmente.

**Pruebas existentes de referencia:** `ContactoServiceTests`, `ContactoAcceptanceTests` y `FlujoPostgresqlTests`, descritas en `docs/contacto-hu05.md`. Revisar los resultados reales en GitHub Actions; no convertir la existencia de una clase en evidencia de que se ejecutó en una fecha determinada.

## 5. Actividades del Sprint y tablero

| ID propuesto | Actividad | Evidencia esperada | Estado al elaborar el plan |
|---|---|---|---|
| S3-01 | Vincular HU-06 con su código, prueba y commit real | Referencias en el repositorio | Pendiente de documentar |
| S3-02 | Verificar registro de comprador: éxito y correo duplicado | Captura de pruebas o ejecución reproducible | Pendiente de registrar como Sprint 3 |
| S3-03 | Verificar detalle de cosecha y rechazo de oferta inexistente | Prueba REST y comprobación de interfaz | Pendiente de registrar como Sprint 3 |
| S3-04 | Verificar contacto, autorización, bandeja y persistencia | Pruebas, comprobación PostgreSQL y capturas | Pendiente de registrar como Sprint 3 |
| S3-05 | Revisar Checkstyle, JUnit, JaCoCo y CI del commit usado | Enlace a ejecución verde de GitHub Actions | Pendiente de asociar al Sprint 3 |
| S3-06 | Preparar demostración, Review y Retrospective | Registro de resultados y acciones de mejora | Pendiente; no inventar reuniones |

Ubicar las tareas en [GitHub Projects](https://github.com/users/JuanFiligrana/projects/1), enlazadas a sus historias y evidencias. Conservar límites de trabajo en progreso: **En curso ≤ 3** y **Pruebas/Revisión ≤ 2**. No marcar una tarjeta como terminada hasta comprobar sus criterios.

## 6. Guion de demostración funcional

1. Iniciar la aplicación en el entorno de pruebas con PostgreSQL y Java 17; abrir `http://localhost:8081/`.
2. Registrar un usuario con rol COMPRADOR y comprobar que puede iniciar sesión.
3. Consultar una oferta activa en el catálogo; entrar a «Ver y contactar» y mostrar el detalle.
4. Enviar un mensaje desde la cuenta COMPRADOR; mostrar el identificador y la confirmación de la solicitud.
5. Acceder con el agricultor propietario de la oferta; abrir su bandeja privada y comprobar la recepción del mensaje.
6. Mostrar un caso de error (sin token, rol inválido o mensaje vacío), sin alterar datos existentes.
7. Exhibir el PR de referencia, las pruebas pertinentes, GitHub Actions y la cobertura JaCoCo de la revisión seleccionada.

**Nota:** los mensajes de contacto no generan una orden ni reservan inventario; esas operaciones pertenecen a incrementos posteriores de compra y logística.

## 7. Criterios de terminado (Definition of Done)

- [ ] Criterios BDD de HU-06, HU-10 y HU-05 comprobados y enlazados con resultados reales.
- [ ] Demostración navegable comprador → detalle → contacto → recepción realizada y documentada.
- [ ] Persistencia y control de acceso comprobados sin exponer información sensible.
- [ ] Checkstyle y pruebas automatizadas exitosas; JaCoCo cumple el mínimo vigente del proyecto (60 % para instrucciones, ramas y líneas).
- [ ] Tablero actualizado con vínculos a historias, tareas, pruebas y Pull Requests; WIP respetado.
- [ ] Sprint Review realizado o preparado, distinguiendo qué fue efectivamente demostrado.
- [ ] Sprint Retrospective realizada y documentada con decisiones reales.
- [ ] Resultado y velocidad del Sprint 3 registrados solo cuando exista evidencia suficiente; no afirmar 11/11 puntos completados por anticipado.

## 8. Riesgos, acuerdos y alcance futuro

- **Riesgo de trazabilidad:** ya existe código implementado antes de la planificación formal. No cambiar fechas históricas, autores ni commits para aparentar un sprint ejecutado.
- **Riesgo de evidencia:** el tercer corte exige demostrar el software. Mantener capturas, pruebas y enlaces verificables, sin reemplazar una revisión real por una nota escrita.
- **Riesgo de participación:** las estimaciones y asignaciones del backlog son propuestas hasta que el equipo registre su acuerdo auténtico.
- **Fuera de este Sprint:** carrito, pagos, pedidos, inventario y despacho. Docker, Compose, despliegue, staging y cambios arquitectónicos anunciados para el tercer corte deben planificarse como actividades técnicas identificables, sin presentarlos como parte completada de estas tres historias.

## 9. Próximo cierre documental

Al completar las verificaciones, elaborar `docs/sprint-3-review.md` y `docs/sprint-3-retrospective.md` con fecha, participantes reales, demostración, resultado de las pruebas, puntos aceptados y acciones de mejora. Actualizar entonces el tablero y la proyección general si la velocidad observada lo justifica.

## 10. Referencias del repositorio

- [Product Backlog](../BACKLOG.md)
- [Proyección general de sprints](proyeccion-sprints.md)
- [Sprint 1 corregido](sprint-1-planning.md)
- [Sprint 2](sprint-2-planning.md)
- [Contacto HU-05 y detalle HU-10](contacto-hu05.md)
- [Pull Request #55: contacto y detalle](https://github.com/JuanFiligrana/Agrovalle-Conect/pull/55)
- [Pull Request #56: compra y logística](https://github.com/JuanFiligrana/Agrovalle-Conect/pull/56)
- [Tablero Scrumban](https://github.com/users/JuanFiligrana/projects/1)

**Integridad académica:** la planificación no equivale a la ejecución, aprobación docente ni validación formal de un sprint. Estos resultados se documentarán únicamente cuando realmente ocurran.
