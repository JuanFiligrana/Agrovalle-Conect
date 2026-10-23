# D04 · Diagrama de despliegue UML

AgroValle Connect · Sprint de diseño

## Abrir

En Draw.io: **Archivo → Abrir desde → Dispositivo** y seleccionar `D04-Diagrama-Despliegue.drawio`.

El archivo contiene dos pestañas:

1. **Despliegue propuesto:** tres nodos —cliente, servidor de aplicación y servidor PostgreSQL— con sus entornos de ejecución, artefactos y conexiones.
2. **Entorno local:** navegador, aplicación Java 17 y PostgreSQL en el PC de desarrollo;
   se diferencia el esquema incremental vigente del modelo objetivo D02.

Las dos hojas están disponibles en PNG y SVG. El archivo `.drawio` contiene cajas, textos y conexiones editables.

## Qué representa

Se indican los puertos de destino: **443** para HTTPS en el despliegue propuesto, **8081** para Spring Boot y **5432** para PostgreSQL. La explicación detalla el alcance de cada conexión y las diferencias entre diseño y código actual.

**D04 documenta el despliegue; no instala ni publica infraestructura remota.** Linux,
NGINX, los certificados y el host de base de datos son decisiones propuestas. La aplicación
local sí configura PostgreSQL y Spring Data JPA para usuarios, fincas y cosechas; contacto
continúa temporalmente en memoria.

## Subir al repositorio

Ubicación: `docs/diseno/D04-Diagrama-Despliegue`, junto a D01, D02 y D03.

Al descomprimir, entrar hasta encontrar directamente el `.drawio`, las imágenes y estos documentos. Subir esa carpeta una sola vez y enlazarla desde la tarjeta D04.

Mantener D04 en «En curso» mientras se revisa y sube. Marcar «Hechos» al terminar y añadir la evidencia; registrar las fechas reales del trabajo.
