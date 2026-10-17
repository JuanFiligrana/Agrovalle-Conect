# D07 · Mockups HU01–HU05 · AgroValle Connect

Diseño de las cinco pantallas del Sprint 1 largo y sus principales estados. Es una propuesta de interfaz alineada con el backlog y con D01–D06; no acredita que los formularios o las integraciones estén implementados.

## Archivos y lectura

El paquete contiene **19 archivos**: un Draw.io editable de ocho páginas, ocho imágenes PNG, ocho imágenes SVG y dos documentos Markdown.

| Hoja | Historia y contenido | Vista |
|---|---|---|
| 01 | HU01 · Registro de agricultores | [PNG](D07-01-HU01-Registro.png) · [SVG](D07-01-HU01-Registro.svg) |
| 02 | HU02 · Publicación de cosechas | [PNG](D07-02-HU02-Publicacion.png) · [SVG](D07-02-HU02-Publicacion.svg) |
| 03 | HU03 · Precios regionales | [PNG](D07-03-HU03-Precios.png) · [SVG](D07-03-HU03-Precios.svg) |
| 04 | HU04 · Búsqueda por municipio y categoría | [PNG](D07-04-HU04-Filtros.png) · [SVG](D07-04-HU04-Filtros.svg) |
| 05 | HU05 · Contacto con el agricultor | [PNG](D07-05-HU05-Contacto.png) · [SVG](D07-05-HU05-Contacto.svg) |
| 06 | HU01/HU02 · Validación, duplicados, permisos y confirmación | [PNG](D07-06-Estados-HU01-HU02.png) · [SVG](D07-06-Estados-HU01-HU02.svg) |
| 07 | HU03/HU04 · Sin datos, sin coincidencias y categoría inválida | [PNG](D07-07-Estados-HU03-HU04.png) · [SVG](D07-07-Estados-HU03-HU04.svg) |
| 08 | HU05 · Confirmación, autenticación, rol, oferta, mensaje y fallo | [PNG](D07-08-Estados-HU05-Contacto.png) · [SVG](D07-08-Estados-HU05-Contacto.svg) |

- [Fuente editable de ocho páginas](D07-Mockups-HU01-HU05.drawio).
- [Explicación, trazabilidad, interacciones y dependencias](D07-explicacion-mockups.md).

En Draw.io / diagrams.net, usar **Archivo → Abrir desde → Dispositivo** y seleccionar el archivo `.drawio`. Las pestañas inferiores permiten cambiar de página. Textos, campos, botones y tarjetas son elementos editables; las pantallas no están pegadas como fotografías.

## Publicación en el repositorio

1. Extraer `D07-Mockups-HU01-HU05.zip`: sus 19 archivos están en la raíz del ZIP para evitar una carpeta duplicada.
2. Cargar la carpeta extraída `D07-Mockups-HU01-HU05` dentro de `docs/Diseño/`, en la rama `chore/sprint-0-completo`.
3. Mensaje de commit sugerido: `docs: agregar D07 mockups HU01 a HU05`.
4. Vincular la carpeta publicada como evidencia en la tarjeta D07. Registrar la fecha real de finalización y moverla a **Hechos** cuando los archivos estén completos y comprobados.

## Alcance

Los nombres, fincas, identificadores, fechas, cantidades y precios son ficticios. El estilo conserva el verde y el fondo claro de la portada existente. Los rótulos D07/HU y las notas exteriores pertenecen al artefacto académico, no a la interfaz final.

Las hojas 01–05 son pantallas de escritorio; las hojas 06–08 reúnen fragmentos alternativos de interfaz. Los diferentes estados de una hoja no se muestran simultáneamente al usuario. Los botones ilustran navegación y acciones previstas; este archivo no ejecuta solicitudes HTTP.

Completar D07 acredita el diseño de estas pantallas. La implementación, las pruebas, la persistencia, la seguridad y el cierre de las historias conservan sus propios criterios de aceptación.
