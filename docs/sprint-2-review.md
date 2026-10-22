# Sprint Review · Sprint 2

## Objetivo revisado

El desarrollador presenta HU-02 y HU-04 como un incremento de 8 Story Points que permite
publicar cosechas persistentes y consultarlas por municipio y categoría.

## Demostración técnica

1. El desarrollador registra un agricultor e inicia sesión desde Postman.
2. Crea una finca en Dagua con el JWT recibido.
3. Publica Mango, categoría Frutas, con fecha futura, cantidad y precio positivos.
4. Verifica la respuesta 201 y el propietario derivado del JWT.
5. Consulta `municipio=Dagua&categoria=Frutas` y obtiene la única coincidencia activa.
6. Consulta el aviso generado por Observer para el agricultor.
7. Calcula COP 18.000 de flete intermedio sobre un subtotal de COP 100.000.
8. Muestra el PR, la ejecución `build` y el artefacto JaCoCo.

La colección `docs/postman/AgroValle-Sprint2.postman_collection.json` prepara variables y
captura token e identificadores para ejecutar el flujo en orden.

## Criterios de aceptación

| Resultado | Evidencia |
|---|---|
| Publicación segura y persistente | MockMvc, servicio y tablas JPA |
| Filtro combinado y catálogos | Pruebas unitarias y REST |
| Strategy comprobable | Tres algoritmos y pruebas |
| Observer desacoplado | Evento, listener y consulta autenticada |
| Calidad automática | Ejecución 35: Checkstyle 0, 55 pruebas y JaCoCo 97,93 % de líneas |

El check remoto del PR #52 quedó en verde en la ejecución 35. El incremento se considera
técnicamente aceptado por la automatización; no se registra aprobación docente o de pares
sin un enlace real.
