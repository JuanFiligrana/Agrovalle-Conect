# Políticas Kanban · AgroValle Connect

Fecha de elaboración: 17 de octubre de 2026. Zona horaria de los registros: `America/Bogota`.

Este documento define las reglas de flujo y salida para el tablero del proyecto. Recoge los límites solicitados en la clase de Ingeniería de Software II y complementa el [Definition of Done](dod.md). Su publicación no configura automáticamente límites, campos ni automatizaciones en GitHub Projects.

## 1. Estados y límites de trabajo

Se conserva la denominación existente del tablero. En esta política, “pendientes” y “revisión” identifican la función de la columna equivalente, sin exigir renombrarla.

| Estado | Regla de entrada | Límite y regla de salida |
|---|---|---|
| Backlog | Necesidad o historia registrada y priorizada. | Sale al seleccionarse para un sprint y descomponerse en tareas concretas. |
| Tareas pendientes / To Do | Tarea con alcance, responsable, sprint y criterio de aceptación identificados. | Se toma solamente cuando existe capacidad en En curso. |
| En curso | El responsable inicia trabajo y registra fecha y hora reales. | Máximo **3 tareas simultáneas del equipo**. Sale cuando el trabajo y su evidencia están preparados para comprobarse. |
| Revisión y pruebas | Cambio listo para validación; si modifica código, tiene PR y resultado de verificación. | Máximo **2 tareas en revisión** y **2 PR simultáneos en revisión**. Sale al cumplirse la política de terminado aplicable. |
| Hechos | Trabajo comprobado, con evidencia enlazada y criterio de aceptación cumplido. | Se registra fecha y hora reales de finalización. La entrega queda disponible en el repositorio o entorno definido para la tarea. |

Los límites son compartidos por todo el equipo y por los sprints visibles; no se multiplican por persona. Si GitHub Projects no impone el bloqueo automáticamente, el equipo controla el conteo antes de mover tarjetas. El texto de esta política no acredita que esa configuración ya esté aplicada.

## 2. Trabajo por capacidad y bloqueos

- Cuando una columna llega a su límite, no se incorporan tareas nuevas a ella. Se ayuda a terminar, probar o revisar el trabajo existente.
- Una tarea bloqueada conserva su estado y sigue contando en su límite. Se registra el motivo, la fecha del bloqueo, la dependencia y la acción para resolverlo.
- La revisión diaria identifica tarjetas detenidas y asigna ayuda para liberar capacidad. No basta con comenzar otra tarea.
- Si una revisión requiere cambios, la devolución a En curso respeta su capacidad. Mientras no haya espacio, la tarjeta sigue identificada en revisión como pendiente de corrección; no se oculta en Hechos.

## 3. Política de salida para diseño y documentación

Para terminar una tarea como D01–D07:

1. Los archivos requeridos están publicados en la ruta correspondiente del repositorio.
2. Se comprueba que abren y que su contenido es legible y coherente con las historias y los otros artefactos.
3. La tarjeta enlaza la carpeta, los archivos o el commit que sirven de evidencia.
4. Se registra la fecha real de finalización y se mueve a Hechos.

Una tarea que documenta una funcionalidad acredita ese documento o diseño. El cierre de la historia funcional requiere también su implementación y los criterios BDD correspondientes.

## 4. Política de salida para cambios de código

Además del alcance concreto de la tarea y del [DoD](dod.md):

- El proyecto compila con el JDK 17 LTS configurado en el repositorio y pasa `mvnw.cmd clean verify` en Windows o `./mvnw clean verify` en un entorno compatible.
- Pasa el **100 % de las pruebas ejecutadas**, sin fallos, errores ni omisiones injustificadas. JaCoCo alcanza al menos el **60 % de instrucciones, ramas y líneas** configurado; estas medidas son distintas.
- Checkstyle no presenta infracciones de nivel warning o superior. Se corrigen y documentan las advertencias relevantes de la ejecución.
- Los criterios BDD aplicables están comprobados. Una prueba del prototipo en memoria no demuestra por sí sola persistencia PostgreSQL ni autenticación JWT.
- El PR tiene aprobación real de otro integrante y sus observaciones están resueltas. El autor no aprueba su propio PR.
- Los checks exigidos de integración continua pasan para la revisión que se integrará. Se enlazan el PR, la aprobación y la ejecución correspondiente.
- Se actualizan las explicaciones afectadas y no se incorporan credenciales ni otros secretos al repositorio.

Para una refactorización, se identifican los componentes modificados, la responsabilidad que se separó y las dependencias que ahora se reciben mediante abstracciones. Se conserva evidencia de las pruebas y de la revisión cruzada. El comportamiento corregido o pendiente se declara expresamente.

## 5. Fechas y trazabilidad

Cada tarjeta registra, en sus campos disponibles o en comentarios:

- Fecha de creación o entrada de la necesidad al backlog, cuando sea conocida.
- Fecha y hora reales de inicio al pasar por primera vez a En curso.
- Entradas y salidas relevantes de revisión, bloqueos y reanudaciones.
- Fecha y hora reales de finalización al cumplir la política de Hechos.
- Responsable, sprint, historia relacionada y enlace de evidencia.

Se utiliza un formato consistente, como `AAAA-MM-DD HH:MM`, con hora de Colombia. Cuando solo se conoce el día, se conserva esa precisión; no se inventa una hora. Un dato ausente se identifica como no disponible y no se utiliza para calcular duraciones exactas.

Las tareas terminadas del Sprint 1 conservan su historial. Si aparece una mejora posterior, se añade una tarea de actualización o refactorización vinculada a la original, como indicó la profesora. No se cambian retroactivamente las fechas para aparentar que la mejora formó parte de una entrega anterior.

## 6. Métricas y revisión del flujo

| Métrica | Registro y uso |
|---|---|
| Trabajo en curso, WIP | Conteo real de tarjetas en trabajo o revisión; se compara con los límites de su columna. |
| Cycle time de tarjeta | Tiempo transcurrido desde su primer inicio en En curso hasta su finalización en Hechos; incluye esperas del flujo. |
| Lead time de entrega | Tiempo desde la necesidad registrada hasta la entrega disponible para su destinatario. Si el código debe desplegarse para estar disponible, no se da por entregado solo por mover la tarjeta. |
| Throughput | Número de elementos completados por período. Se informa la unidad, por ejemplo tareas por semana o historias por sprint, sin mezclar ambas. |
| Flujo acumulado, CFD | Se analiza con el historial o las capturas periódicas reales del tablero. Una banda de revisión que crece indica acumulación que debe investigarse. |

El equipo revisa las métricas para encontrar bloqueos y mejorar la entrega. No se rellenan retrospectivamente historiales, firmas, revisiones, resultados de pruebas ni cifras de productividad que no estén respaldados por evidencia.

## 7. Referencias del proyecto

- [Tablero GitHub Projects](https://github.com/users/JuanFiligrana/projects/1).
- [Repositorio AgroValle Connect](https://github.com/JuanFiligrana/Agrovalle-Conect).
- [Definition of Done](dod.md) y [publicación y revisión en GitHub](github.md).
- Clase de la profesora Paola Andrea Bedoya Toro, 8 de octubre de 2026: límites En curso 3 y revisión 2, políticas explícitas de salida, métricas de flujo, refactorización de al menos dos componentes con SRP/DIP y revisión cruzada del PR.
