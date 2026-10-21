# HU-08 — Registro persistente de fincas

## Contrato

`POST /api/v1/fincas` exige `Authorization: Bearer <token>` y recibe:

```json
{
  "nombre": "La Esperanza",
  "municipio": "Dagua",
  "descripcion": "Producción familiar"
}
```

El identificador del agricultor no se recibe como dato confiable del cuerpo. La API lo
obtiene del JWT firmado y verifica que el rol sea `AGRICULTOR`. Un comprador autenticado
recibe `403 Forbidden`; un token ausente, alterado o vencido recibe `401 Unauthorized`.

`GET /api/v1/fincas` devuelve solamente las fincas del agricultor autenticado.

## Persistencia

La tabla `fincas` conserva nombre, municipio, descripción y la clave foránea
`agricultor_id` hacia `usuarios`. La restricción referencial impide guardar una finca
para un usuario inexistente. El municipio se valida con el catálogo de los 42 municipios
del Valle del Cauca y se guarda con su nombre canónico.

## Verificación

`RegistroFincaAcceptanceTests` cubre registro válido, propietario derivado del token,
persistencia y consulta, ausencia de token, rol incorrecto y municipio inválido.
