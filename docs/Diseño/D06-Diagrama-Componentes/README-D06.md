# D06 · Diagrama de componentes

AgroValle Connect · Sprint de diseño

## Abrir

En Draw.io: **Archivo → Abrir desde → Dispositivo** y seleccionar `D06-Diagrama-Componentes.drawio`.

El archivo tiene dos pestañas:

1. **Vista general:** interfaz web, API, seguridad, casos de uso, persistencia JPA y PostgreSQL, con sus interfaces y dependencias.
2. **Contratos de datos:** siete interfaces del negocio, sus servicios consumidores y los adaptadores que las implementan.

También se entregan las dos hojas en PNG y SVG y un documento con la explicación y la correspondencia con el código.

## Notación

- `«component»`: unidad funcional del software; se reconoce también por el icono de componente.
- `«interface»`: contrato que un componente puede usar o implementar.
- Flecha discontinua abierta: dependencia de uso, desde el consumidor hacia la interfaz que requiere.
- Flecha discontinua con triángulo vacío: realización, desde el proveedor hacia la interfaz que implementa.
- `«package»`: agrupación de elementos relacionados.

Son componentes lógicos de **una aplicación backend Spring Boot**, además de su interfaz cliente y dependencia externa PostgreSQL. La integración JWT/JPA/PostgreSQL y los nuevos contratos siguen por implementar.

## Guardar en el repositorio

Ubicación: `docs/Diseño/D06-Diagrama-Componentes`.

Descomprimir el ZIP en una carpeta con ese nombre, subir la carpeta con sus siete archivos y enlazarla desde D06. Mantener «En curso» mientras se revisa y sube; marcar «Hechos» cuando el artefacto esté revisado y enlazado, registrando las fechas reales.
