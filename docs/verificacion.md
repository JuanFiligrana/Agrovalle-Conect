# Verificación técnica de la base del Sprint 0

## Resultados reproducibles

La base se verificó con JDK 25.0.2 y Maven Wrapper 3.9.9:

- `clean verify`: BUILD SUCCESS.
- Checkstyle 10.21.4 con reglas de Google Java Style: cero infracciones.
- JUnit 5: tres pruebas, cero fallos y cero errores.
- JaCoCo: supera el umbral mínimo del 60 % para el código de la base.
- El JAR inicia y responde `GET /` con la portada y `GET /api/v1/estado` con un JSON cuyo estado es `OK`.
- `npm ci` instala Husky 9.1.7 y configura el hook local.
- El hook rechaza una importación comodín intencional y una prueba fallida; acepta el código restaurado.
- `BACKLOG.md` contiene 15 historias y 40 escenarios Given-When-Then.

## Alcance de la verificación

La comprobación cubre la infraestructura del Sprint 0. La cobertura no representa las
funcionalidades de negocio porque todavía están especificadas en el backlog y serán
desarrolladas en incrementos posteriores. La base no conecta PostgreSQL, no emite JWT
y no implementa registro, catálogo, pedidos ni logística.

La ejecución de Windows se deja preparada con `configurar-java.cmd`, `iniciar.cmd` y
`verificar.cmd`. El equipo debe ejecutarlos en su computador con JDK 25 y confirmar el
resultado antes del commit. Los avisos de Java o Maven no sustituyen un `BUILD SUCCESS`.

## Evidencias que debe completar el equipo

- URL pública del repositorio después de integrar la rama.
- Historial de commits semánticos.
- Pull Request con comentarios técnicos y aprobación de otro integrante.
- Check build exitoso en GitHub Actions.
- Sesión de Planning Poker con fecha, participantes y acuerdos finales.
- Aceptación firmada o aprobada del Definition of Done.
