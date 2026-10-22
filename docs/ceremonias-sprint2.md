# Ceremonias del Sprint 2

## Contexto del registro

El incremento fue ejecutado individualmente por Michael David Caicedo Mina. Por ello,
las notas describen seguimiento individual y no atribuyen asistencia, aprobación ni
decisiones a personas que no participaron. La fecha se expresa en `America/Bogota`.

## Daily Scrum · 21 de octubre de 2026

El desarrollador informó que el Sprint 1 corregido ya estaba integrado en `main` mediante
el PR #26. Identificó como siguiente objetivo convertir HU-02 y HU-04 en un incremento
persistente y verificable. Señaló como impedimento que la estación disponible no tenía
JDK 25 ni acceso al repositorio público de Maven, por lo cual la ejecución definitiva se
delegaría al ambiente reproducible de GitHub Actions.

El plan acordado consistió en escribir primero pruebas rojas para publicación, propiedad,
catálogos, filtros y patrones; después implementar la persistencia y abrir un Pull Request.

## Daily Scrum · 22 de octubre de 2026

El desarrollador registró que las pruebas de aceptación y servicio ya estaban definidas,
y que la implementación incluía identidad por JWT, esquema normalizado, Strategy y
Observer. Indicó que la documentación debía alinearse antes de declarar terminado el
incremento. El impedimento local de JDK 25 continuó, de modo que el check remoto seguía
siendo la evidencia de compilación, estilo, pruebas y cobertura.

El siguiente paso consistió en publicar la rama, atender cualquier fallo real de CI y
adjuntar los enlaces del PR, la ejecución y el reporte JaCoCo.

La primera ejecución remota encontró cuatro infracciones de nombres en métodos de prueba.
El desarrollador aplicó una corrección acotada y la ejecución 35 quedó aprobada con cero
infracciones, 55 pruebas exitosas y 97,93 % de cobertura de líneas.

## Sprint Review

La revisión preparada presenta un flujo de extremo a extremo:

1. Se registra un agricultor y se obtiene un JWT.
2. Se crea una finca en Dagua.
3. Se publica una cosecha de Mango en la categoría Frutas.
4. Se consulta la oferta mediante los dos filtros.
5. Se consulta el aviso creado por Observer.
6. Se calcula un flete intermedio mediante Strategy.
7. Se muestra el Pull Request con el check `build` y el artefacto JaCoCo.

El check remoto del PR #52 quedó en verde en la ejecución 35. No se registra aprobación
docente ni de otro integrante sin evidencia real.

## Sprint Retrospective

### Lo que funcionó

El desarrollador separó pruebas e implementación en commits distintos, reutilizó la
seguridad y persistencia del Sprint 1, y mantuvo la identidad fuera del cuerpo controlado
por el cliente. Los puertos existentes permitieron cambiar el repositorio en memoria por
JPA sin modificar los consumidores de precios y contacto.

### Lo que dificultó el flujo

La ausencia de JDK 25 en el ambiente local impidió ejecutar Maven antes de publicar. La
ejecución individual también impide acreditar una revisión por pares auténtica. Esos
límites se registran como riesgos y no se ocultan con evidencias simuladas.

### Acción de mejora

Para el siguiente Sprint, el desarrollador instalará JDK 25 antes de iniciar el código,
mantendrá como máximo tres elementos en curso y dos en revisión, y buscará una persona
autorizada para revisar el PR cuando la asignatura exija aprobación externa.

## Guion de presentación · 10 minutos

| Tiempo | Responsable | Contenido |
|---:|---|---|
| 0–5 min | Desarrollador | Demostración REST en vivo del flujo HU-02/HU-04. |
| 5–7 min | Desarrollador | Reglas de negocio, SOLID, Strategy y Observer. |
| 7–9 min | Desarrollador | Rama, commits TDD, Pull Request, Actions y JaCoCo. |
| 9–10 min | Desarrollador | Preguntas y siguientes historias del backlog. |
