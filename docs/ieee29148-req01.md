# Especificación formal y comparación BDD de la HU-01

## 1. Identificación y propósito

- Proyecto: AgroValle Connect.
- Historia: HU-01 Registro de agricultores.
- Tarea relacionada: T02.
- Prioridad: Must Have.
- Estimación: 5 Story Points.
- Referencia para la especificación formal: ISO/IEC/IEEE 29148:2018.
- Fuente del requerimiento: HU-01 y escenarios BDD de BACKLOG.md.

Este documento expresa el registro de agricultores mediante requisitos identificables y verificables y los compara con su especificación ágil BDD. Complementa el backlog y establece el comportamiento esperado; no certifica que la funcionalidad esté completamente implementada.

## 2. Necesidad del usuario y alcance

Como agricultor, quiero registrarme en la plataforma para ofrecer mis productos.

El registro permite crear una cuenta de agricultor con datos válidos y únicos. Incluye validación de datos, protección de la contraseña, persistencia en PostgreSQL y respuestas de éxito o error.

El inicio de sesión corresponde a la HU-07. La publicación de cosechas corresponde a la HU-02 y no forma parte de esta operación.

## 3. Actores e interfaz

- Actor principal: persona que desea registrarse como agricultor.
- Sistema responsable: API de AgroValle Connect.
- Sistema de persistencia: PostgreSQL.
- Operación: POST /api/v1/auth/register.
- Formato de intercambio: JSON.
- Datos de entrada: nombre, correo, contraseña, cédula, ubicación en el Valle del Cauca y rol AGRICULTOR.

La solicitud de registro no requiere una cuenta previamente autenticada.

## 4. Requisitos funcionales

| ID | Requisito | Forma de verificación |
|---|---|---|
| RF-HU01-01 | El sistema deberá recibir una solicitud de registro mediante POST /api/v1/auth/register con los datos definidos en la interfaz. | Prueba de solicitud válida al endpoint. |
| RF-HU01-02 | El sistema deberá rechazar solicitudes con campos obligatorios ausentes o vacíos, correo con formato inválido o municipio fuera del catálogo permitido del Valle del Cauca. | Pruebas de datos inválidos y respuesta 400. |
| RF-HU01-03 | El sistema deberá impedir la creación de cuentas con correo o cédula ya registrados. | Pruebas independientes de correo duplicado y cédula duplicada; respuesta 409. |
| RF-HU01-04 | El sistema deberá crear la cuenta con el rol AGRICULTOR e impedir que esta operación otorgue roles diferentes. | Prueba de registro válido y rechazo de rol no permitido. |
| RF-HU01-05 | El sistema deberá almacenar la cuenta válida en PostgreSQL y asignarle un identificador único. | Prueba de integración que consulte el registro persistido. |
| RF-HU01-06 | El sistema deberá responder 201 Created con el identificador de la cuenta cuando el registro termine correctamente. | Verificación del estado HTTP y del contenido de la respuesta. |
| RF-HU01-07 | El sistema deberá responder 400 Bad Request ante datos inválidos y 409 Conflict ante duplicados, sin crear una cuenta adicional. | Verificación de respuestas y ausencia de inserciones indebidas. |

## 5. Requisitos de seguridad e integridad

| ID | Requisito | Forma de verificación |
|---|---|---|
| RC-HU01-01 | La contraseña deberá almacenarse mediante hash BCrypt y nunca en texto plano. | Verificar que el hash sea diferente de la contraseña y que BCrypt permita comprobarla. |
| RC-HU01-02 | La respuesta de la API no deberá incluir la contraseña ni su hash. | Inspección del JSON de éxito y de error. |
| RC-HU01-03 | PostgreSQL deberá mantener restricciones de unicidad para correo y cédula, incluso ante solicitudes concurrentes. | Prueba de integración y comprobación de las restricciones. |
| RC-HU01-04 | Un fallo durante la persistencia no deberá dejar una cuenta parcialmente registrada ni producir una respuesta de éxito. | Prueba de fallo y comprobación del estado de la base de datos. |
| RC-HU01-05 | Las credenciales de PostgreSQL deberán configurarse mediante variables de entorno y no incorporarse al repositorio. | Revisión de configuración y archivos versionados. |

Los requisitos de seguridad complementan la necesidad funcional: crear una cuenta no es suficiente si sus datos o credenciales quedan expuestos.

## 6. Flujo principal

1. El agricultor envía sus datos de registro.
2. El sistema valida los campos y el rol solicitado.
3. El sistema comprueba la unicidad del correo y la cédula.
4. El sistema genera el hash de la contraseña.
5. El sistema persiste la cuenta en PostgreSQL.
6. El sistema devuelve 201 Created con el identificador, sin datos secretos.

