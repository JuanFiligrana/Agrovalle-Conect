# Sprint Retrospective · Sprint 2

## Continuar

El desarrollador continuará escribiendo criterios antes de la implementación, separando
responsabilidades mediante puertos y conservando la identidad fuera de los DTO enviados
por el cliente. También mantendrá los catálogos centralizados y las pruebas REST sobre el
flujo completo.

## Detener

El desarrollador evitará comenzar un incremento sin comprobar primero la disponibilidad
del JDK requerido. También evitará tratar prototipos heredados como historias terminadas
o registrar revisión, firmas y métricas que no tengan evidencia.

## Empezar

El desarrollador preparará el ambiente JDK 25 al inicio del siguiente Sprint, enlazará
cada tarjeta con su PR o documento y buscará una revisión externa cuando la asignatura
la exija. Mantendrá un máximo de tres elementos en curso y dos en revisión.

## Resultado de la retrospectiva

La principal mejora técnica fue sustituir el repositorio temporal de cosechas por JPA sin
cambiar el contrato consumido por otros servicios. La principal deuda restante es llevar
los avisos y contactos a persistencia y completar las historias de pedidos del backlog.
La primera ejecución de CI también confirmó el valor de Checkstyle como retroalimentación
temprana: cuatro nombres fueron corregidos sin mezclar cambios funcionales. La segunda
ejecución aprobó 55 pruebas y 97,93 % de cobertura de líneas.
