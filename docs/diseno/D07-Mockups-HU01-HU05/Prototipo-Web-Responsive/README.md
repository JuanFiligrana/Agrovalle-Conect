# Prototipo web responsive de alta fidelidad

Este complemento de D07 materializa la experiencia visual de AgroValle Connect como una
**aplicación web empresarial**. No reemplaza los ocho mockups trazables a HU01–HU05: los
integra en una navegación coherente y añade las vistas de contexto necesarias para explicar
el producto a un cliente.

## Vistas y público objetivo

| Ruta | Usuario principal | Objetivo visible |
|---|---|---|
| [`index.html`](../../../../src/main/resources/static/index.html) | Visitante o comprador | Descubrir cosechas, confiar en el origen y decidir explorar o comprar |
| [`catalogo.html`](../../../../src/main/resources/static/catalogo.html) | Comprador | Buscar por producto, municipio, categoría o productor y comparar ofertas |
| [`login.html`](../../../../src/main/resources/static/login.html) | Agricultor o comprador | Iniciar sesión o registrar un único perfil con campos explícitos |
| [`agricultor.html`](../../../../src/main/resources/static/agricultor.html) | Agricultor autenticado | Atender productos, mensajes, pedidos, entregas, ofertas y precios |
| [`registrar-finca.html`](../../../../src/main/resources/static/registrar-finca.html) | Agricultor autenticado | Registrar el origen que se asociará a sus publicaciones |
| [`publicar-cosecha.html`](../../../../src/main/resources/static/publicar-cosecha.html) | Agricultor autenticado | Publicar desde un formulario independiente con unidades inequívocas |
| [`seguimiento.html`](../../../../src/main/resources/static/seguimiento.html) | Comprador o agricultor | Consultar el avance y las novedades de una entrega |
| [`administrador.html`](../../../../src/main/resources/static/administrador.html) | Administrador | Gestionar usuarios, productos, pedidos, logística y reportes |

## Decisiones de experiencia de usuario

- La portada pública muestra productos y llamadas a la acción desde el primer bloque; no
  confunde al comprador con indicadores administrativos.
- Los rótulos describen acciones concretas: “Explorar cosechas”, “Reservar”, “Publicar
  cosecha” y “Seguir entrega”. Se evitan categorías vagas como “actividad”.
- Las ofertas presentan producto o variedad, finca, productor, municipio, cantidad en
  kilogramos y precio en COP por kilogramo. Así se elimina la ambigüedad de las unidades.
- El catálogo permite combinar producto, municipio, categoría y agricultor. También incluye
  un estado sin resultados y la opción de limpiar filtros.
- La publicación no ocupa el tablero del agricultor: se abre desde un botón hacia un
  formulario dedicado.
- Los mensajes nuevos tienen contador visible y aparecen entre las prioridades del
  agricultor. Los pedidos distinguen estados pendientes, en curso, entregados y retrasados.
- El seguimiento muestra un historial, origen, destino, producto y última novedad, en lugar
  de reducir la logística a un estado aislado.
- El panel administrativo está separado de la página pública y del panel del agricultor.
  Resume agricultores, compradores, productos, pedidos, entregas, alertas y reportes.
- La marca utiliza sol, valle y hojas diversas; no asocia la plataforma a un único cultivo.

## Legibilidad y adaptación

El diseño usa Arial/Helvetica, texto base de 17 px tanto en escritorio como en pantallas
pequeñas, controles principales de al menos 48 px y etiquetas visibles. La paleta combina
verde bosque, verdes suaves, crema y un acento cálido; el verde no ocupa todas las superficies
y se reserva suficiente fondo claro para evitar fatiga visual.

La interfaz se reorganiza a 1080, 820 y 560 px. En pantallas estrechas aparecen menús
desplegables, las tarjetas se presentan en una columna, las tablas permiten desplazamiento
horizontal y el seguimiento cambia de una línea horizontal a una secuencia vertical.

## Alcance técnico comprobable

Los HTML, estilos y comportamientos están en `src/main/resources/static/` y Spring Boot los
sirve desde la raíz de la aplicación. La búsqueda del catálogo, los estados vacíos, los
mensajes de validación del prototipo, la consulta visual de entrega y los menús responsive
funcionan en el navegador.

Las vistas representan el producto objetivo completo. En el incremento actual, las rutas
REST terminadas siguen siendo las declaradas en el README principal y en la planificación del
Sprint 2. Por tanto, los pedidos, entregas, reportes y reservas de estas pantallas son evidencia
de diseño navegable, no una afirmación de que todos esos módulos backend ya estén terminados.

## Recursos originales

- [`site.css`](../../../../src/main/resources/static/assets/site.css): sistema visual y
  adaptación responsive.
- [`site.js`](../../../../src/main/resources/static/assets/site.js): consumo de la API REST,
  sesión JWT, navegación, filtros y retroalimentación visible.
- [`brand-mark.svg`](../../../../src/main/resources/static/assets/brand-mark.svg): marca
  vectorial multicutivo.
- [`agrovalle-hero.webp`](../../../../src/main/resources/static/assets/agrovalle-hero.webp):
  composición agrícola creada para AgroValle Connect y optimizada para la web.

## Integración funcional disponible

La interfaz consume el backend para registro e inicio de sesión, consulta de identidad,
registro y listado de fincas, publicación de cosechas y catálogo filtrable. El catálogo
muestra productor, finca y municipio mediante un contrato público que excluye correo,
documento, contraseña y demás datos privados. Contacto, reservas, pedidos y trazabilidad
permanecen identificados como el siguiente incremento de programación.

Los pedidos y entregas visibles son datos de diseño. Las cuentas, fincas y cosechas creadas
desde los formularios conectados sí se leen y guardan mediante la API REST.
