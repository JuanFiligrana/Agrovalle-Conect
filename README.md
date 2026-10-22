# AgroValle Connect

[![Java CI](https://github.com/JuanFiligrana/Agrovalle-Conect/actions/workflows/ci.yml/badge.svg)](https://github.com/JuanFiligrana/Agrovalle-Conect/actions/workflows/ci.yml)
![Java 25](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.16-green)

![Logo Automatic](assets/logo-automatic.jpeg)

## Visión del producto
Para los productores del Valle del Cauca que necesitan vender directamente sus cosechas, AgroValle Connect es una plataforma web empresarial en Java 25 y Spring Boot que conecta la oferta agrícola con compradores y comercios de Cali y sus alrededores. A diferencia de la intermediación tradicional, el producto propone información transparente sobre origen, precio y disponibilidad, junto con trazabilidad de los pedidos mediante contratos de API verificables.


## Integrantes
- Juan Eduardo Filigrana Mindinero
- Michael David Caicedo Mina
- Juan Camilo Álvarez
- Jhojan Aragón

## Alcance de esta entrega
El Sprint 2 incorpora HU-02 y HU-04, con 8 puntos en total, sobre el flujo seguro del
Sprint 1. Un agricultor autenticado publica una cosecha en una finca propia; la oferta
se persiste en PostgreSQL y puede consultarse con filtros combinados de municipio y
categoría. El incremento también demuestra Strategy para el cálculo de flete y Observer
para avisos de publicación. Alcance, criterios y trazabilidad se detallan en
[Sprint 2](docs/sprint2.md).

### Rutas del incremento

- `POST /api/v1/auth/register`: registra un agricultor o comprador.
- `POST /api/v1/auth/login`: valida correo y contraseña y devuelve un JWT.
- `GET /api/v1/auth/me`: comprueba un JWT y devuelve la identidad autenticada.
- `POST /api/v1/fincas`: crea una finca para el agricultor autenticado.
- `GET /api/v1/fincas`: lista las fincas del agricultor autenticado.
- `POST /api/v1/productos`: publica una cosecha persistente; exige JWT de agricultor.
- `GET /api/v1/productos?municipio=&categoria=`: filtra ofertas activas.
- `GET /api/v1/notificaciones/cosechas`: consulta avisos propios generados por Observer.
- `GET /api/v1/fletes/calculo?municipio=&subtotal=`: calcula el flete mediante Strategy.

### Funciones heredadas aún fuera del alcance terminado

- `GET /api/v1/precios/promedio?producto=`: calcula el promedio en COP/kg.
- `POST /api/v1/contacto/mensaje`: registra una solicitud de contacto con
  encabezado `Authorization`.

## Ejecutar en Windows
1. Extraer el ZIP y abrir esta carpeta completa, donde está pom.xml.
2. Ejecutar `iniciar.cmd`. Busca un JDK 25 ya instalado en ubicaciones comunes o JAVA_HOME,
   lo selecciona para esa ejecución y muestra la dirección a abrir.
3. Al ver `Started AgrovalleApplication`, abrir http://localhost:8081/.
4. Conservar la terminal abierta. Ctrl+C detiene la aplicación.

Si el puerto 8081 está ocupado, ejecutar `iniciar.cmd 8082` y abrir localhost:8082.
Si solo está instalado otro Java, instalar JDK 25 desde una distribución oficial.
El iniciador no desinstala Java ni cambia variables globales.
Maven Enforcer exige JDK 25: compilar con target 25 usando otra JVM no sustituye ese requisito.

### Comandos directos
Con JAVA_HOME apuntando al JDK 25, en PowerShell:
```powershell
.\mvnw.cmd clean verify
.\mvnw.cmd spring-boot:run
```
En Linux o Git Bash: `sh ./mvnw clean verify` y `sh ./mvnw spring-boot:run`.
El Wrapper descarga Maven 3.9.9; la primera ejecución requiere Internet.

## Calidad local
Con Git, Node.js 18 o posterior y JDK 25 instalados, dentro del repositorio clonado:
```powershell
npm.cmd ci
git config --get core.hooksPath
```
El resultado esperado es `.husky/_`. En una carpeta sin .git, primero hay que integrar
los archivos al repositorio siguiendo [la guía de GitHub](docs/github.md).
El hook ejecuta `sh ./mvnw -B test checkstyle:check`. Checkstyle también corre en validate.
Husky detecta fallos antes del commit y Checkstyle impone un estilo uniforme; así se
reduce el trabajo de corrección que se acumularía como deuda técnica.
`verify` agrega empaquetado y el umbral JaCoCo de 60 %; reporte en target/site/jacoco/index.html.

## Estrategia de ramas
El equipo propone Trunk-Based Development con ramas de tarea cortas y Pull Requests hacia main. Para cuatro integrantes y una sola versión académica activa, reduce las ramas de integración que deben mantenerse. Las tareas pequeñas, la sincronización frecuente y la revisión de un compañero antes del merge reducen esperas y conflictos. No se programa directamente sobre main después de la inicialización. La elección no elimina por sí sola los conflictos: requiere disciplina y controles de integración.

```mermaid
gitGraph
    commit id: "chore: base del proyecto"
    branch chore/calidad
    checkout chore/calidad
    commit id: "build: configurar calidad"
    checkout main
    merge chore/calidad id: "PR revisado"
    branch docs/backlog
    checkout docs/backlog
    commit id: "docs: especificar BDD"
    checkout main
    merge docs/backlog id: "PR aprobado"
```

El diagrama representa el flujo acordado, no pruebas de PR ya aprobados. Las ramas de
funcionalidad se nombran `feature/HU-XX-descripcion`; documentación usa `docs/` y tareas
iniciales `chore/`. Las mejoras se integran mediante PR, no mediante push directo a main.

## Conventional Commits
- `chore: inicializar estructura del proyecto`
- `build: configurar Maven y controles de calidad`
- `test: comprobar estado y portada web`
- `docs: especificar backlog y contrato de calidad`
- `feat: implementar historias y patrones del Sprint 2`

## Contrato y documentos
- [BACKLOG.md](BACKLOG.md): 15 historias, 40 escenarios y 76 puntos propuestos.
- [Definition of Done](docs/dod.md): checklist y aceptación por los cuatro integrantes.
- [Estimación](docs/estimacion.md): procedimiento y registro de Planning Poker.
- [Arquitectura](docs/arquitectura.md): capas y límites de los incrementos.
- [PostgreSQL para T01](docs/postgresql-t01.md): creación, variables y comprobación.
- [Autenticación JWT de HU-07](docs/jwt-hu07.md): configuración y contratos seguros.
- [Persistencia de fincas de HU-08](docs/fincas-hu08.md): propiedad y pruebas.
- [Incremento del Sprint 2](docs/sprint2.md): HU-02, HU-04 y demostración REST.
- [Especificación 29148 del Sprint 2](docs/especificacion-sprint2.md): requisitos y BDD.
- [SOLID y patrones](docs/patrones-sprint2.md): decisiones Strategy y Observer.
- [Ceremonias](docs/ceremonias-sprint2.md): Daily, Review y Retrospective en tercera persona.
- [Integración en GitHub](docs/github.md): ramas, commits, PR, revisión y checks.
- [Verificación](docs/verificacion.md): resultados comprobados y límites.
- [Matriz de requisitos](docs/matriz-requisitos.md): correspondencia con las guías.

## Integración continua
El workflow `.github/workflows/ci.yml` ejecuta `verify` con Java 25 en pushes a main
y en Pull Requests hacia main. Checkstyle, JUnit y el umbral JaCoCo de 60 % forman una
sola barrera; el reporte HTML de JaCoCo se conserva como artefacto de cada ejecución.
El badge refleja el último resultado remoto. Un workflow por sí solo no bloquea merges
si la protección de la rama no exige el check `build`.

## Estructura
- src/main/java/co/edu/uniajc/agrovalle/models: contratos de datos.
- src/main/java/co/edu/uniajc/agrovalle/controllers: controladores REST.
- src/main/java/co/edu/uniajc/agrovalle/repositories/jpa: entidades y acceso relacional.
- src/main/java/co/edu/uniajc/agrovalle/strategies: algoritmos intercambiables de flete.
- src/main/java/co/edu/uniajc/agrovalle/events: eventos desacoplados del dominio.
- src/main/resources/static: vista web.
- src/main/views: documentación de la capa de vistas.
- src/test/java: pruebas automatizadas.
- docs: decisiones, calidad y evidencias.

## Fuentes
Guías oficiales de Paola Andrea Bedoya Toro: Proyecto integrador AgroValle Connect;
Guía de Trabajo Práctico V2; Flujo de Trabajo Profesional en Git y GitHub.
[Spring Boot](https://docs.spring.io/spring-boot/3.5/system-requirements.html),
[Husky](https://typicode.github.io/husky/get-started.html),
[Checkstyle Google Java Style](https://github.com/checkstyle/checkstyle/blob/checkstyle-10.21.4/src/main/resources/google_checks.xml).
