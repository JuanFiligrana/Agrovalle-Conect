# Contacto persistente (HU-05) y detalle de cosecha (HU-10)

## Alcance

El [PR #55](https://github.com/JuanFiligrana/Agrovalle-Conect/pull/55) incorpora un
incremento posterior al Sprint 2. Un comprador autenticado consulta una cosecha activa
y envía una solicitud al agricultor. El agricultor consulta sus mensajes y confirma
la lectura. No se reserva inventario ni se crea una orden al enviar un mensaje.

## Contrato HTTP

| Método y ruta | Acceso | Resultado |
|---|---|---|
| `GET /api/v1/productos/{id}` | Público | Detalle seguro de la cosecha activa; 404 si no existe o está inactiva. |
| `POST /api/v1/contacto/mensaje` | JWT de comprador | 200 con ID, estado y `notificacionRegistrada: true` después de persistir. |
| `GET /api/v1/contacto/mensajes` | JWT | Hasta 100 solicitudes donde el usuario es comprador o agricultor destinatario, más recientes primero. |
| `PATCH /api/v1/contacto/notificaciones/{id}/lectura` | JWT del destinatario | 204; repetir la operación mantiene el resultado. |

El cuerpo del envío es `{"productoId": 123, "mensaje": "¿Hay entrega el viernes?"}`.
También se acepta `id_producto` como alias. El servidor obtiene el comprador del JWT:
un `compradorId` enviado por el cliente no puede suplantar a otro usuario.
El mensaje debe contener texto y no superar 1000 caracteres. Se recortan espacios
en los extremos. Se devuelve 400 por datos inválidos, 401 por autenticación ausente
o inválida, 403 por rol o propietario incorrectos y 404 por recurso inexistente.

## Persistencia y privacidad

`interacciones_contacto` conserva comprador, cosecha, mensaje y fecha con zona horaria.
`notificaciones_contacto` vincula una notificación única al contacto y a su destinatario.
La creación de ambos registros es transaccional: un fallo revierte la operación.
Las claves foráneas mantienen las relaciones y el esquema es incremental, sin borrar
datos existentes al reiniciar. Los DTO públicos no devuelven correos, documentos ni hashes.

La bandeja solo devuelve mensajes de los participantes; únicamente el destinatario
puede marcar su notificación como leída. El navegador escapa el contenido del mensaje
al construir la interfaz. La sesión JWT continúa limitada a la pestaña.

## Uso desde la web

1. El agricultor registra una cuenta, una finca y una cosecha.
2. El comprador abre el catálogo y selecciona «Ver y contactar».
3. Inicia sesión como comprador y envía el formulario del detalle.
4. Abre «Mensajes» para comprobar la solicitud. El agricultor también la ve en su
   panel y puede marcarla como leída.

## Verificación

`ContactoServiceTests` comprueba roles, identidad, oferta activa, propagación de errores
y propiedad de las notificaciones. `ContactoAcceptanceTests` ejecuta registro → finca →
cosecha → catálogo → contacto → lectura, además de los casos de seguridad e invalidez.
`FlujoPostgresqlTests` utiliza HTTP real, comprueba que el motor es PostgreSQL y vuelve a
iniciar Spring Boot para verificar que cuentas, finca, cosecha y mensajes permanecen.

Con JDK 17 y dependencias disponibles:

```sh
sh ./mvnw -B verify
```

Para la segunda validación, usar exclusivamente una base PostgreSQL desechable de pruebas.
Configurar `DB_TEST_URL`, `DB_TEST_USERNAME`, `DB_TEST_PASSWORD` y
`AGROVALLE_TEST_POSTGRES=true`; ejecutar:

```sh
sh ./mvnw -B test -Dspring.profiles.active=ci-postgres
```

El workflow levanta esa base aislada y ejecuta ambos comandos. El test de reinicio se
omite expresamente en la primera pasada H2 y se habilita en la pasada PostgreSQL.
Los resultados efectivos y el SHA validado se consultan en los checks del PR #55;
la presencia de estos comandos no constituye evidencia de una ejecución aprobada.

## Límites

No incluye respuestas de chat, adjuntos, correo electrónico, paginación anterior a los
100 mensajes, carrito, reservas, pagos, pedidos ni logística. Los avisos Observer de
publicación son distintos de estas notificaciones de contacto. No se acredita una
revisión por pares ni una aprobación de la docente con este cambio.
