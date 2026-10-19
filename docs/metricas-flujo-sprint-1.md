# Métricas de flujo — Sprint 1

**Proyecto:** AgroValle Connect  
**Fecha de corte:** 18 de octubre de 2026  
**Zona horaria:** Colombia (UTC-5)  
**Fuente:** GitHub Projects — gráficos *Burn up* y *Status chart*

## 1. Resumen ejecutivo

Al momento del corte, el tablero contiene **20 tareas**. De ellas, **8 están terminadas** y **12 continúan abiertas**, por lo que el avance general es del **40 %** y el trabajo pendiente representa el **60 %**.

Las ocho tareas terminadas corresponden a los siete artefactos de diseño **D01–D07** y a la refactorización **R01**. El mayor avance se produjo entre el 15 y el 17 de octubre. Sin embargo, actualmente no hay tareas en las columnas **En curso** ni **Testing / Code Review**, por lo que el flujo está detenido a pesar de que quedan doce tareas abiertas.

## 2. Estado actual del tablero

| Estado | Cantidad | Porcentaje del total |
|---|---:|---:|
| Pendientes del producto | 5 | 25 % |
| Sprint Backlog (To Do) | 7 | 35 % |
| En curso | 0 | 0 % |
| Testing / Code Review | 0 | 0 % |
| Hechas | 8 | 40 % |
| **Total** | **20** | **100 %** |

Los límites de trabajo en proceso definidos son **máximo 3 tareas en En curso** y **máximo 2 tareas en Testing / Code Review**. En el corte actual ambos valores están en cero, así que no existe sobrecarga en esas columnas; la acumulación está antes de iniciar el desarrollo.

## 3. Evolución observada en el gráfico Burn up

| Fecha | Abiertas | Terminadas | Total registrado | Avance |
|---|---:|---:|---:|---:|
| 4–14 de octubre | 11 | 0 | 11 | 0 % |
| 15 de octubre | 18 | 1 | 19 | 5,26 % |
| 16 de octubre | 16 | 3 | 19 | 15,79 % |
| 17 de octubre | 12 | 8 | 20 | 40 % |
| 18 de octubre | 12 | 8 | 20 | 40 % |

El alcance aumentó durante el periodo analizado: pasó de 11 a 19 tareas el 15 de octubre y llegó a 20 tareas el 17 de octubre. Por esta razón, el crecimiento de la línea de trabajo abierto no significa únicamente retraso; también refleja la incorporación de nuevas tareas de diseño y refactorización.

## 4. Métricas de flujo

- **Trabajo total registrado:** 20 tareas.
- **Throughput acumulado:** 8 tareas terminadas.
- **Trabajo pendiente:** 12 tareas.
- **Porcentaje completado:** 40 %.
- **Trabajo en proceso actual (WIP):** 0 tareas.
- **Trabajo en revisión actual:** 0 tareas.
- **Mayor avance diario observado:** 5 tareas terminadas el 17 de octubre, al pasar de 3 a 8 completadas.
- **Ritmo durante la ventana activa del 15 al 17 de octubre:** 8 tareas terminadas en 3 días, equivalente a aproximadamente 2,67 tareas por día. Este valor describe únicamente esa ventana y no debe tomarse todavía como velocidad estable del equipo.

## 5. Tiempo de ciclo disponible

La única tarea con hora de inicio y finalización registrada de forma completa es **R01**:

- Inicio: 17/10/2026 a las 02:26.
- Integración: 17/10/2026 a las 03:26:30.
- Tiempo transcurrido: **1 hora y 30 segundos**.

Este dato sirve como evidencia individual, pero no permite calcular un promedio confiable del Sprint. Para obtener tiempos de ciclo y tiempos de entrega reales, cada tarjeta debe registrar fecha y hora al entrar en **En curso** y al pasar a **Hechas**.

## 6. Acumulaciones y posibles cuellos de botella

La principal acumulación se encuentra antes del desarrollo: hay **7 tareas en Sprint Backlog** y **5 en el Product Backlog**, mientras que las columnas intermedias permanecen vacías. Por tanto, el problema actual no es superar los límites de WIP, sino que no se ha iniciado el siguiente trabajo funcional.

Si el Sprint continúa, se debe seleccionar la siguiente tarea de mayor prioridad y moverla a **En curso**, sin exceder el límite de tres. Conviene mantener el flujo continuo: terminar y verificar una tarea antes de iniciar varias al mismo tiempo.

## 7. Conclusión

El Sprint presenta un avance verificable del **40 %**. Se completó el bloque de diseño y la refactorización R01 con validación automática, pero todavía queda el **60 %** del trabajo registrado. La prioridad siguiente debe ser reactivar el flujo con las tareas funcionales pendientes, conservando los límites de WIP y registrando las fechas de cada transición para que las próximas métricas de tiempo sean más precisas.

