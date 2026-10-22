# D04 · Explicación del despliegue

## Objetivo y estado

El diagrama ubica las partes de AgroValle Connect y hace explícitos los protocolos y
puertos entre cliente, servidor de aplicación y servidor de base de datos. La hoja 01
representa la topología remota objetivo; la hoja 02 representa el entorno local vigente.

La configuración actual usa Java 17 LTS, Spring Boot 3.5.16, Tomcat embebido y el puerto
`${PORT:8081}`. `application.properties` define un datasource PostgreSQL, Hibernate valida
el esquema y `schema.sql` crea las cinco tablas del incremento: usuarios, fincas,
categorías, productos y cosechas. Agricultor, finca y cosecha se persisten mediante Spring
Data JPA; contacto conserva un repositorio temporal en memoria.

## Hoja 01: topología de tres nodos

| Nodo | Entorno de ejecución | Artefactos y responsabilidades |
|---|---|---|
| 1. Cliente | Navegador en PC, celular o tableta. | Recibe HTML/CSS/JavaScript, presenta la aplicación web y envía solicitudes HTTP. |
| 2. Servidor de aplicación | Linux propuesto con NGINX y JVM Java 17 LTS. | NGINX termina HTTPS; el JAR sirve la interfaz, ejecuta la API, los servicios y los adaptadores JPA. |
| 3. Servidor de datos | PostgreSQL 16 propuesto en red privada. | Conserva el esquema relacional, sus datos, índices y restricciones. |

NGINX y la JVM comparten el nodo de aplicación, por lo que no se añade un cuarto equipo.
El navegador ejecuta una copia de los recursos web recibidos; no contiene un segundo
backend y nunca accede directamente a PostgreSQL.

## Protocolos y puertos

| Comunicación | Protocolo | Destino | Alcance |
|---|---|---|---|
| Navegador → NGINX | HTTPS sobre TLS/TCP | 443 | Acceso público propuesto para interfaz y API. |
| NGINX → Spring Boot | HTTP/TCP por loopback | `127.0.0.1:8081` | Salto interno en el mismo servidor; TLS termina en NGINX. |
| Aplicación → PostgreSQL remoto | Protocolo PostgreSQL sobre TLS/TCP, mediante JDBC | 5432 | Conexión privada propuesta con validación del certificado. |
| Navegador → Spring Boot local | HTTP/TCP | `localhost:8081` | Ejecución local vigente. |
| Aplicación → PostgreSQL local | Protocolo PostgreSQL/TCP, mediante JDBC | 5432 | Conexión configurada mediante `DB_URL`, `DB_USERNAME` y `DB_PASSWORD`. |

Los valores son puertos del servicio de destino. Los puertos de origen pueden ser
efímeros y la respuesta regresa por la misma conexión. JDBC es la API usada por Java;
en la red se utiliza el protocolo PostgreSQL.

## Artefactos y relación con las cuatro capas

| Capa lógica | Ubicación de ejecución |
|---|---|
| Presentación web | Navegador; los recursos se sirven desde el JAR. |
| Controladores REST | Proceso Spring Boot del nodo de aplicación. |
| Servicios y dominio | Proceso Spring Boot del nodo de aplicación. |
| Adaptadores y Spring Data JPA | Proceso Spring Boot del nodo de aplicación. |
| Persistencia relacional | PostgreSQL del nodo de datos. |

El artefacto desplegable es `agrovalle-connect-0.0.1-SNAPSHOT.jar`. La aplicación incluye
las páginas públicas y por rol, controladores, servicios, puertos y adaptadores. Cuatro
capas lógicas no significan cuatro servidores: varias capas comparten la JVM.

El modelo D02 define el producto objetivo de 17 tablas y 3 vistas. El esquema ejecutable
del incremento en `src/main/resources/schema.sql` materializa por ahora cinco tablas. Esta
distinción evita presentar como implementados los módulos futuros de pedidos, logística,
contacto persistente e idempotencia.

## Hoja 02: entorno local

En desarrollo, navegador, aplicación y PostgreSQL pueden ejecutarse en el mismo PC. La
aplicación usa `http://localhost:8081/` y se conecta a la base predeterminada
`agrovalle_connect` en el puerto 5432. Los valores pueden cambiar mediante variables de
entorno sin modificar el código.

`spring.jpa.hibernate.ddl-auto=validate` impide iniciar con un esquema incompatible y
`spring.sql.init.mode=always` aplica el DDL incremental antes de la validación. En pruebas se
usa una base H2 efímera configurada para compatibilidad PostgreSQL; esto no sustituye la
persistencia PostgreSQL de la ejecución normal.

Para probar desde otro dispositivo de la red, `localhost` no sirve porque identifica el
propio dispositivo. Esa demostración exige una dirección accesible y una regla de red, y no
forma parte de D04.

## Pendiente para un despliegue remoto

La topología propuesta aún requiere un servidor, DNS, certificado TLS, configuración de
NGINX, secretos de entorno, PostgreSQL 15/16 accesible solo desde la aplicación, copias de
seguridad y reglas de red. También se deberá aplicar una migración controlada desde el
esquema incremental hacia las entidades futuras de D02.

D04 documenta la arquitectura de despliegue; no afirma que esos recursos remotos ya estén
aprovisionados. La verificación actual cubre la coherencia del diagrama con el código, el
arranque local y las pruebas automatizadas del proyecto.

## Fuentes de la entrega

- Guía práctica del bloque 02: Java 17 LTS, PostgreSQL 15/16, tres nodos y puertos.
- Guía del Sprint de diseño y arquitectura: MVC en cuatro capas y topología UML.
- `pom.xml`, `application.properties`, `schema.sql` y adaptadores JPA del repositorio.
- D02 y D03: esquema relacional objetivo y distribución de responsabilidades.
