# D04 · Explicación del despliegue

## Objetivo y estado

Mostrar dónde se ejecutarían las partes de AgroValle Connect y cómo se comunicarían. La transcripción de clase solicita identificar cliente, servidor de aplicación y servidor de base de datos, incluyendo protocolos y puertos.

La hoja 01 es una **propuesta de despliegue** con tres equipos. La hoja 02 representa el entorno de desarrollo a partir del código y separa las funciones existentes de las conexiones pendientes. Ninguna hoja acredita que se haya instalado una infraestructura remota ni que se hayan comprobado procesos activos en el PC.

Base revisada: rama `chore/sprint-0-completo`, commit `f7a968b58750467362d66829767b1be520a82df5`. El proyecto configura Java 25, Spring Boot 3.5.16 y `server.port=${PORT:8081}`. Los cuatro repositorios de Agricultor, Finca, Cosecha y Contacto usan memoria. No hay datasource JPA configurado en el archivo de propiedades revisado.

## Hoja 01: los tres nodos

| Nodo | Entorno de ejecución | Artefactos y responsabilidades |
|---|---|---|
| 1. Cliente | Navegador en un PC, celular o tableta. | Interpreta una copia de HTML/CSS/JavaScript recibida del servidor; presenta la interfaz y envía peticiones a la API. |
| 2. Servidor de aplicación | Servidor Linux propuesto con dos procesos: NGINX y JVM Java 25 con Spring Boot/Tomcat. | NGINX recibe HTTPS. El JAR sirve recursos estáticos y ejecuta controladores, servicios y acceso a datos. El acceso JPA corresponde al diseño D03 y sigue por implementar. |
| 3. Servidor de datos | PostgreSQL 18 propuesto, en una red privada accesible por la aplicación. | Aloja la base propuesta `agrovalle_db`, el esquema `agrovalle` y las 17 tablas/3 vistas definidas en D02. Conserva datos, índices y restricciones. |

Se propone colocar NGINX en el mismo servidor de aplicación, por lo que **no se añade un cuarto equipo**. La selección de Linux, NGINX y PostgreSQL 18 es una decisión de diseño, no un requisito de versión o proveedor atribuido a la profesora.

La aplicación conserva la vista estática y la API en el mismo servidor. Los archivos HTML/CSS/JavaScript del cliente son la copia recibida por el navegador, no una segunda aplicación backend desplegada en ese dispositivo.

## Protocolos y puertos

| Comunicación | Protocolo | Puerto de destino | Alcance y estado |
|---|---|---|---|
| Navegador → NGINX | HTTP protegido por TLS, es decir, HTTPS sobre TCP. | 443 | Acceso público propuesto para interfaz y API. Requiere DNS y certificado válidos. |
| NGINX → Spring Boot | HTTP sobre TCP. | `127.0.0.1:8081` | Salto interno propuesto, dentro del servidor de aplicación. TLS termina en NGINX. |
| Aplicación → PostgreSQL remoto | Protocolo PostgreSQL protegido por TLS sobre TCP; Java accede mediante el driver JDBC. | 5432 | Conexión privada propuesta; configurar certificado de BD y verificación de identidad. |
| Navegador → Spring Boot local | HTTP sobre TCP. | 8081 | Configuración del prototipo, con URL de desarrollo `http://localhost:8081/`. |
| Aplicación → PostgreSQL local | Protocolo PostgreSQL sobre TCP, mediante JDBC. | 5432 | Integración prevista; datasource y mapeos aún por conectar. La hoja local no afirma que ya exista esa comunicación. |

Los números indicados son puertos **del servicio de destino**. Los puertos de origen del navegador o del cliente de BD pueden ser efímeros. Las respuestas regresan por la misma conexión; no requieren dibujar un puerto adicional de respuesta.

**JDBC es una API de acceso desde Java, no el protocolo que viaja por la red.** El driver se comunica con el servidor usando el protocolo PostgreSQL. El puerto 5432 es el predeterminado del motor y puede configurarse de otra forma si el entorno lo exige.

El salto HTTP entre NGINX y Spring Boot es local al mismo servidor. Si se separaran en equipos diferentes, habría que actualizar la topología y el cifrado de ese enlace. El objetivo remoto de esta entrega mantiene ambos procesos en el mismo host.

## Entornos de ejecución y artefactos

| Elemento del diagrama | Interpretación |
|---|---|
| Caja con volumen y `«device»` | Equipo que puede alojar software: cliente, aplicación o base de datos. |
| Caja interna `«executionEnvironment»` | Software de ejecución dentro de ese equipo: navegador, NGINX, JVM/Tomcat o PostgreSQL. |
| Documento con esquina plegada y `«artifact»` | Unidad desplegada o representación física de datos: recursos web, JAR o esquema de BD. |
| Contención de un artefacto en un entorno | Ubicación donde se aloja o utiliza ese artefacto. |
| Línea continua entre entornos | Camino de comunicación, rotulado con protocolo y puerto. |
| Dependencia discontinua de la hoja local | Integración de datos prevista; no se presenta como conexión existente. |

