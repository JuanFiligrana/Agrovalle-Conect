# D06 · Explicación del diagrama de componentes

## Objetivo y base

Mostrar cómo se organiza el software de AgroValle Connect y qué contratos conectan sus partes. El diagrama mantiene las cuatro capas de D03 y los flujos de HU01–05 de D05, incluyendo el registro de fincas como apoyo a la publicación de cosechas.

La base revisada es la rama `chore/sprint-0-completo`, commit `15ff8dd40099b26e400530f1a0a7e32be6189cdb`. El prototipo tiene vista estática, controladores, DTO, servicios y repositorios en memoria. Los adaptadores JPA, la verificación JWT y los contratos dibujados son diseño objetivo.

Los componentes representan unidades funcionales. Las agrupaciones del backend pueden mantenerse en un solo ejecutable Spring Boot; el diagrama no exige crear servicios desplegados de forma independiente. D04 documenta dónde se ejecutan esas partes, mientras D06 muestra su organización y sus dependencias.

## Hoja 01 · Componentes y responsabilidades

| Componente | Responsabilidad y correspondencia |
|---|---|
| Interfaz web | Presentar información y formularios; consumir el contrato HTTP de la aplicación. La página base está en `resources/static/index.html`; los formularios completos están previstos en D07. |
| API REST y recursos web | Entregar la vista estática, recibir solicitudes REST, validar DTO y traducir resultados/errores a HTTP. Agrupa los controladores Auth, Finca, Producto, Precio, Contacto y Estado, sus DTO y `ApiExceptionHandler`. |
| Seguridad | Proveer identidad autenticada para rutas protegidas y el codificador de contraseñas. La verificación JWT es nueva; `PasswordEncoder` con BCrypt ya está declarado como bean en el prototipo. |
| Casos de uso / negocio | Aplicar reglas del dominio y coordinar las transacciones. Agrupa `AgricultorService`, `FincaService`, `CosechaService`, `ContactoService` y el nuevo `ConsultaPreciosService` de D03. |
| Persistencia JPA | Implementar los contratos de datos mediante adaptadores, repositorios Spring Data JPA, Hibernate y proyecciones del esquema D02. Está pendiente en el código. |
| PostgreSQL | Proveer el servicio externo de persistencia relacional con tablas, vistas e índices de D02. Se dibuja como componente de software externo; su servidor aparece en D04. |

El paquete del backend delimita las responsabilidades de ejecución Java. Los recursos de la interfaz se sirven desde el mismo JAR, aunque su interpretación se realiza en el navegador. Los nombres de componentes son agrupaciones de diseño; no se presentan como nombres de nuevas clases obligatorias.

Los tipos de `models` y `dto` son estructuras de datos compartidas según su función: las entidades describen estado y relaciones; los DTO describen el intercambio de entrada/salida. Los servicios y los adaptadores utilizan las entidades del modelo D02. La vista recibe DTO y no entidades completas con datos internos.

## Interfaces generales

| Interfaz de la vista general | Consumidor | Proveedor | Significado |
|---|---|---|---|
| HTTP AgroValle | Interfaz web. | API REST y recursos web. | Contrato externo para recursos HTML y solicitudes/respuestas JSON. Conserva las rutas del prototipo. |
| Identidad JWT | Presentación en las rutas protegidas. | Seguridad, mediante el filtro previsto. | Identidad y rol obtenidos de una autenticación verificable. |
| PasswordEncoder | Registro dentro de casos de uso. | Codificador BCrypt del componente de seguridad. | `encode` protege la contraseña; `matches` permite comprobarla cuando se implemente el inicio de sesión. |
| Casos de uso | API REST. | Negocio. | Resumen conceptual de los puntos de entrada de HU01–05 y fincas. El detalle lo dan los servicios, no una nueva interfaz Java gigante. |
| CosechaPort | Negocio. | Persistencia JPA. | Ejemplo de los contratos de salida; la hoja 02 detalla cada interfaz de datos. |
| Servicio SQL | Persistencia JPA desde Java. | PostgreSQL. | Contrato externo de acceso relacional. JDBC es la API del cliente Java; el motor atiende el protocolo PostgreSQL. |

Las interfaces HTTP, Identidad JWT, Casos de uso y Servicio SQL resumen fronteras del modelo; no implican que existan archivos Java con esos nombres. `PasswordEncoder` sí corresponde a una interfaz de Spring Security. Los contratos terminados en `Port` son interfaces de aplicación propuestas.

La dependencia hacia identidad JWT se aplica a las operaciones protegidas, como publicación y contacto. El registro de HU01 y las consultas públicas de precios/filtros conservan su alcance público. La presencia del componente de seguridad no significa que todas las peticiones deban exigir JWT.

## Hoja 02 · Contratos y adaptadores

| Contrato de negocio | Servicios consumidores | Adaptador JPA propuesto |
|---|---|---|
| UsuarioPort | AgricultorService, FincaService, CosechaService, ContactoService. | UsuarioJpaAdapter. |
| FincaPort | FincaService, CosechaService. | FincaJpaAdapter. |
| CosechaPort | CosechaService, ContactoService. | CosechaJpaAdapter. |
| PrecioPort | ConsultaPreciosService. | ConsultaPrecioJpaAdapter. |
| ContactoPort | ContactoService. | ContactoJpaAdapter. |
| NotificacionPort | ContactoService. | NotificacionJpaAdapter. |
| CatalogoPort | AgricultorService, FincaService, CosechaService. | CatalogoJpaAdapter. |

