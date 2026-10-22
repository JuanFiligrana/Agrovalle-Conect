# Índice de diseño D01–D07 y correspondencia con Sprint 2

Esta es la ruta canónica `docs/diseno/` solicitada para los artefactos del Sprint de
Diseño. Además de D01–D07, contiene el DDL ejecutable, la síntesis de arquitectura y la
documentación de los patrones comportamentales.

- [DDL PostgreSQL ejecutable](schema.sql)
- [Arquitectura MVC en cuatro capas y patrones](arquitectura-mvc-gof.md)
- [Patrones Strategy y Observer con diagramas de clases](patrones.md)

| Artefacto | Contenido | Relación con HU-02/HU-04 |
|---|---|---|
| D01 | Modelo entidad–relación | Finca, producto, categoría y cosecha |
| D02 | Modelo relacional en 3FN | Claves foráneas y tablas normalizadas |
| D03 | Arquitectura MVC/cuatro capas | Controlador, servicio, puerto y adaptador JPA |
| D04 | Despliegue | Aplicación Java 17 LTS conectada a PostgreSQL |
| D05 | Secuencias | Publicación y consulta con filtros |
| D06 | Componentes | API, seguridad, negocio, persistencia y base de datos |
| D07 | Mockups y prototipo web responsive | Flujos HU01–HU05, portada pública y vistas por rol |

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
- D07 incorpora un prototipo web navegable de alta fidelidad. Sus módulos de pedidos,
  entregas, reservas y reportes expresan el diseño objetivo; el backend terminado del
  Sprint 2 continúa limitado a las rutas declaradas en la planificación.

## Prototipo navegable

La [documentación del prototipo responsive](D07-Mockups-HU01-HU05/Prototipo-Web-Responsive/README.md)
explica la separación entre portal público, panel del agricultor y panel administrativo,
además de las decisiones de legibilidad, unidades, filtros, mensajes y seguimiento.

Las explicaciones históricas que indiquen “pendiente” describen el estado del prototipo
en el commit citado por cada artefacto. Este índice registra el avance posterior sin
reescribir esa evidencia de diseño.
