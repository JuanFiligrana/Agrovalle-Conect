# Definition of Done (DoD) - AgroValle Connect

Este documento representa el contrato innegociable del equipo para garantizar la calidad e integridad de cada incremento funcional de AgroValle Connect, cumpliendo con la norma ISO/IEC 25010.

## Checklist de Cumplimiento Obligatorio

- [ ] **Build Local:** El proyecto compila o transpila sin errores en el entorno local (Java 17 / Spring Boot).
- [ ] **Linter Pass (Modularity/Style):** El código cumple con las reglas estáticas. Cero advertencias en Checkstyle (usando checkstyle.xml basado en Google Java Style).
- [ ] **Functional Correctness:** El 100% de las pruebas unitarias e integradas existentes (JUnit 5) pasan con éxito (mínimo 60% de cobertura verificada con JaCoCo).
- [ ] **Peer Review:** Todo Pull Request ha sido revisado y aprobado por al menos un par (compañero de equipo) antes de ser fusionado.
- [ ] **Documentation:** El `README.md` y la documentación técnica de la carpeta `/docs` están actualizados.
- [ ] **Commits:** El historial de Git sigue estrictamente la convención de Conventional Commits (feat, fix, docs, etc.).
- [ ] **Automatización:** Hooks de Husky activos que impiden el commit si falla el linter o los tests automáticos.

## Firmas de Compromiso del Equipo
Al firmar este documento, los desarrolladores se comprometen a no integrar código a la rama principal (`main` o `develop`) que no cumpla con TODOS los puntos anteriores.

*   **[Juan Eduardo Filigrana Mindinero]** - 
*   **[Michael David Caicedo Mina]** - 
*   **[Juan Camilo Alvarez]** - 
*   **[Jhojan Aragon]** - 