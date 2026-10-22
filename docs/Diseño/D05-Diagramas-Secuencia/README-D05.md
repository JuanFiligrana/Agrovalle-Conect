# D05 · Diagramas de secuencia

AgroValle Connect · Sprint de diseño

## Abrir

En Draw.io: **Archivo → Abrir desde → Dispositivo** y seleccionar `D05-Diagramas-Secuencia.drawio`.

El archivo contiene seis pestañas de un mismo entregable:

1. HU01: registro de agricultores.
2. HU02: publicación de cosechas.
3. HU03: consulta de precios regionales.
4. HU04: filtros por municipio y categoría.
5. HU05: contacto y notificación interna.
6. Fallo transaccional: interacción de rollback y respuesta de error, reutilizada por las historias que escriben datos.

Se incluyen las seis hojas en PNG y SVG. Los textos, flechas, fragmentos y barras de activación del `.drawio` son editables. Las hojas largas se consultan con zoom y desplazamiento vertical; los SVG conservan la calidad al ampliar.

## Cómo leer

El tiempo avanza hacia abajo. Las flechas continuas son llamadas y las discontinuas son retornos. Los rectángulos verticales estrechos representan activaciones. `alt` presenta alternativas, `opt` un bloque opcional, `break` interrumpe la interacción cuando se cumple su condición y `ref` invoca la interacción de la hoja 06.

Por ejemplo, el mensaje `5.12` identifica el mensaje 12 de la hoja de HU05. La numeración identifica mensajes; no representa duración ni exige ejecutar todos los caminos alternativos.

**Son secuencias del diseño objetivo.** JWT, PostgreSQL, los adaptadores y las transacciones representadas requieren implementación. Los diagramas no acreditan el cierre funcional de las HU01–05.

## Subir a GitHub

Ubicación: `docs/Diseño/D05-Diagramas-Secuencia`.

Al descomprimir, entrar hasta ver directamente el archivo `.drawio`, las imágenes y los documentos. Subir esa carpeta una sola vez, enlazarla desde D05 y registrar las fechas reales de trabajo. Marcar «Hechos» cuando el artefacto se haya revisado y enlazado.
