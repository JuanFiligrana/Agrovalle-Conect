# Índice de diseño D01–D07 y correspondencia con Sprint 2

| Artefacto | Contenido | Relación con HU-02/HU-04 |
|---|---|---|
| D01 | Modelo entidad–relación | Finca, producto, categoría y cosecha |
| D02 | Modelo relacional en 3FN | Claves foráneas y tablas normalizadas |
| D03 | Arquitectura MVC/cuatro capas | Controlador, servicio, puerto y adaptador JPA |
| D04 | Despliegue | Aplicación Java 25 conectada a PostgreSQL |
| D05 | Secuencias | Publicación y consulta con filtros |
| D06 | Componentes | API, seguridad, negocio, persistencia y base de datos |
| D07 | Mockups | Formularios, resultados y estados de error de HU-02/HU-04 |

Cada carpeta contiene fuentes editables Draw.io cuando corresponden, exportaciones SVG
y PNG, y una explicación. Los archivos conservan el diseño objetivo completo del producto;
el Sprint 2 implementa solamente el subconjunto HU-02/HU-04.

## Estado de materialización

- El esquema ejecutable incorpora `categorias`, `productos` y `cosechas` vinculadas con
  `fincas`, de acuerdo con D01/D02.
- `ProductoController → CosechaService → CosechaPort → CosechaRepository` materializa
  la dirección de dependencias de D03/D06.
- Spring Security entrega la identidad JWT para publicar y deja pública la consulta.
- Los flujos de publicación y filtros se comprueban mediante MockMvc.
- D04 continúa siendo una propuesta de despliegue: este incremento no publica un servidor.
- Los formularios completos de D07 no se implementan; la demostración se realiza por API.

Las explicaciones históricas que indiquen “pendiente” describen el estado del prototipo
en el commit citado por cada artefacto. Este índice registra el avance posterior sin
reescribir esa evidencia de diseño.
