# Bitácora Daily Scrum · Sprint 2

> **Nota de vigencia:** las menciones a Java 25 registran el entorno histórico del PR #52.
> El proyecto y su workflow vigentes se ejecutan con Java 17.

Zona horaria: `America/Bogota`.

## 21 de octubre de 2026

### Trabajo terminado

El desarrollador verificó que el Sprint 1 corregido estaba integrado en `main` mediante
el PR #26 y que HU-01, HU-07 y HU-08 podían reutilizarse como precondiciones.

### Trabajo siguiente

El desarrollador seleccionó HU-02 y HU-04, y preparó pruebas rojas para publicación,
propiedad de finca, catálogos, filtros, Strategy y Observer.

### Impedimentos

El ambiente de trabajo disponible no tenía JDK 25 ni acceso directo a Maven Central. Se
definió GitHub Actions como evidencia definitiva y reproducible de `verify`.

## 22 de octubre de 2026

### Trabajo terminado

El desarrollador implementó persistencia normalizada de cosechas, identidad por JWT,
filtros validados, cálculo de flete por Strategy y avisos desacoplados por Observer.

### Trabajo siguiente

El desarrollador alineó README, backlog, requisitos 29148, SOLID, demostración Postman y
ceremonias. El siguiente paso consistió en publicar la rama y corregir resultados reales
del check `build` hasta alcanzar verde.

### Impedimentos

Continuó el impedimento local de JDK 25. También se registró que la ejecución individual
no permite acreditar una revisión por pares externa.

### Resultado del cierre técnico

La primera ejecución remota identificó cuatro nombres de prueba incompatibles con la
regla `AbbreviationAsWordInName`. El desarrollador los corrigió en un commit específico.
La ejecución 35 aprobó después Java 25, Checkstyle, 55 pruebas y el umbral JaCoCo con
97,93 % de cobertura de líneas.