La segunda hoja repite el componente de negocio como **referencia por contrato**. No propone siete copias del negocio ni siete procesos. Los nombres de servicios dentro de cada caja identifican a los consumidores concretos.

Los puertos se ubican en el negocio, mientras sus adaptadores están en acceso a datos. Los servicios requieren los contratos; los adaptadores los implementan. Esa dirección permite recibir las dependencias por constructor y sustituir el almacenamiento mediante una implementación que conserve el contrato.

Cada adaptador se especializa en su responsabilidad. La infraestructura comparte el datasource y el administrador de transacciones configurados para el backend; no se propone una base de datos por adaptador.

## Refinamiento del catálogo respecto a D03

D03 presentó seis contratos de datos y D05 mostró además consultas a municipios, categorías y productos. D06 concreta esas consultas mediante `CatalogoPort` y `CatalogoJpaAdapter` como **refinamiento del diseño**.

Ese contrato permite consultar municipio válido, categoría existente y producto del catálogo sin mezclar esas consultas con el registro de usuarios o con un repositorio de propósito general. Sus entidades ya existen en D02. No cambia el esquema, los endpoints, las historias ni las cuatro capas.

Las firmas y los tipos completos se concretarán al implementar. La relación con los seis contratos anteriores se conserva; este documento no afirma que las siete interfaces ya estén programadas ni modifica los archivos D03/D05 subidos.

## Reglas preservadas de D02 y D05

- HU01 crea usuario y subtipo de agricultor como una unidad, con hash BCrypt y sin secretos en la respuesta. Los índices únicos protegen correo/cédula.
- HU02 relaciona la cosecha con finca y producto; propietario, municipio y categoría se obtienen por las relaciones de D02.
- HU03 usa `PrecioPort` y la proyección histórica de `v_precios_24h`, en lugar de promediar ofertas activas. El promedio es no ponderado por cantidad y devuelve null/0 cuando no hay ventas.
- HU04 consulta ofertas activas, valida la categoría enviada y devuelve arreglo vacío cuando una combinación válida no tiene coincidencias.
- HU05 coordina `ContactoPort` y `NotificacionPort` dentro de la misma transacción. El destinatario se deriva de la oferta y su finca. El resultado confirma un aviso interno registrado.

Las operaciones de commit/rollback se administran mediante la infraestructura de Spring en el límite del caso de uso, como se explicó en D05. Dibujar dos adaptadores no convierte automáticamente sus escrituras en una operación atómica: debe existir una transacción compartida y sus pruebas.

## Patrones y calidad

Los contratos y adaptadores materializan Repository y la inversión de dependencias prevista en D03. Las responsabilidades diferenciadas de registro, ofertas, precios y contacto ayudan a mantener SRP. Las entidades JPA de D02 permiten el mapeo de filas mediante Hibernate; los DTO mantienen el contrato público.

Los componentes compartidos por Spring deben conservar los datos de cada operación en su contexto correspondiente. El diagrama de componentes no se usa para justificar almacenamiento del usuario actual o del carrito en campos mutables de un servicio compartido.

## Qué existe y qué queda pendiente

| Elemento | Estado observado o previsto |
|---|---|
| Vista, controladores, DTO y cuatro servicios base. | Existen en el prototipo, con límites documentados. |
| BCrypt / PasswordEncoder. | Bean ya declarado en la clase de arranque. |
| ConsultaPreciosService separado. | Nuevo componente de implementación propuesto por D03. |
| Filtro y verificación JWT. | Pendientes; comprobar una cabecera no sustituye validar el token. |
| Puertos y siete adaptadores de D06. | Propuestos; todavía deben implementarse. |
| Integración Spring Data JPA/PostgreSQL. | Pendiente; el prototipo conserva repositorios en memoria. |
| Modelos normalizados, transacciones y formularios completos. | Diseño definido en D02/D03/D05 y trabajo técnico posterior. |

## Alcance de la revisión

Se revisan legibilidad, componentes/paquetes, interfaces, dirección de las dependencias y correspondencia con D02/D03/D05. Esta entrega crea documentación editable; no ejecuta pruebas del backend ni acredita el cierre funcional de las historias.

## Fuentes

- Transcripción de clase compartida: artefacto de componentes y ejemplo de agrupación de componentes en paquetes.
- [Base revisada del repositorio](https://github.com/JuanFiligrana/Agrovalle-Conect/tree/15ff8dd40099b26e400530f1a0a7e32be6189cdb): estructura, controladores, servicios y bean de BCrypt.
- D02, D03 y D05 del mismo Sprint de diseño.
- [OMG: UML 2.5.1](https://www.omg.org/spec/UML/2.5.1), referencia general de notación.
- [PlantUML: diagramas de componentes](https://plantuml.com/component-diagram), ejemplos de componentes, interfaces y agrupación. Los archivos entregados son Draw.io nativo con elementos editables.
