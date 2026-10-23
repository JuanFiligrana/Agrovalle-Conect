# HU-07 — Inicio de sesión seguro con JWT

## Contratos

- `POST /api/v1/auth/login` recibe `correo` y `contrasena`.
- Una combinación válida devuelve `accessToken`, `tokenType` y `expiresInSeconds`.
- Las credenciales incorrectas devuelven `401 Unauthorized` sin indicar cuál dato falló.
- `GET /api/v1/auth/me` exige `Authorization: Bearer <token>`.
- Un token ausente, alterado o vencido devuelve `401 Unauthorized`.

La contraseña se compara con el hash BCrypt almacenado. El JWT contiene únicamente el
identificador, correo y rol del usuario; nunca contiene la contraseña ni su hash.

## Configuración segura

La firma HMAC usa una clave Base64 de al menos 32 bytes suministrada por el entorno.
No se debe guardar una clave real en Git, capturas, documentos ni mensajes.

En PowerShell se puede generar y usar una clave temporal para la terminal actual:

```powershell
$bytes = New-Object byte[] 32
[Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
$env:JWT_EXPIRATION_SECONDS = "3600"
```

En Linux o Git Bash:

```bash
export JWT_SECRET="$(openssl rand -base64 32)"
export JWT_EXPIRATION_SECONDS="3600"
```

La aplicación no arranca si `JWT_SECRET` está ausente. Las pruebas usan una clave fija
exclusiva del entorno H2; esa clave no debe reutilizarse fuera de las pruebas.

## Verificación

`InicioSesionAcceptanceTests` cubre credenciales válidas, contraseña incorrecta, token
alterado y token vencido. El flujo completo se verifica con `mvnw clean verify` en JDK 17 LTS.
