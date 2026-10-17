# D07 · Explicación de los mockups

## 1. Propósito y fuentes

La entrega representa cómo una persona usaría HU01–HU05 del Sprint 1 largo: registrarse como agricultor, publicar una cosecha, consultar una referencia de precio, filtrar ofertas y enviar una solicitud al agricultor. Las pantallas trasladan a la vista los comportamientos de D05 y mantienen los datos y dependencias de D02, D03 y D06.

Fuentes del proyecto:

- [BACKLOG.md, HU01–HU05 y reglas comunes](https://github.com/JuanFiligrana/Agrovalle-Conect/blob/7c8bf2f9bce0874a96403102f0e37ccc2306dd3a/BACKLOG.md).
- [Especificación formal de HU01](https://github.com/JuanFiligrana/Agrovalle-Conect/blob/7c8bf2f9bce0874a96403102f0e37ccc2306dd3a/docs/especificacion-hu01.md).
- Documento de planificación aportado: `SPRING1 TABLA AgroValle Connect.docx`, selección HU01–HU05, 23 puntos.
- Transcripción de la clase de la profesora Paola Andrea Bedoya Toro: artefactos de diseño y mockups en la carpeta de diseño del repositorio.
- Entregas D01–D06: entidades, modelo relacional, arquitectura, despliegue, secuencias y componentes.

Se revisaron el backlog, los DTO y la portada del commit `7c8bf2f9bce0874a96403102f0e37ccc2306dd3a` de `chore/sprint-0-completo`. La portada actual es una página informativa: estos formularios completos son un diseño objetivo. Los repositorios del prototipo siguen en memoria y la integración JPA/PostgreSQL y la validación JWT del diseño continúan pendientes. D07 no modifica el código ni la selección del sprint.

## 2. Pantallas y actores

| Hoja | Actor | Interacción principal | Respuesta de éxito prevista |
|---|---|---|---|
| 01 · HU01 | Visitante que desea ser agricultor | Completar los datos y pulsar Crear cuenta | Confirmación de la cuenta, identificador y enlace para iniciar sesión. |
| 02 · HU02 | Agricultor autenticado con finca propia | Elegir finca/producto y completar cantidad, precio y fecha | Confirmación de la oferta activa e identificador. |
| 03 · HU03 | Usuario o visitante | Elegir producto y consultar promedio | Promedio regional, COP/kg y conteo de registros completados en las últimas 24 horas. |
| 04 · HU04 | Comprador o visitante | Combinar municipio y categoría | Tarjetas únicamente de ofertas activas que cumplen ambos filtros. |
| 05 · HU05 | Comprador autenticado | Redactar mensaje para la oferta seleccionada | Identificador de interacción y confirmación del aviso interno registrado. |
| 06–08 | Los mismos actores | Comprender un resultado y corregir o continuar | Fragmentos alternativos de éxito, validación, acceso y ausencia de resultados. |

Las consultas HU03 y HU04 se diseñan públicas porque el backlog no les exige autenticación. Para enviar contacto sí se exige la identidad y el rol del comprador.

## 3. Navegación propuesta

| Acción visible | Destino o efecto previsto |
|---|---|
| Cosechas | Pantalla de búsqueda HU04. |
| Precios | Consulta HU03. |
| Soy agricultor | Entrada al registro HU01 para visitantes; acceso a publicación HU02 para agricultores autenticados. |
| Crear cuenta | Validar y enviar HU01; mostrar confirmación solo después de guardar. No inicia sesión automáticamente. |
| Iniciar sesión | Punto de integración con HU07. Su formulario y emisión de sesión están fuera de D07. |
| Publicar cosecha | Enviar HU02; la vista previa no se convierte en oferta publicada hasta confirmar el guardado. |
| Buscar cosechas | Consultar con ambos filtros; conservar las selecciones utilizadas. |
| Limpiar filtros | Restaurar Municipio: Todos y Categoría: Todas, quitar los filtros de la consulta y volver a buscar. |
| Contactar al agricultor | Abrir HU05 conservando el ID y contexto de la oferta elegida; comprobar la sesión antes de enviar. |
| Enviar solicitud | Validar el mensaje y registrar contacto y aviso; mostrar el estado de la hoja 08. |
| Volver a las cosechas | Regresar a HU04, conservando los filtros si la navegación lo permite. |

El enlace desde HU04 hacia HU05 no exige desarrollar una pantalla adicional de detalle HU10. Se utiliza el contexto obtenido en la lista y se revalida la oferta en el servidor al enviar. La confirmación de contacto no representa un pedido ni una conversación de chat en tiempo real.

## 4. Campos y correspondencia con el contrato

Los nombres visibles se expresan para la persona usuaria; los nombres JSON pertenecen al contrato técnico. El backlog utiliza algunos nombres en snake_case; el prototipo Java actual utiliza camelCase. D07 no renombra las rutas ni resuelve por sí solo esas diferencias.

| HU | Control de la vista | Correspondencia y regla |
|---|---|---|
| HU01 | Nombre completo | `nombre`; obligatorio. |
| HU01 | Correo electrónico | `correo`; obligatorio y formato válido. Se normaliza para comprobar unicidad. |
| HU01 | Cédula | `cedula`; obligatoria y única. Es un documento, no una cantidad para calcular. |
| HU01 | Contraseña | `contrasena`; entrada de contraseña con caracteres ocultos al escribir; mínimo actual de seis caracteres del DTO. La respuesta no devuelve contraseña ni hash. |
| HU01 | Municipio del Valle del Cauca | Selector del catálogo; `ubicacionValle` en el DTO actual. Debe corresponder a un municipio permitido. |
| HU01 | Cuenta: Agricultor | Etiqueta fija, no selector de privilegios. La solicitud corresponde a `rol=AGRICULTOR`. El servidor lo valida. |
| HU02 | Finca de origen | `fincaId`; selector de fincas propias. El servidor vuelve a comprobar propiedad. |
| HU02 | Categoría y Producto | `categoria` y `tipo` actuales. Elegir categoría limita el catálogo de productos; cambiarla limpia un producto incompatible. En D02, Producto pertenece a Categoría y Cosecha referencia a Producto. |
| HU02 | Cantidad (kg) | `cantidadKg`; positiva, máximo tres decimales del diseño. |
| HU02 | Precio por kg (COP) | `precioCopKg`; positivo, máximo dos decimales. |
| HU02 | Fecha de cosecha | `fechaCosecha`; visualmente día/mes/año, serializada como fecha ISO `yyyy-MM-dd`. Igual o posterior a hoy en `America/Bogota`. |
| HU02 | Municipio de la finca | Solo lectura; derivado de la finca, sin otro municipio independiente que pueda contradecirla. |
| HU03 | Producto | Parámetro `producto`; obligatorio para calcular la referencia elegida. La etiqueta Café puede mapear al valor canónico `Cafe` del contrato. |
| HU04 | Municipio / Categoría | Parámetros `municipio` y `categoria`; Todos/Todas omite el parámetro. Cuando ambos están presentes se aplica AND, nunca OR. |
| HU05 | Oferta y destinatario | `productoId` actual / `id_producto` en el backlog identifica la oferta Cosecha, no el Producto de catálogo de D02. El agricultor se obtiene de oferta → finca → propietario; no se escribe ni se elige manualmente. |
| HU05 | Tu mensaje | `mensaje`; de 1 a 1000 caracteres después de rechazar contenido vacío. Contador visible y error junto al campo. |

La vista no solicita escribir `agricultorId` ni `compradorId`. Los DTO actuales los reciben, pero el diseño objetivo obtiene la identidad del principal autenticado y no confía en un ID de persona enviado por el cliente. Esta adecuación queda para la implementación de seguridad.

Los selectores de fincas, municipios, productos y categorías requieren consultas de catálogo acordes con `CatalogoPort` y `FincaPort` de D06. Sus rutas de lectura definitivas todavía deben concretarse: el mockup no inventa endpoints que ya existan.

## 5. Trazabilidad de éxito y errores

Los códigos HTTP se muestran en esta matriz técnica. En la interfaz se presentan mensajes comprensibles y acciones concretas.

| HU / condición | Respuesta esperada | Representación o regla de interfaz |
|---|---|---|
| HU01 · Datos válidos | 201 | Hoja 06: cuenta creada #42, botón para iniciar sesión. |
| HU01 · Formato, campo obligatorio o municipio inválidos | 400 | Señalar los campos correspondientes y conservar los demás datos; la hoja 06 ejemplifica correo incorrecto. |
| HU01 · Correo o cédula duplicados | 409 | Hoja 06: ya existe una cuenta con esos datos, sin confirmar un nuevo registro. |
| HU02 · Publicación válida | 201 | Hoja 06: oferta #101 activa. La hoja 02 muestra la vista previa anterior al envío. |
| HU02 · Fecha anterior a hoy o números inválidos | 400 | Hoja 06: error junto a Fecha de cosecha. Cantidad y precio usan el mismo patrón de error junto al campo. |
| HU02 · Rol incorrecto o finca ajena | 403 | Mensaje de permisos; la hoja 06 ejemplifica finca ajena. No se publica. |
| HU02 · Sin sesión válida | 401 | Solicitar inicio de sesión de agricultor, reutilizando el patrón de acceso de la hoja 08 con el rol adecuado. |
| HU02 · Finca inexistente | 404 | Informar que la finca ya no está disponible y actualizar su selección. |
| HU03 · Ventas completadas en la ventana | 200 | Hoja 03: $18.750,00 COP/kg y 50 registros. |
| HU03 · Sin ventas completadas en la ventana | 200, promedio null y conteo 0 | Hoja 07: Sin datos y 0 registros, sin mostrar $0,00 como precio. |
| HU03 · Producto omitido o vacío | 400 | Pedir selección válida junto al selector; no presentar una referencia calculada. |
| HU04 · Ambos filtros válidos | 200 | Hoja 04: dos ofertas de Frutas en Dagua, ambas activas. |
| HU04 · Sin coincidencias | 200, arreglo vacío | Hoja 07: mensaje vacío, filtros visibles y acción para limpiarlos. |
| HU04 · Categoría inválida | 400 | Hoja 07: solicitar otra categoría; no confundir un filtro inválido con cero coincidencias. |
| HU05 · Contacto y aviso confirmados | 200 | Hoja 08: interacción #548 y aviso interno registrado al agricultor de la oferta #101. |
| HU05 · Mensaje vacío o demasiado largo | 400 | Hoja 08: error junto al mensaje; conservar lo escrito para corregirlo. |
| HU05 · Sesión ausente o vencida | 401 | Hoja 08: pedir inicio de sesión como comprador. No registrar el contacto. |
| HU05 · Rol no permitido | 403 | Hoja 08: explicar que la función es para compradores. |
| HU05 · Oferta inexistente o inactiva | 404 | Hoja 08: oferta no disponible y regreso a cosechas. |
| HU05 · Fallo transaccional gestionado por el servidor | 500 | Hoja 08: no se completó el envío ni se registraron cambios; conservar mensaje para reintentar. |

No se dibuja una pantalla separada para cada combinación de campos inválidos: se aplica el mismo patrón de validación junto al campo. La selección previa de fincas propias ayuda a evitar errores, pero no sustituye la autorización del servidor.

Si el agricultor aún no tiene fincas, HU02 debe mostrar “Registra una finca antes de publicar”, deshabilitar el envío y ofrecer la entrada a HU08. Este estado es una dependencia de implementación descrita aquí; el formulario completo de HU08 no se añade al alcance de D07.

El mensaje de ausencia de cambios de la hoja 08 se limita a un fallo transaccional que el servidor haya gestionado y comunicado con reversión, como D05-06. Si se pierde la conexión o no llega una respuesta, la vista no debe afirmar que no se guardó: debe indicar que el resultado no pudo confirmarse. D07 no añade una garantía de idempotencia al contacto ni acredita recuperación de fallos de red.

## 6. Ejemplos ficticios y coherencia de datos

Todos los valores son ilustrativos. No proceden de cuentas, cosechas ni ventas reales.

| Elemento | Datos de ejemplo |
|---|---|
| Agricultor registrado | ID 42 · Luis Andrés Rojas. |
| Oferta 101 | Mango · Frutas · finca La Esperanza · Dagua · Luis Andrés Rojas · 250 kg · $3.500,00 COP/kg · cosecha 24/10/2026. |
| Oferta 102 | Banano · Frutas · finca El Porvenir · Dagua · Ana Torres · 180 kg · $2.800,00 COP/kg · cosecha 23/10/2026. |
| Contacto | Interacción 548 sobre oferta 101; destinatario derivado Luis Andrés Rojas. El mensaje dibujado tiene 80 caracteres. |
| Referencia temporal | 17/10/2026 a las 02:00 en Colombia. La fecha 16/10/2026 de la hoja 06 es inválida para la simulación de publicación. En funcionamiento, “hoy” se recalcula. |

La cantidad escrita como `250,000` en el formulario utiliza coma decimal: equivale a **250 kg**, con tres decimales. El precio `3.500,00` equivale a **3500 COP/kg**. Al serializar JSON se utilizan números sin separadores de miles y con punto decimal.

Para reproducir el promedio de la hoja 03 puede utilizarse un conjunto sintético de 50 registros históricos de Café: 25 a 18.500 COP/kg y 25 a 19.000 COP/kg. Todos pertenecen a pedidos ENTREGADO completados dentro de las 24 horas anteriores al corte. Su media simple es `(25 × 18.500 + 25 × 19.000) / 50 = 18.750,00 COP/kg`.

De acuerdo con D02, cada registro contado corresponde a una línea de venta completada de `detalle_pedido`, con el precio histórico aceptado. No es un promedio ponderado por kg ni un conteo necesariamente igual al número de pedidos distintos. La UI usa “registros de ventas” para conservar esa interpretación. El fixture de Cacao sin registros es independiente del de Café. La búsqueda vacía representa una situación distinta a la búsqueda con dos ofertas, no datos simultáneos contradictorios.

## 7. Reglas de interacción y presentación al implementar

- Mostrar las etiquetas siempre; los ejemplos dentro de campos no reemplazan sus nombres.
- Asociar cada error al campo afectado, indicar el motivo con texto y llevar el foco al primer error. Conservar datos válidos; no exponer contraseñas en confirmaciones o mensajes.
- Durante Crear cuenta, Publicar cosecha, Consultar promedio, Buscar y Enviar solicitud, deshabilitar temporalmente la acción y cambiar su texto a un estado de progreso. Esto es una regla de interacción, no otra pantalla terminada.
- Mostrar confirmación solo con respuesta exitosa del servidor. El éxito de HU05 requiere que contacto y aviso interno estén confirmados en una misma transacción.
- En HU04, conservar filtros y resultados de la última consulta confirmada; un fallo de consulta debe informarse como error y no como lista vacía exitosa.
- Revalidar la oferta y los permisos al enviar HU05 aunque la lista original la mostrara activa.
- Usar controles de contraseña, fecha, selectores y cantidades con unidades. Añadir navegación por teclado, foco visible y mensajes anunciados a tecnologías de apoyo al implementar.
- El verde indica acciones y confirmaciones, el rojo errores y el ámbar necesidad de acceso. Siempre hay texto que explica el significado del color.
- Las hojas principales muestran escritorio a 1600 píxeles de ancho del lienzo. La adaptación a móviles deberá apilar columnas y permitir lectura sin desplazamiento horizontal; no se afirma que haya una implementación responsive probada.

## 8. Dependencias y límites

HU01 crea agricultores. El alta de compradores pertenece a HU06 y el inicio de sesión a HU07. HU02 necesita HU07 y HU08. HU05 necesita un comprador autenticado y una oferta activa; HU06/HU07 son sus dependencias. Los enlaces hacia esas capacidades expresan integración prevista, no ampliación del alcance de las pantallas entregadas.

HU03 necesita ventas completadas: el ciclo de pedidos de HU11–HU14 o datos de prueba debe proporcionar el histórico. Las ofertas activas de HU02 no bastan para calcular esa referencia. El valor de ejemplo no certifica que exista información de mercado real.

El contacto se confirma mediante una notificación interna persistida. No se diseña envío automático por correo, WhatsApp o SMS ni una bandeja de chat completa. No se añaden pagos, pedidos, carrito, GPS continuo ni rutas automáticas a estos mockups.

## 9. Correspondencia con la arquitectura

| Artefacto | Correspondencia con D07 |
|---|---|
| D01/D02 | Usuario/Agricultor, Municipio, Finca, Categoría, Producto y Cosecha explican el registro y la oferta. Contacto y Notificación explican el envío. Los detalles de pedidos entregados proporcionan el precio histórico. |
| D03 | Las pantallas pertenecen a presentación/vista. Los controladores traducen solicitudes; las reglas de propiedad, promedio e integridad permanecen en servicios. |
| D04 | El navegador ejecutará la vista web y se comunicará con el servidor de aplicación; no accederá directamente a PostgreSQL. |
| D05 | Cada formulario o consulta se corresponde con su secuencia HU01–HU05. Las confirmaciones ocurren después de la respuesta exitosa y las escrituras confirmadas. |
| D06 | La Interfaz web consume el contrato HTTP de la API. El backend coordina servicios, puertos y adaptadores. Los catálogos alimentan las selecciones. |

Rutas conservadas: `POST /api/v1/auth/register`, `POST /api/v1/productos`, `GET /api/v1/precios/promedio`, `GET /api/v1/productos` y `POST /api/v1/contacto/mensaje`. Sus códigos y ejemplos corresponden al comportamiento objetivo; las diferencias del prototipo se resolverán al implementar y probar las historias.

## 10. Verificación de esta entrega

Se generaron ocho páginas de Draw.io con elementos nativos editables y sus exportaciones SVG/PNG. Se comprobaron identificadores, referencias de pertenencia, límites de página, medidas de texto y correspondencia de las ocho páginas con las ocho parejas de imágenes. Se revisaron visualmente las ocho imágenes para verificar lectura, separación de campos y ausencia de recortes.

La revisión de contenido comprueba el contador de 80 caracteres, los dos filtros de ambas ofertas, la reutilización de oferta 101 entre publicación/búsqueda/contacto, la media sintética de 18.750,00, los estados sin datos y los mensajes principales de aceptación. Se comprueba también la integridad del ZIP y sus 19 archivos.

Esta verificación corresponde al artefacto de diseño: no sustituye pruebas del frontend, pruebas de API, integración con PostgreSQL, revisión de código ni aceptación de las historias.
