# Publicación y revisión del Sprint 2 en GitHub

## Referencias

- Repositorio: <https://github.com/JuanFiligrana/Agrovalle-Conect>
- Tablero: <https://github.com/users/JuanFiligrana/projects/1>
- Rama: `feature/sprint2-hu02-hu04`
- Destino del Pull Request: `main`

## Historial TDD previsto

La rama parte del merge del Sprint 1 y conserva cambios coherentes:

```text
test: definir criterios Sprint 2 en rojo
feat: implementar historias y patrones del Sprint 2
docs: documentar entrega y trazabilidad del Sprint 2
```

El primer commit debe fallar porque define las capacidades que todavía no existen. El
segundo implementa HU-02/HU-04, persistencia, Strategy y Observer. El tercero alinea la
entrega. No se reescribe el historial para aparentar una secuencia distinta.

## Pull Request

El PR debe incluir:

- propósito y alcance de HU-02/HU-04;
- migración del repositorio de cosechas en memoria a JPA;
- controles de JWT, propiedad, catálogos y fecha de Colombia;
- pruebas unitarias Mockito y pruebas REST MockMvc;
- explicación de Strategy y Observer;
- enlace al tablero y riesgos conocidos.

El check `build` ejecuta `mvnw -B verify` con JDK 25. Checkstyle, JUnit y el umbral
JaCoCo de 60 % deben pasar antes del merge. El workflow publica el HTML de JaCoCo como
artefacto para evitar depender de una captura aislada.

## Protección de `main`

La configuración objetivo exige:

1. cambios mediante Pull Request;
2. resolución de conversaciones antes del merge;
3. check `build` exitoso y actualizado;
4. bloqueo de force push y eliminación de la rama principal.

La profesora también solicitó revisión por pares. Como el incremento se ejecuta de forma
individual, esa aprobación permanece **no acreditada** hasta que participe otra persona.
No se crea una revisión ficticia ni se usa la aprobación del propio autor como evidencia.

## Tablero y WIP

Las tarjetas se mueven según evidencia real: Backlog → Tareas pendientes → En curso →
Testing / Code Review → Hechos. El límite compartido es tres elementos en curso y dos en
revisión. Cada tarjeta cerrada enlaza el PR, commit, documento o ejecución que demuestra
su resultado. La política completa está en `kanban-policies.md`.

## Evidencias que se registran al finalizar

- URL del PR y SHA integrado.
- URL de la ejecución `build` aprobada.
- número de pruebas y porcentaje de líneas reportado por JaCoCo.
- enlace de aprobación externa, si realmente existe.
- estado final de las tarjetas del Sprint 2.
- informe PDF con portada y resumen de cambios.

No se publican contraseñas, claves JWT, tokens ni archivos `.env`. Las variables reales
se configuran fuera del repositorio.
