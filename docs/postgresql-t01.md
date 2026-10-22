# T01 — PostgreSQL y persistencia de usuarios

## Alcance

T01 reemplaza el almacenamiento temporal de cuentas por una tabla `usuarios` en
PostgreSQL. Spring Data JPA asigna el identificador, consulta las cuentas y conserva
las restricciones únicas de correo y cédula. Las contraseñas continúan almacenándose
únicamente como hash BCrypt.

Los repositorios de fincas, cosechas y contactos siguen en memoria y pertenecen a
tareas posteriores.

## Preparar la base de datos

En pgAdmin, conectarse al servidor PostgreSQL local y ejecutar una sola vez:

```sql
CREATE DATABASE agrovalle_connect;
```

No es necesario crear manualmente la tabla. Al iniciar la aplicación, `schema.sql`
crea `usuarios` si todavía no existe y Hibernate comprueba que coincida con la entidad.

## Configurar las credenciales sin guardarlas en Git

Abrir PowerShell dentro de la carpeta del proyecto y definir las variables para esa
terminal:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/agrovalle_connect"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "ESCRIBE_AQUI_TU_CLAVE_DE_POSTGRESQL"
./iniciar.cmd
```

La clave no debe copiarse en `application.properties`, documentos, capturas ni commits.
Al cerrar PowerShell, estas variables dejan de existir.

## Comprobar el resultado

Después de registrar una cuenta mediante `POST /api/v1/auth/register`, ejecutar en el
Query Tool de pgAdmin:

```sql
SELECT id, nombre, correo, cedula, municipio, rol
FROM usuarios
ORDER BY id;
```

La consulta omite intencionalmente `contrasena_hash`. El endpoint tampoco devuelve la
contraseña ni su hash.

## Pruebas automáticas

`mvnw.cmd clean verify` utiliza H2 únicamente durante las pruebas. H2 se ejecuta en modo
PostgreSQL y valida el guardado, la consulta, la normalización del correo y el rechazo de
correos o cédulas duplicados. La ejecución normal de la aplicación usa PostgreSQL.