El artefacto `agrovalle-connect-0.0.1-SNAPSHOT.jar` usa el nombre derivado de `artifactId` y versión de Maven. En la hoja propuesta representa el paquete objetivo que incorporará la integración D03. No se entrega ni se afirma haber construido aquí un nuevo ejecutable del backend.

En PostgreSQL se dibuja el esquema aplicado y sus datos como artefacto persistente. `D02-esquema-postgresql.sql` es el archivo fuente que permite crear esas estructuras; no se confunde el archivo SQL con un proceso del servidor.

## Relación con las cuatro capas de D03

| Capa lógica | Ubicación de ejecución |
|---|---|
| Presentación: vista | Navegador del nodo 1; los recursos se sirven desde el JAR del nodo 2. |
| Presentación: controladores REST | Aplicación Spring Boot del nodo 2. |
| Lógica de negocio y dominio | Aplicación Spring Boot del nodo 2. |
| Acceso a datos: adaptadores y JPA/Hibernate | Aplicación Spring Boot del nodo 2. |
| Persistencia relacional | PostgreSQL del nodo 3. |

**Cuatro capas lógicas no significan cuatro servidores.** En el diseño propuesto, varias capas comparten el proceso Java. El navegador no accede directamente a PostgreSQL.

## Hoja 02: desarrollo local

En desarrollo, navegador y aplicación pueden ejecutarse en el PC Windows. El prototipo usa el puerto 8081 y repositorios temporales, que pierden sus datos al reiniciar. La dependencia a PostgreSQL se muestra como pendiente.

La base `agrovalle_db` es un **nombre propuesto**, distinto del nombre del esquema `agrovalle`. No se afirma que esa base ya exista, ni se reutiliza o modifica ninguna otra base de los trabajos de clase. pgAdmin es una herramienta de administración, no un servidor adicional que forme parte del flujo entre el navegador y la API.

La URL local `localhost` permite probar desde el mismo PC. Para un celular distinto, `localhost` identifica el propio celular, no el PC; una prueba desde otro equipo requeriría configurar dirección de red y acceso. Este ajuste no se realiza como parte de D04.

## Configuración pendiente para materializar la propuesta

El despliegue remoto necesitará configurar DNS/certificados y NGINX, fijar el proceso Spring Boot en loopback para su salto interno, conectar el datasource y aplicar D02 a la base elegida. También deberá completar la integración JPA de D03.

El código actual fija el puerto pero no define `server.address`; el alcance de escucha en loopback de la hoja remota es una decisión futura, no una configuración que ya se haya aplicado. Las credenciales y certificados se proporcionarán mediante configuración del entorno. La conexión remota a PostgreSQL deberá validar certificado y nombre del servidor, por ejemplo con `sslmode=verify-full` al configurar el driver.

La red prevista permite el acceso del servidor de aplicación al puerto de BD; la base de datos y el puerto interno 8081 no se plantean como interfaces públicas. Esto define el alcance de los enlaces del dibujo; todavía no se han aplicado reglas de red.

## Alcance de la comprobación

Se revisan la legibilidad, las relaciones de contención del archivo editable, la correspondencia del puerto/JAR/versiones con el código y la coherencia con D02/D03. No se realizan instalaciones, cambios de configuración, pruebas de conectividad ni publicaciones del sistema. Las pruebas de arranque y de conexión corresponderán al trabajo técnico posterior.

## Fuentes

- Transcripción de clase compartida: topología con cliente, servidor de aplicación y servidor de BD; protocolos y puertos visibles.
- [Código revisado](https://github.com/JuanFiligrana/Agrovalle-Conect/tree/f7a968b58750467362d66829767b1be520a82df5): `pom.xml`, `application.properties` y repositorios del prototipo.
- D02 y D03 del Sprint de diseño: esquema relacional, mapeos y distribución de responsabilidades.
- [OMG: UML 2.5.1](https://www.omg.org/spec/UML/2.5.1), referencia general de notación UML.
- [Spring Boot 3.5: propiedades de aplicación](https://docs.spring.io/spring-boot/3.5/appendix/application-properties/index.html), configuración de servidor y datasource.
- [PostgreSQL 18: conexiones](https://www.postgresql.org/docs/18/runtime-config-connection.html), puerto de escucha.
- [pgJDBC: SSL](https://jdbc.postgresql.org/documentation/ssl/), verificación TLS en el cliente Java.
- [NGINX: HTTPS](https://nginx.org/en/docs/http/configuring_https_servers.html) y [proxy HTTP](https://nginx.org/en/docs/http/ngx_http_proxy_module.html), base de la opción de proxy propuesta.
