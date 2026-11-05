# AgroValle Connect — Proyección de sprints del producto

**Asignatura:** Ingeniería de Software II · **Grupo académico:** 6303B  
**Fecha de elaboración de esta proyección:** 5 de noviembre de 2026  
**Fuente:** `BACKLOG.md` y las planificaciones de Sprint 1 y Sprint 2 publicadas en la rama `main`, contrastadas con las indicaciones de clase de la profesora Paola Andrea Bedoya Toro.  
**Naturaleza:** proyección propuesta, sujeta a refinamiento; no es un acta de consenso del equipo ni una declaración de cierre de sprints futuros.

## 1. Estimación global

El Product Backlog vigente contiene **15 historias de usuario (HU-01 a HU-15)**, para **76 Story Points propuestos**. La estimación es relativa y no equivale a horas.

- Capacidad de referencia de 12 puntos por sprint: 76 / 12 = 6,33; se redondea hacia arriba a **7 sprints funcionales**.
- Capacidad de referencia de 14 puntos por sprint: 76 / 14 = 5,43; se redondea hacia arriba a **6 sprints funcionales**, antes de ajustar dependencias y agrupación de historias indivisibles.
- **Propuesta de planificación:** **7 sprints funcionales (1–7)**, con puntos por iteración de entre 5 y 13. Se conservan por separado el **Sprint 0** de preparación y el **Sprint de Diseño** (D01–D07). Esto evita confundir el número de sprints funcionales con el total de etapas del proyecto.

La profesora indicó que la proyección debe partir de la suma de Story Points y una capacidad aproximada de 12–14 puntos, y que puede ajustarse tras cada retrospectiva. También explicó que el tercer corte debe avanzar, como mínimo orientativo, con Sprint 3 y Sprint 4, dependiendo de la velocidad y el periodo de cuatro semanas.

## 2. Distribución de historias propuesta

| Sprint funcional | Historias | Puntos | Objetivo de valor | Estado documental |
|---|---|---:|---|---|
| **1** | HU-01 (5), HU-07 (5), HU-08 (3) | **13** | Registro de agricultores, autenticación JWT y registro de fincas | **Ejecutado y documentado**; conservar compromiso histórico |
| **2** | HU-02 (5), HU-04 (3) | **8** | Publicación persistente y búsqueda por municipio/categoría | **Ejecutado y documentado**; conservar compromiso histórico |
| **3 (propuesto)** | HU-06 (3), HU-05 (5), HU-10 (3) | **11** | Cuenta de comprador, contacto y detalle de cosecha | **Por formalizar** como sprint; hay avances de implementación previos que deben enlazarse, sin alterar su fecha ni atribuirlos retrospectivamente |
| **4 (propuesto)** | HU-09 (5), HU-11 (8) | **13** | Inventario actualizado y carrito con reservas temporales | **Por formalizar**; hay código y pruebas ya integrados mediante PR #56 |
| **5 (propuesto)** | HU-12 (8), HU-15 (5) | **13** | Confirmación y cancelación segura de pedidos | **Proyección**, aunque existen funcionalidades integradas en PR #56 |
| **6 (propuesto)** | HU-13 (8), HU-14 (5) | **13** | Programación de entregas y seguimiento | **Proyección**, aunque existen funcionalidades integradas en PR #56 |
| **7 (propuesto)** | HU-03 (5) | **5** | Precios históricos basados en transacciones completadas | **Proyección**; ya existe implementación en PR #56, pero solo se considera sprint cerrado cuando se registren sus ceremonias y evidencias |
| **TOTAL** | **HU-01 a HU-15 (15 historias sin duplicar)** | **76** | Producto funcional según el backlog vigente | **Siete sprints funcionales proyectados** |

**Justificación del orden:** primero registro/autenticación/fincas; luego ofertas; después acceso y contacto del comprador; reservas e inventario; órdenes; despacho/seguimiento; finalmente estadísticas de precios que requieren transacciones entregadas. Una historia implementada por anticipado no cambia el compromiso histórico de los sprints 1 y 2. Las nuevas asignaciones son un **mapa de trazabilidad propuesto**, no una afirmación de que los sprints 3–7 ya fueron ejecutados formalmente.

