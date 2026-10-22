# D05 · Explicación de las secuencias

## Alcance y trazabilidad

Se documentan los escenarios de HU01–05 del Sprint 1 largo. La base es `BACKLOG.md`, la
especificación formal de HU01 y los modelos D02/D03, contrastados con el código vigente.

Las secuencias muestran el comportamiento objetivo completo. El incremento vigente ya usa
JWT y JPA/PostgreSQL para usuarios, fincas y cosechas. Contacto, notificaciones, ventas
históricas y varias transacciones del modelo D02 continúan pendientes o temporales.

La profesora pidió mensajes numerados y barras verticales de activación. Cada hoja muestra un caso de uso con sus llamadas, retornos, condiciones y respuestas.

## Participantes y nivel de detalle

| Línea de vida | Qué representa |
|---|---|
| `vista:Interfaz` | Pantalla que utiliza el actor indicado; D07 incorpora el prototipo navegable. |
| `api:Presentacion` | Límite de la API: controlador, validación de entrada, filtro JWT y traducción de errores. |
| `negocio:Servicio` | Servicio del caso de uso y su límite transaccional administrado por Spring. |
| `datos:Persistencia` | Colaboración de contratos, adaptadores JPA, consultas de catálogo y soporte de persistencia de D03. |
| `bd:PostgreSQL` | Operaciones sobre el esquema relacional `agrovalle` de D02. |

Se usa un nivel de interacción entre módulos para mantener cinco participantes visibles.
`Presentacion` y `Persistencia` son agrupaciones lógicas del dibujo, no nuevas clases Java
monolíticas. La validación JWT pertenece al filtro de seguridad y no al método del
controlador. La línea de datos agrupa contratos y adaptadores definidos en D03.

Los nombres de operaciones de persistencia son descriptivos: no son firmas Java completas ni SQL listo para ejecutar. Las consultas de municipios y categorías corresponden a las entidades de catálogo de D02; durante la implementación se concretarán sus interfaces específicas.

## Escenarios y respuestas

| Hoja | Éxito | Alternativas representadas |
|---|---|---|
| 01 · HU01 | Cuenta de agricultor confirmada; 201 con ID, sin contraseña ni hash. | 400 por formato, rol no permitido o municipio inválido; 409 por correo/cédula duplicados, incluso si el conflicto se detecta al persistir; rollback por error. |
| 02 · HU02 | Cosecha activa vinculada a finca propia; 201 con ID. | 401 por JWT inválido; 400 por datos/reglas; 403 por rol o finca ajena; 404 por finca inexistente; rollback por error. |
| 03 · HU03 | 200 con promedio regional histórico, conteo y unidad COP/kg. | 400 por parámetro vacío/ausente; 200 con promedio null y conteo 0 cuando no hay ventas. |
| 04 · HU04 | 200 con ofertas activas que cumplen los filtros. | 400 si la categoría enviada no existe; 200 con arreglo vacío si no hay coincidencias. |
| 05 · HU05 | Contacto y aviso registrados en una misma transacción; 200 con ID y confirmación de aviso. | 401 por JWT inválido; 400 por mensaje inválido; 403 por rol; 404 por oferta inexistente o inactiva; rollback de ambas escrituras si falla una. |
| 06 · FalloTransaccional | Se deshacen las escrituras de la transacción y se responde sin éxito parcial. | 409 para unicidad de correo/cédula en HU01; 500 para los demás errores de persistencia representados. |

No todos los errores de infraestructura posibles se enumeran en estos dibujos. La hoja 06 representa errores transaccionales gestionados por el backend; no acredita pruebas de recuperación, conectividad o tolerancia a fallos de red.

## HU01 · Registro de agricultores

La petición es pública: esta operación no requiere un JWT previo. Presentación comprueba el formato y el rol AGRICULTOR; el servicio normaliza los datos y consulta municipio y unicidad. La validación de catálogo produce 400; el correo o la cédula duplicados producen 409.

La contraseña se convierte en hash BCrypt. El diseño D02 crea `usuario` y su subtipo
`agricultor` dentro de una transacción, usando una PK compartida. El esquema incremental
actual conserva un único registro `usuarios` con rol. Ambos mantienen restricciones únicas
para correo y documento; una consulta previa de duplicados no reemplaza esas restricciones.

La respuesta 201 se produce después de confirmar la transacción. Si falla la inserción de cualquiera de las dos tablas, se aplica la hoja 06 y no queda una cuenta parcial. El contrato de respuesta excluye contraseña y hash.

## HU02 · Publicación de cosechas

El actor debe estar autenticado como agricultor y tener una finca registrada. La identidad usada para comprobar propiedad se obtiene de la autenticación; no se considera suficiente un ID enviado por el cliente.

El servicio comprueba rol, propiedad de la finca, catálogo de producto/categoría, cantidad y precio positivos y fecha igual o posterior a hoy en `America/Bogota`. La ausencia de finca conserva la respuesta 404 del prototipo; el escenario de finca ajena responde 403.

