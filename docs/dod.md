# Definition of Done

Contrato de calidad propuesto para aceptación del equipo. La lista define condiciones;
las casillas se evalúan por cada incremento y no declaran que las historias estén terminadas.

- [ ] **Compilación:** El proyecto compila con JDK 25 y Maven Wrapper sin errores.
- [ ] **Estilo:** Checkstyle basado en Google Java Style no reporta infracciones de nivel warning o superior.
- [ ] **Pruebas:** Pasa el 100 % de las pruebas existentes de JUnit 5; verify exige al menos 60 % de cobertura de líneas con JaCoCo.
- [ ] **Revisión por pares:** Cada Pull Request tiene revisión y aprobación explícita de otro integrante antes del merge.
- [ ] **Documentación:** README, BACKLOG y docs reflejan el comportamiento y las decisiones vigentes.
- [ ] **Commits:** Los commits nuevos siguen Conventional Commits y describen cambios concretos.
- [ ] **Automatización:** Husky está instalado en cada equipo y bloquea el commit si fallan pruebas o estilo.
- [ ] **Criterios BDD:** Los escenarios aplicables de la historia se verificaron, incluidos los errores relevantes.
- [ ] **Seguridad del repositorio:** No se versionan contraseñas, tokens, archivos .env, node_modules, target ni ajustes personales del IDE.
- [ ] **Integración:** Los checks exigidos en GitHub están aprobados y la rama principal permanece ejecutable.

## Relación con calidad
La adecuación funcional se verifica con criterios BDD y pruebas; la mantenibilidad
con estilo, modularidad, revisión y documentación. La protección de datos y permisos
se verificará al implementar las funciones protegidas. Esta relación toma como guía
ISO/IEC 25010; no afirma una certificación de cumplimiento.

## Aceptación formal
Cada integrante declara que acepta este contrato y se compromete a aplicarlo.
Registrar una firma real y fecha o un enlace a su aprobación explícita en GitHub.
No se considera firmado por el simple hecho de listar los nombres.

| Integrante | Firma o enlace de aceptación | Fecha |
|---|---|---|
| Juan Eduardo Filigrana Mindinero | Pendiente de aceptación | Pendiente |
| Michael David Caicedo Mina | Pendiente de aceptación | Pendiente |
| Juan Camilo Álvarez | Pendiente de aceptación | Pendiente |
| Jhojan Aragón | Pendiente de aceptación | Pendiente |
