# Sprint 2 · Publicación y búsqueda de cosechas

## Objetivo

Entregar un incremento funcional que permita a un agricultor autenticado publicar una
cosecha de una finca propia y a cualquier comprador consultar ofertas activas mediante
filtros válidos de municipio y categoría. El compromiso suma 8 Story Points.

## Alcance comprometido

| Orden | Historia | Puntos | Resultado esperado |
|---|---|---:|---|
| 1 | HU-02 · Publicación de cosechas | 5 | Oferta persistida y vinculada al dueño del JWT |
| 2 | HU-04 · Filtro por categoría y municipio | 3 | Consulta pública combinable sobre ofertas activas |

El incremento reutiliza HU-01, HU-07 y HU-08 del Sprint 1. HU-03 y HU-05 se conservan
como funciones heredadas del prototipo y no se presentan como terminadas en este Sprint.

## Flujo demostrable

1. El agricultor se registra e inicia sesión.
2. Registra una finca indicando un municipio del Valle del Cauca.
3. Envía el JWT y los datos de la cosecha a `POST /api/v1/productos`.
4. El servicio toma el agricultor del JWT, valida rol, finca, propiedad, categoría y fecha.
5. JPA persiste categoría, producto y cosecha en el esquema relacional.
6. El servicio publica `CosechaPublicadaEvent` después de guardar la oferta.
7. El observador crea un aviso interno después de confirmar la transacción.
8. La consulta pública filtra ofertas activas por municipio, categoría o ambos criterios.

El cuerpo de publicación no acepta `agricultorId`. Esa identidad siempre procede del
JWT verificado para impedir que el cliente publique en nombre de otra cuenta.

## Contratos REST

### Publicar una cosecha

`POST /api/v1/productos` requiere `Authorization: Bearer <jwt>` y un cuerpo como:

```json
{
  "fincaId": 9,
  "tipo": "Mango",
  "categoria": "Frutas",
  "cantidadKg": 250.000,
  "precioCopKg": 3500.00,
  "fechaCosecha": "2026-11-05"
}
```

También se aceptan los alias `finca_id`, `cantidad_kg`, `precio_cop_kg` y
`fecha_cosecha`. Una creación válida responde `201 Created`. Los errores relevantes
son `400` por datos, categoría o fecha; `401` por ausencia o invalidez del JWT; `403`
por rol o finca ajena; y `404` por finca inexistente.

### Consultar ofertas activas

`GET /api/v1/productos?municipio=Dagua&categoria=Frutas` es público. Ambos parámetros
son opcionales y se combinan cuando están presentes. Los nombres se normalizan sin
depender de mayúsculas ni tildes; un valor ajeno al catálogo responde `400`.

### Consultar el aviso Observer

`GET /api/v1/notificaciones/cosechas` requiere JWT y devuelve solamente los avisos del
usuario autenticado. El canal actual conserva los avisos en memoria para demostrar el
patrón; su persistencia multicanal queda fuera del alcance de HU-02 y HU-04.

### Calcular flete con Strategy

`GET /api/v1/fletes/calculo?municipio=Dagua&subtotal=100000` selecciona una estrategia
por zona y devuelve municipio canónico, estrategia aplicada y valor en COP. Esta regla
es una demostración de patrón solicitada para el corte; no crea pedidos ni cobra dinero.

## Persistencia

El esquema de ejecución contiene `usuarios`, `fincas`, `categorias`, `productos` y
`cosechas`. La cosecha referencia finca y producto; el agricultor y municipio se derivan
de la finca, mientras la categoría se deriva del producto. La estructura evita repetir
esos datos en la tabla de ofertas y coincide con el modelo normalizado de D02.

PostgreSQL se usa en ejecución normal. Las pruebas de integración usan H2 en modo
PostgreSQL con el mismo `schema.sql` y `ddl-auto=validate` para detectar diferencias de
mapeo antes de integrar.

## TDD y trazabilidad

El commit `test: definir criterios Sprint 2 en rojo` introduce primero pruebas que
referencian las capacidades todavía ausentes. El commit funcional implementa esas reglas.
La evidencia definitiva de verde corresponde al check remoto `build` del Pull Request.

| Criterio | Prueba principal |
|---|---|
| Identidad obtenida del JWT | `CosechaServiceTests.publicaUnaOfertaActivaUsandoLaIdentidadDelJwt` |
| Rol agricultor y propiedad de finca | `CosechaServiceTests` y `Sprint2EndpointsTests` |
| Fecha y categoría válidas | `CosechaServiceTests` |
| Persistencia y filtro combinado | `Sprint2EndpointsTests.publicaUnaCosechaPersistenteYpermiteFiltrarla` |
| Strategy por zona | `CalculadoraFleteServiceTests` |
| Observer posterior a la publicación | verificación del evento y endpoint de avisos |

## Criterios de terminado aplicables

- JDK 17 LTS, Maven Wrapper y compilación reproducible.
- Checkstyle sin infracciones y todas las pruebas JUnit aprobadas.
- Cobertura JaCoCo igual o superior a 60 % en instrucciones, ramas y líneas.
- Pruebas unitarias con Mockito y pruebas REST con MockMvc.
- Persistencia relacional validada y secretos fuera del repositorio.
- Pull Request con check `build` aprobado antes de integrar.
- README, backlog, especificación, patrones y ceremonias actualizados.

La aprobación de otra persona solo se registra si realmente existe. La ejecución
individual no se presenta como revisión por pares.

## Resultado verificable

El PR [#52](https://github.com/JuanFiligrana/Agrovalle-Conect/pull/52) recibió el check
`Java CI / build` aprobado en la [ejecución 35](https://github.com/JuanFiligrana/Agrovalle-Conect/actions/runs/39735124556).
El resultado fue: cero infracciones de Checkstyle, 55 pruebas aprobadas, cero fallos,
cero errores, cero omitidas y 473 de 483 líneas cubiertas (97,93 %). El artefacto
`jacoco-35` conserva el reporte HTML; el resumen reproducible está en
[`docs/evidencias-sprint2.md`](evidencias-sprint2.md).
