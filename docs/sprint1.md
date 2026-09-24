# Sprint 1: prototipo de API

## Objetivo

Entregar un primer flujo demostrable de AgroValle Connect: registrar usuarios,
relacionar agricultores con sus fincas, publicar cosechas, consultar precios, filtrar
ofertas y registrar la intención de contacto de un comprador.

Esta versión es un prototipo; no acredita el cierre de las historias. Los resultados
y los criterios pendientes están en [Verificación](verificacion.md).

## Correspondencia con el tablero

| Tarea | Historia | Implementación |
|---|---|---|
| T02 | HU-01 | `POST /api/v1/auth/register` |
| T04 | HU-02 | `POST /api/v1/fincas` |
| T06 | HU-02 | `POST /api/v1/productos` |
| T09 | HU-03 | `GET /api/v1/precios/promedio` |
| T11 | HU-04 | `GET /api/v1/productos?municipio=&categoria=` |
| T14 | HU-05 | `POST /api/v1/contacto/mensaje` |

## Desglose técnico

- Controladores REST para cada contrato.
- DTO con validaciones de campos obligatorios, correo, cantidades y fechas.
- Servicios con reglas de propiedad de fincas, roles y ofertas activas.
- Repositorios en memoria para poder ejecutar una demostración sin credenciales.
- Pruebas MockMvc para flujos válidos y errores de duplicidad o autorización.

## Límites conocidos

Los repositorios son temporales y se vacían al reiniciar la aplicación. La cabecera
`Authorization` del contacto comprueba la presencia de una autorización, pero todavía
no valida un JWT firmado. La siguiente iteración debe conectar PostgreSQL, agregar
Spring Security/JWT y conservar los mismos contratos REST.

## Ejecución

Con JDK 25 configurado, ejecutar `mvnw.cmd clean verify` y luego `iniciar.cmd`. La
aplicación queda disponible en `http://localhost:8081/`.