## 3. ¿Cómo se relaciona con el trabajo real ya integrado?

El repositorio documenta estos incrementos de código, **distintos de la proyección de sprints**:

- Sprint 1 corregido: HU-01, HU-07 y HU-08; **13 puntos**.
- Sprint 2: HU-02 y HU-04; **8 puntos**.
- Incremento posterior de contacto y detalle: HU-05 y HU-10; **8 puntos**; PR #55.
- Incremento posterior de compra y logística: HU-03, HU-09 y HU-11 a HU-15; **44 puntos**; PR #56.
- HU-06: **3 puntos**, figura en el backlog y tiene evidencia de funcionamiento del registro de comprador, pero no aparece asignada explícitamente en esa tabla histórica de incrementos. Corresponde enlazar su prueba e implementación.

La tabla anterior no debe ser reetiquetada como si correspondiera a sprints ejecutados con fechas retrospectivas. El Sprint 3 y el Sprint 4 requieren planificación y cierre propios para el tercer corte.

## 4. Próximos pasos para el tercer corte

1. **Publicar la proyección** en `docs/proyeccion-sprints.md` en una rama de documentación, y enlazarla desde `README.md` o el índice de gestión del proyecto. Conservar los cálculos (76 / 12 y 76 / 14).
2. **Verificar el cierre documental** del Sprint 1, Sprint 2 y Sprint de Diseño: metas, entregables, evidencia, Sprint Review y Sprint Retrospective. No inventar reuniones o aprobaciones.
3. **Crear/actualizar tarjetas del tablero** para los sprints proyectados, indicando cada HU, Story Points y las dependencias; respetar WIP `En curso ≤ 3` y `Revisión ≤ 2`.
4. **Planificar formalmente Sprint 3** con HU-06, HU-05 y HU-10 (11 puntos) y comprobar qué código y pruebas ya existen. Registrar objetivo, duración (máximo dos semanas), fechas reales, Definition of Done y evidencia de revisión.
5. **Planificar formalmente Sprint 4** con HU-09 y HU-11 (13 puntos); rastrear pruebas de inventario y reservas existentes y realizar el refinamiento que corresponda.
6. **Preparar los contenidos nuevos del tercer corte:** Dockerfile, Docker Compose, entorno de staging, despliegue continuo, revisión de puertos/adaptadores y seguridad JWT. Estas son áreas que la docente anunció para próximas sesiones; no afirmar que ya fueron implementadas sin verificarlas.
7. **Preparar una demostración técnica del software** en navegador y/o Postman, con GitHub Actions y cobertura, y un guion breve para responder preguntas sobre arquitectura y decisiones.
8. **Actualizar velocidad y proyección** al concluir cada sprint, con puntos realmente completados y evidencias. Si se divide una HU de 8 puntos, conservar su trazabilidad y explicar por qué se modificó la proyección.

## 5. Evidencias canónicas

- Product Backlog: https://github.com/JuanFiligrana/Agrovalle-Conect/blob/main/BACKLOG.md
- Sprint 1: https://github.com/JuanFiligrana/Agrovalle-Conect/blob/main/docs/sprint-1-planning.md
- Sprint 2: https://github.com/JuanFiligrana/Agrovalle-Conect/blob/main/docs/sprint-2-planning.md
- Estimaciones pendientes de ratificación real: https://github.com/JuanFiligrana/Agrovalle-Conect/blob/main/docs/estimacion.md
- PR #55: https://github.com/JuanFiligrana/Agrovalle-Conect/pull/55
- PR #56: https://github.com/JuanFiligrana/Agrovalle-Conect/pull/56
- Tablero: https://github.com/users/JuanFiligrana/projects/1
- CI de `main` comprobada: https://github.com/JuanFiligrana/Agrovalle-Conect/actions/runs/41692997653

**Nota de integridad:** la profesora solicita una proyección y recomienda refinarla; la cifra de siete sprints es la opción de planificación propuesta para este backlog de 76 puntos, no una cifra que haya dictado para nuestro equipo. El Sprint 0 y el Sprint de Diseño se reportan por separado para que no se pierdan en el recuento.
