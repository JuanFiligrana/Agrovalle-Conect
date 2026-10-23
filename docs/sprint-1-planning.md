# Sprint 1 corregido

## Objetivo

Entregar el primer flujo seguro y persistente de AgroValle Connect: un agricultor crea
su cuenta, inicia sesión y registra una finca propia. El alcance comprometido suma 13
Story Points y se ejecuta en el orden HU-01, HU-07 y HU-08.

## Historias comprometidas

| Orden | Historia | Puntos | Resultado |
|---|---|---:|---|
| 1 | HU-01 Registro de agricultores | 5 | Cuenta persistida con BCrypt |
| 2 | HU-07 Inicio de sesión seguro | 5 | JWT firmado y con expiración |
| 3 | HU-08 Registro de fincas | 3 | Finca persistida para el dueño del JWT |

## Tareas técnicas integradas

| Tarea | Historia | Implementación principal |
|---|---|---|
| T02 | HU-01 | `POST /api/v1/auth/register` |
| T03 | HU-07 | `POST /api/v1/auth/login` y `GET /api/v1/auth/me` |
| T04 | HU-08 | `POST` y `GET /api/v1/fincas` protegidos |

T01 proporciona la base PostgreSQL y la persistencia de usuarios requerida por T02.
Las tareas T06, T09, T11 y T14 permanecen fuera de este Sprint corregido.

## Flujo demostrable

1. El agricultor se registra con datos válidos del Valle del Cauca.
2. La API guarda solamente el hash BCrypt de la contraseña.
3. El agricultor inicia sesión con correo y contraseña.
4. La API entrega un JWT firmado con identificador, rol y expiración.
5. El agricultor registra una finca enviando el JWT como Bearer token.
6. La API obtiene el propietario del token y persiste la finca en PostgreSQL.

El cuerpo de la finca no acepta al cliente como fuente de identidad. Aunque se envíe un
campo `agricultorId`, la propiedad se deriva exclusivamente del JWT verificado.

## Criterios de calidad

- JDK 17 LTS y Maven Wrapper.
- Checkstyle sin infracciones.
- Pruebas BDD de éxito, validación, duplicados, credenciales y tokens inválidos.
- Umbral JaCoCo igual o superior al 60 % en instrucciones, ramas y líneas.
- PostgreSQL en ejecución normal y H2 compatible con PostgreSQL en pruebas repetibles.
- Claves y credenciales suministradas mediante variables de entorno.
- Pull Request revisado por otro integrante antes de fusionar.

## Ejecución

Configurar PostgreSQL y `JWT_SECRET` según `postgresql-t01.md` y `jwt-hu07.md`. Con JDK
25, ejecutar `mvnw.cmd clean verify` en Windows o `sh ./mvnw clean verify` en Linux.
