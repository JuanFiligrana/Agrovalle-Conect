# D03 · Arquitectura MVC de cuatro capas

AgroValle Connect · Sprint de diseño

## Abrir el diagrama

En Draw.io: **Archivo → Abrir desde → Dispositivo** y seleccionar `D03-Arquitectura-MVC.drawio`.

El archivo contiene dos pestañas de un mismo diseño:

1. **Cuatro capas:** presentación/API REST, lógica de negocio, acceso a datos y persistencia. La vista y el controlador se muestran separados dentro de presentación.
2. **Dependencias y SOLID:** detalle parcial de los contratos y adaptadores propuestos para publicación de ofertas y consulta de precios.

Las cajas, textos y conexiones son editables. También se incluyen ambas hojas en PNG y SVG para consultarlas sin Draw.io.

## Qué explica cada documento

- `D03-explicacion-arquitectura.md`: responsabilidades por capa, correspondencia MVC, patrones y reglas de comunicación.
- `D03-correspondencia-HU-y-codigo.md`: relación con HU01–05, clases existentes y cambios pendientes.

## Estado del trabajo

**Este es un artefacto de diseño.** El prototipo del repositorio ya tiene controladores, servicios y repositorios en memoria. La integración JPA/PostgreSQL, los nuevos contratos y la refactorización dibujada quedan por implementar. Los diagramas no acreditan el cierre funcional de las historias.

## Subir a GitHub

La carpeta debe quedar en `docs/Diseño/D03-Arquitectura-MVC`, junto a D01 y D02.

Al descomprimir, entrar hasta ver directamente el archivo `.drawio`, las imágenes y estos documentos. Arrastrar esa carpeta una sola vez para evitar duplicar su nombre como subcarpeta.

Enlazar la carpeta subida desde la tarjeta D03. Registrar las fechas reales de trabajo en el tablero; marcar «Hechos» cuando el artefacto esté revisado y enlazado.