### Flujos alternativos

- Datos inválidos: responder 400 sin persistir una cuenta.
- Correo o cédula duplicados: responder 409 sin crear otra cuenta.
- Fallo de persistencia: informar el error sin devolver 201 y sin dejar datos parciales.

### Poscondiciones de éxito

La cuenta existe en PostgreSQL, tiene un identificador único y rol AGRICULTOR, y su contraseña está protegida mediante hash.

## 7. Especificación ágil BDD

Los siguientes escenarios conservan el comportamiento del backlog y hacen explícita la verificación de ambos campos únicos.

```gherkin
Característica: Registro de agricultores

  Escenario: Registro válido
    Dado que no existe una cuenta con el correo ni la cédula enviados
    Cuando se solicita el registro con datos válidos y rol AGRICULTOR
    Entonces la API responde 201 Created con el identificador
    Y la cuenta queda almacenada en PostgreSQL
    Y la contraseña queda protegida mediante hash BCrypt
    Y la respuesta no contiene la contraseña ni su hash

  Escenario: Correo duplicado
    Dado que el correo enviado pertenece a una cuenta existente
    Cuando se solicita otro registro con ese correo
    Entonces la API responde 409 Conflict
    Y no crea otra cuenta

  Escenario: Cédula duplicada
    Dado que la cédula enviada pertenece a una cuenta existente
    Cuando se solicita otro registro con esa cédula
    Entonces la API responde 409 Conflict
    Y no crea otra cuenta

  Escenario: Datos inválidos
    Dado que falta un campo obligatorio o el municipio no está permitido
    Cuando se solicita el registro
    Entonces la API responde 400 Bad Request
    Y informa los campos inválidos
    Y no crea una cuenta
```

## 8. Matriz comparativa

| Aspecto | Especificación formal con referencia a ISO/IEC/IEEE 29148:2018 | Especificación ágil BDD |
|---|---|---|
| Propósito | Precisar obligaciones, restricciones y formas de verificación. | Explicar el comportamiento mediante ejemplos comprensibles. |
| Organización | Requisitos identificados, alcance, interfaces, flujos y condiciones. | Historia de usuario y escenarios Dado, Cuando y Entonces. |
| Nivel de detalle | Detalla datos, respuestas, integridad, seguridad y condiciones de aceptación. | Describe situaciones concretas de éxito y error. |
| Lenguaje | Declaraciones explícitas del tipo “El sistema deberá”. | Lenguaje orientado al comportamiento observable. |
| Trazabilidad | Cada requisito tiene un identificador asociado a verificaciones. | Cada escenario se vincula con una historia y puede asociarse a pruebas. |
| Verificación | Define cómo comprobar cada obligación del sistema. | Los escenarios pueden convertirse en pruebas automatizadas. |
| Cambios | Permite identificar qué requisitos y verificaciones deben actualizarse. | Permite ajustar ejemplos al aclarar las necesidades del usuario. |
| Aplicación en HU-01 | Especifica registro, unicidad, persistencia y protección de credenciales. | Ejemplifica registro válido, duplicados y datos inválidos. |

Ambos enfoques son complementarios. La especificación formal detalla las obligaciones y restricciones; BDD facilita discutirlas y comprobarlas mediante ejemplos. Usar Gherkin no significa que los escenarios ya estén automatizados.

## 9. Trazabilidad y aceptación

| Comportamiento | Requisitos relacionados | Evidencia requerida |
|---|---|---|
| Registro válido | RF-HU01-01, RF-HU01-04, RF-HU01-05, RF-HU01-06 | Prueba de API y consulta del registro en PostgreSQL. |
| Datos inválidos | RF-HU01-02, RF-HU01-07 | Respuesta 400 y ausencia de inserción. |
| Correo o cédula duplicados | RF-HU01-03, RF-HU01-07, RC-HU01-03 | Respuesta 409 y conservación de la unicidad. |
| Protección de credenciales | RC-HU01-01, RC-HU01-02, RC-HU01-05 | Hash verificable, JSON sin secretos y configuración revisada. |
| Fallo de persistencia | RC-HU01-04 | Prueba de fallo sin registro parcial ni respuesta de éxito. |

Para aceptar la HU-01 deberán cumplirse estos requisitos y el Definition of Done del proyecto, con pruebas ejecutadas y revisión de otro integrante.

## 10. Referencias

- BACKLOG.md: HU-01 Registro de agricultores.
- Planificación del Sprint 1: tarea T02.
- docs/dod.md: Definition of Done.
- ISO/IEC/IEEE 29148:2018: Systems and software engineering — Life cycle processes — Requirements engineering.
