# Especificación formal y comparación BDD del Sprint 2

## 1. Identificación

- Proyecto: AgroValle Connect.
- Historias: HU-02 Publicación de cosechas y HU-04 Filtro por categoría y municipio.
- Prioridad: Must Have.
- Estimación: 5 + 3 = 8 Story Points.
- Referencia de estructura: ISO/IEC/IEEE 29148:2018.
- Fuentes: `BACKLOG.md`, diseño D01–D07 y transcripción de clase del 22 de octubre.

La referencia normativa se utiliza para redactar requisitos singulares, necesarios,
verificables y trazables. El documento no afirma una certificación ISO.

## 2. Contexto, actores y límites

El agricultor autenticado publica disponibilidad agrícola originada en una finca propia.
El comprador o visitante consulta la oferta activa. La autenticación y las fincas son
precondiciones provistas por HU-07 y HU-08. Los pedidos, pagos, reservas y despachos no
forman parte de estas dos historias.

| Interfaz | Actor | Autenticación | Resultado |
|---|---|---|---|
| `POST /api/v1/productos` | Agricultor | JWT Bearer obligatorio | Cosecha creada |
| `GET /api/v1/productos` | Comprador o visitante | No requerida | Lista filtrada |

## 3. Requisitos de HU-02

| ID | Requisito verificable | Verificación |
|---|---|---|
| RF-HU02-01 | El sistema deberá exigir un JWT válido para publicar una cosecha. | MockMvc sin token, con token inválido y con token válido. |
| RF-HU02-02 | El sistema deberá permitir la publicación únicamente al rol AGRICULTOR. | Prueba con una cuenta COMPRADOR; respuesta 403. |
| RF-HU02-03 | El sistema deberá obtener el agricultor del JWT y no de un campo controlado por el cliente. | Inspección del DTO y prueba del servicio. |
| RF-HU02-04 | El sistema deberá comprobar que la finca exista y pertenezca al agricultor autenticado. | Pruebas 404 y 403 sin inserción. |
| RF-HU02-05 | El sistema deberá aceptar solamente categorías del catálogo versionado. | Pruebas con `Frutas` y con categoría inexistente. |
| RF-HU02-06 | El sistema deberá aceptar cantidad y precio estrictamente positivos. | Validación Bean Validation y respuesta 400. |
| RF-HU02-07 | El sistema deberá rechazar fechas anteriores al día actual en `America/Bogota`. | Prueba de límite temporal; respuesta 400. |
| RF-HU02-08 | El sistema deberá persistir la oferta activa y responder 201 con ID único. | Prueba REST seguida por una consulta. |
| RF-HU02-09 | El sistema deberá relacionar la cosecha con finca y producto normalizados. | Validación JPA del esquema y prueba de integración. |
| RF-HU02-10 | El sistema deberá emitir un evento al completar la publicación. | Verificación Mockito de `CosechaPublicadaEvent`. |

### Precondiciones y poscondiciones

- Precondición: existe una cuenta de agricultor autenticable y una finca registrada.
- Poscondición de éxito: existe una cosecha activa vinculada a esa finca y se emite un
  evento con la representación guardada.
- Poscondición de error: no se crea una cosecha cuando falla autenticación, autorización,
  propiedad, catálogo, cantidad, precio o fecha.

## 4. Requisitos de HU-04

| ID | Requisito verificable | Verificación |
|---|---|---|
| RF-HU04-01 | El sistema deberá permitir omitir ambos filtros. | Prueba que devuelve solo ofertas activas. |
| RF-HU04-02 | El sistema deberá filtrar por municipio canónico del Valle del Cauca. | Prueba con `Dagua` y normalización de espacios/caso. |
| RF-HU04-03 | El sistema deberá filtrar por categoría canónica. | Prueba con `Frutas` y normalización. |
| RF-HU04-04 | El sistema deberá aplicar simultáneamente ambos filtros cuando se envíen. | Prueba con coincidencias y descartes. |
| RF-HU04-05 | El sistema deberá excluir ofertas inactivas. | Prueba unitaria con una oferta inactiva. |
| RF-HU04-06 | El sistema deberá devolver `[]` y 200 cuando los filtros válidos no tengan coincidencias. | Prueba REST de lista vacía. |
| RF-HU04-07 | El sistema deberá responder 400 si municipio o categoría no pertenecen a su catálogo. | Pruebas de ambos parámetros inválidos. |

## 5. Requisitos de calidad y restricciones

| ID | Requisito | Evidencia prevista |
|---|---|---|
| RC-S2-01 | La implementación deberá usar Java 17 LTS y Spring Boot 3.5.16. | Maven Enforcer y CI. |
| RC-S2-02 | La persistencia normal deberá utilizar PostgreSQL sin credenciales versionadas. | Configuración por variables y revisión del repositorio. |
| RC-S2-03 | La suite deberá aprobarse completamente y JaCoCo deberá medir al menos 60 % de instrucciones, ramas y líneas. | `mvnw -B verify` y artefacto JaCoCo. |
| RC-S2-04 | Los controladores no deberán implementar reglas de negocio ni acceso SQL. | Revisión de dependencias y Checkstyle. |
| RC-S2-05 | Los cambios deberán integrarse mediante rama y Pull Request con check obligatorio. | Historial del PR y protección de `main`. |
| RC-S2-06 | Los catálogos deberán normalizar mayúsculas, espacios y tildes sin aceptar valores ajenos. | Pruebas unitarias y REST. |

## 6. Comparación con BDD

| Aspecto | Especificación 29148 | Escenario BDD |
|---|---|---|
| Unidad | Un requisito numerado expresa una obligación singular. | Given/When/Then describe un ejemplo observable. |
| Cobertura | Incluye interfaces, errores, datos, seguridad y restricciones. | Prioriza conversaciones y criterios representativos del backlog. |
| Verificación | Cada fila identifica una técnica o prueba. | Cada Then define el resultado esperado del escenario. |
| Trazabilidad | Los IDs enlazan diseño, código, prueba y evidencia. | El escenario enlaza directamente con la historia de usuario. |
| Uso conjunto | Reduce ambigüedad contractual y facilita auditoría. | Facilita comprensión y automatización desde la perspectiva del usuario. |

BDD no reemplaza la especificación formal y la tabla formal no reemplaza ejemplos. En
este incremento, ambos niveles apuntan a las mismas pruebas de servicio y de API.

## 7. Matriz código–prueba

| Requisito | Componente | Prueba |
|---|---|---|
| RF-HU02-01–04 | SecurityConfig, JwtAuthenticationFilter, CosechaService | Sprint2EndpointsTests, CosechaServiceTests |
| RF-HU02-05–07 | CosechaRequest, CategoriasCosecha, CosechaService | CosechaServiceTests |
| RF-HU02-08–09 | CosechaRepository y entidades JPA | Sprint2EndpointsTests |
| RF-HU02-10 | CosechaPublicadaEvent | CosechaServiceTests |
| RF-HU04-01–07 | CosechaService y MunicipiosValle | CosechaServiceTests, Sprint2EndpointsTests |
| RC-S2-01–06 | `pom.xml`, workflow, configuración y documentos | Check `build` y reporte JaCoCo |

## 8. Referencia

ISO/IEC/IEEE 29148:2018, *Systems and software engineering — Life cycle processes —
Requirements engineering*.
