# D06 · Explicación del diagrama de componentes

## Objetivo

D06 muestra cómo se organiza AgroValle Connect y qué contratos conectan la interfaz web,
la API, la seguridad, los casos de uso, el acceso a datos y PostgreSQL. Mantiene las cuatro
capas de D03 y los flujos objetivo de HU01–HU05 definidos en D05.

Los componentes son unidades lógicas dentro de una misma aplicación Spring Boot. No se
proponen microservicios independientes. D04 explica los nodos de ejecución; D06 explica la
organización y la dirección de las dependencias.

## Hoja 01 · Componentes generales

| Componente | Responsabilidad | Estado |
|---|---|---|
| Interfaz web | Portada, catálogo, detalle, registro, mensajes, seguimiento y paneles por rol; consume HTTP/JSON. | Diez vistas responsive en `resources/static`. |
| API REST y recursos | Sirve la web, valida DTO y traduce resultados o errores a HTTP. | Controladores de autenticación, finca, producto, precio, contacto, flete, notificación y estado. |
| Seguridad | BCrypt, filtro JWT, identidad y rol para rutas protegidas. | Implementado para los endpoints declarados. |
| Casos de uso | Registro, autenticación, finca, cosecha, contacto, precios, flete y avisos. | Servicios separados e inyectados por constructor. |
| Acceso a datos | Implementa puertos mediante adaptadores JPA o almacenamiento temporal. | JPA para usuarios, fincas, cosechas, contactos y sus notificaciones; los avisos Observer de publicación siguen en memoria. |
| PostgreSQL | Conserva el esquema incremental y aplica restricciones. | Siete tablas implementadas; D02 conserva el modelo objetivo. |

Los recursos web viajan dentro del mismo JAR pero se interpretan en el navegador. La
interfaz HTTP es externa; `PasswordEncoder` y los puertos son interfaces de software; el
servicio SQL representa la frontera JDBC/protocolo PostgreSQL.

## Hoja 02 · Contratos de datos

La hoja de diseño enumera siete contratos para cubrir todo D02. Cuatro están materializados
en el incremento y tres permanecen como objetivo:

| Contrato | Estado y proveedor |
|---|---|
| `UsuarioPort` | Implementado por `AgricultorRepository` con `UsuarioJpaRepository`. |
| `FincaPort` | Implementado por `FincaRepository` con `FincaJpaRepository`. |
| `CosechaPort` | Implementado por `CosechaRepository` y cuatro repositorios JPA normalizados. |
| `ContactoPort` | Implementado por `ContactoRepository` con repositorios JPA; guarda contacto y notificación en una transacción. |
| `PrecioPort` | Objetivo para consultar ventas históricas y `v_precios_24h`. |
| `NotificacionPort` | Separación objetivo; actualmente la notificación de contacto se persiste mediante `ContactoPort` para mantener la unidad transaccional. |
| `CatalogoPort` | Refinamiento objetivo para catálogos compartidos; hoy esas consultas se resuelven dentro del adaptador de cosecha y validadores. |

Los servicios requieren contratos y los adaptadores los implementan. Spring los inyecta
por constructor. Esta dirección aplica DIP y permite cambiar infraestructura sin introducir
SQL ni decisiones HTTP en los servicios.

## Correspondencia con el modelo

- HU01 persiste una cuenta con rol y hash BCrypt. La especialización completa de D02 se
  conserva como evolución del esquema.
- HU02 relaciona cosecha, finca, producto y categoría mediante claves foráneas.
- HU03 usa actualmente las cosechas del incremento; el histórico de pedidos entregados es
  trabajo posterior.
- HU04 filtra ofertas activas por municipio y categoría.
- HU05 obtiene la identidad del JWT, valida rol y oferta activa y guarda contacto y
  notificación en PostgreSQL. Solo los participantes consultan el mensaje y solo el
  destinatario puede marcar su notificación como leída.
- Pedidos, reservas, envíos, eventos de seguimiento e idempotencia aparecen en D02 y en el
  prototipo visual, no como componentes backend terminados.

## Patrones y calidad

Los puertos/adaptadores materializan Repository e inversión de dependencias. Strategy
encapsula el cálculo de flete y Observer desacopla la publicación de cosecha de sus avisos.
DTO evita exponer entidades persistentes y Hibernate realiza el mapeo a filas.

Un componente compartido por Spring no guarda el usuario actual ni un carrito en campos
mutables. Las operaciones que coordinan varias tablas deberán tener límites transaccionales
en el servicio y pruebas de concurrencia.

## Alcance

El diagrama conserva el producto objetivo y la explicación distingue qué piezas están
programadas. La existencia de un componente o una pantalla no acredita por sí sola una
historia: cada módulo requiere endpoint, reglas, persistencia, seguridad y pruebas.

## Fuentes de la entrega

- Guías del Sprint de diseño: componentes, cuatro capas, PostgreSQL y patrones.
- Código vigente: controladores, servicios, seguridad, puertos, adaptadores y vistas.
- D02, D03 y D05: modelo relacional, arquitectura y secuencias.
