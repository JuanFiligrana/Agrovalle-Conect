# Verificación de la corrección del arranque

Fecha: 24 de septiembre de 2026. Entorno de revisión: Linux, Temurin 25.0.4.1,
Maven 3.9.9. Se ejecutó el ciclo `clean verify` con las reglas originales activas.
Los scripts de Windows se incluyen para ejecución local; esta revisión no ejecutó Windows.

## Resultados observados

- Compilación de 29 archivos Java de producción: BUILD SUCCESS.
- Checkstyle: 0 infracciones.
- JUnit: 5 pruebas, 0 fallos, 0 errores y 0 omitidas.
- JaCoCo: 157 de 175 líneas cubiertas
  (89.71 %), por encima del mínimo configurado del 60 %.
- JAR generado: arranque correcto con Java 25.
- GET `/`: HTTP 200, portada HTML de AgroValle Connect.
- GET `/api/v1/estado`: HTTP 200, estado OK y etapa Sprint 1.

Se conserva la salida real de Maven en `evidencias/verificacion-local.log` y
el resumen de resultados en `evidencias/resumen.json`.

## Cambios que resuelven el fallo comunicado

Se añadieron 28 comentarios Javadoc a constructores y métodos públicos, se corrigió
el orden y la separación de importaciones en CosechaRequest y se renombraron dos
pruebas. Esos cambios resuelven las 36 infracciones de Checkstyle del registro recibido.
No se desactivaron Checkstyle, las pruebas, JaCoCo ni la comprobación de Java 25.

## Alcance real y trabajo pendiente

Esta corrección permite compilar y arrancar el prototipo ya entregado. Las pruebas
existentes verifican la portada, el estado del servicio y algunos flujos de la API.
El porcentaje de cobertura mide ese código, no el cumplimiento de todas las historias.

- Los repositorios guardan datos en memoria; se pierden al reiniciar. Falta PostgreSQL.
- El contacto comprueba que exista una cabecera Authorization; no valida un JWT.
  Faltan inicio de sesión, identidad autenticada y autorización real.
- El promedio usa precios de ofertas activas; HU-03 solicita transacciones completadas
  de las últimas 24 horas. Ese criterio continúa pendiente.
- Faltan comprobaciones contra los catálogos de municipios y categorías.
- La página inicial es informativa; no incorpora formularios para las operaciones.
- La aprobación de un compañero, los checks remotos y las evidencias del tablero
  deben registrarse en GitHub. No se consideran completados por esta prueba local.

## Repetir la verificación en Windows

Dentro de la carpeta que contiene pom.xml, ejecutar `verificar.cmd`.
Para iniciar, ejecutar `iniciar.cmd` y abrir http://localhost:8081/.