Se guarda una `cosecha` con FK a `finca` y `producto`, activa. El agricultor y el municipio se obtienen por la finca; la categoría, por el producto. Se respeta así la normalización de D02. La ruta existente `/api/v1/productos` se conserva como contrato de publicación de ofertas/cosechas.

## HU03 · Consulta de precios regionales

`PrecioController` delega en el `ConsultaPreciosService` propuesto en D03. El acceso a datos consulta `v_precios_24h` del diseño D02, con precios unitarios históricos de líneas de pedidos entregados dentro de las últimas 24 horas. No se promedian los precios de las ofertas que estén activas en ese momento.

El promedio es aritmético no ponderado por cantidad, se presenta con dos decimales y unidad COP/kg. `cantidad_transacciones` cuenta las líneas de venta que cumplen la ventana, según la interpretación documentada en D02. Si no hay resultados de venta, la proyección devuelve `promedio: null` y conteo 0; sigue siendo una respuesta 200.

Esta consulta requiere datos históricos de pedidos completados o datos de prueba coherentes. El diagrama no afirma que ya existan esas ventas en el prototipo.

## HU04 · Filtro por categoría y municipio

Los filtros son opcionales. El bloque `opt` valida la categoría solamente si se envió. Una categoría inexistente produce 400; una categoría válida sin ofertas coincidentes produce 200 con `[]`.

La consulta recupera solamente cosechas activas y combina ambos filtros cuando están presentes. Para municipio sigue `cosecha → finca → municipio`; para categoría sigue `cosecha → producto → categoria`. Se usa la relación de D02 sin repetir municipio o categoría como columnas de cosecha.

El resultado se transforma en DTO de salida y se devuelve como arreglo JSON. No se crea una oferta ni se modifica inventario durante esta consulta.

## HU05 · Contacto y notificación

La API verifica autenticación y formato del mensaje: no vacío y máximo 1000 caracteres. El servicio comprueba que el actor sea comprador, que la oferta exista y esté activa y quién es su agricultor receptor. El destinatario se deriva de la oferta y su finca; no se acepta un destinatario arbitrario enviado por el comprador.

El contrato del prototipo llama `productoId` al identificador de la oferta/cosecha. Esa correspondencia se conserva al interpretar el mensaje: no debe confundirse con el `id_producto` del catálogo de D02.

Se inicia la unidad transaccional y se insertan primero `contacto` y luego `notificacion`, con origen en ese contacto. Si falla la primera escritura se interrumpe el flujo. Si falla la segunda, se revierte también la primera. Los identificadores previos al commit son provisionales y no implican aceptación de la operación.

Después de confirmar ambas escrituras se devuelve 200 con el ID y confirmación de aviso interno registrado. Ese resultado no afirma envío de correo, WhatsApp o notificación a un teléfono: el alcance es la notificación interna persistente del backlog.

## Transacciones e interacción reutilizable

`iniciar TX`, `confirmar TX` y `revertir TX` representan efectos de la infraestructura de Spring alrededor del caso de uso, administrados por el límite transaccional del servicio. No indican que los controladores deban escribir SQL ni que se deban agregar métodos de commit manual a los puertos del negocio.

En los flujos ya materializados, el proxy de Spring y el administrador de transacciones
coordinan esos efectos. En los flujos futuros, este es el comportamiento que deberá
implementarse. Los dibujos separan la escritura provisional de la confirmación para mostrar
que la respuesta HTTP de éxito ocurre después del commit.

Los fragmentos `break` de fallos transaccionales invocan `FalloTransaccional` mediante `ref`. La hoja 06 recibe el contexto de la petición que ya estaba en curso, revierte la unidad de trabajo y devuelve el error correspondiente. El resto del flujo principal no se ejecuta cuando se toma ese camino.

La traducción de errores conserva el contrato común del backlog: estado HTTP y cuerpo JSON con `code`, `message` y `fields`, sin datos internos. La implementación actual de `ApiExceptionHandler` requiere adaptación a ese contrato; no se acredita aquí que ya devuelva esa estructura.

## Alcance de la revisión

Se revisan numeración, orden temporal, llamadas/retornos, fragmentos, referencias entre hojas, activaciones y correspondencia con las historias y D02/D03. La generación de estos archivos no ejecuta los endpoints ni reemplaza las pruebas del backend, de las transacciones o de concurrencia necesarias para cerrar las HU.

## Fuentes

- Transcripción de clase compartida: diagramas de secuencia, mensajes numerados y barras de activación.
- Código vigente del repositorio: `BACKLOG.md`, especificación, controladores, DTO,
  seguridad, servicios, puertos y adaptadores.
- D02: esquema, normalización y mapeo JPA previstos.
- D03: cuatro capas, servicios, contratos y adaptadores previstos.
- [OMG: UML 2.5.1](https://www.omg.org/spec/UML/2.5.1), referencia general de UML.
- [PlantUML: diagramas de secuencia](https://plantuml.com/sequence-diagram), referencia de ejemplos de participantes, mensajes, fragmentos y activaciones. Los archivos entregados son Draw.io nativo, no imágenes incrustadas generadas por PlantUML.
