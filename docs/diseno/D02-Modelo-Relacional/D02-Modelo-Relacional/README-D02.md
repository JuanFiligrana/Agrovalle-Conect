# D02 Modelo relacional de AgroValle Connect

## Contenido

Abrir D02-Modelo-Relacional.drawio desde Archivo > Abrir desde > Dispositivo en Draw.io. Las seis pestañas muestran un mismo esquema por áreas, con PK, FK, claves únicas, tipos y nulabilidad. Todas las tablas y conexiones son editables. Las tablas grises se repiten como referencias a otras vistas.

- Vistas 01 a 05: usuarios, producción, comunicación, pedidos y logística.
- Vista 06: asociación técnica de idempotencia y datos calculados.
- D02-normalizacion.md: transformación del MER y justificación de 1FN, 2FN y 3FN.
- D02-diccionario-datos.md: cada columna, su dominio y sus restricciones.
- D02-validacion.md: alcance y resultado de 19 comprobaciones del esquema y las vistas.
- D02-mapeo-JPA.md: correspondencia con entidades y anotaciones.
- D02-esquema-postgresql.sql: DDL estructural de 17 tablas y tres vistas. No modifica el backend en memoria ni ejecuta operaciones de negocio.

## Qué cambia respecto al MER D01

Los rectángulos representan tablas con columnas en vez de entidades con atributos en óvalos. Las relaciones se concretan como llaves foráneas. La tabla técnica idempotencia_pedido separa la clave de reintento por comprador sin repetir ese comprador en pedido; por eso 16 entidades conceptuales originan 17 tablas físicas.

Las restricciones transaccionales se distinguen de las declarativas: un dibujo o un CHECK de una fila no bastan para impedir sobreventa o para confirmar un pedido de forma atómica. La explicación enumera esas operaciones para implementarlas posteriormente.

## Guardar en el repositorio

Subir esta carpeta completa como `D02-Modelo-Relacional` dentro de `docs/diseno`, junto a
`D01-MER-AgroValle`. Enlazar esta carpeta desde la tarjeta D02. Revisar las seis pestañas y
la explicación de normalización antes de marcar la entrega como finalizada.

Los tipos y longitudes son decisiones de diseño propuestas; no se presentan como tamaños impuestos por la profesora.
