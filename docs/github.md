# Publicación y revisión del Sprint 2 en GitHub

## Referencias

- Repositorio: <https://github.com/JuanFiligrana/Agrovalle-Conect>
- Tablero: <https://github.com/users/JuanFiligrana/projects/1>
- Rama: `feature/sprint2-hu02-hu04`
- Pull Request: <https://github.com/JuanFiligrana/Agrovalle-Conect/pull/52>
- CI aprobada: <https://github.com/JuanFiligrana/Agrovalle-Conect/actions/runs/39735124556>
- Destino del Pull Request: `main`

## Historial TDD observado

La rama parte del merge del Sprint 1 y conserva cambios coherentes:

```text
test: definir criterios Sprint 2 en rojo
feat: implementar historias y patrones del Sprint 2
refactor: inyectar estrategias de flete
docs: documentar entrega del Sprint 2
test: corregir nombres exigidos por Checkstyle
```

El primer commit define las capacidades antes de implementarlas. El segundo implementa
HU-02/HU-04, persistencia, Strategy y Observer; el tercero fortalece OCP/DIP; el cuarto
alinea la entrega. El quinto responde al primer resultado real de CI sin reescribir el
historial para aparentar una secuencia distinta.

## Pull Request

El PR debe incluir:

- propósito y alcance de HU-02/HU-04;
- migración del repositorio de cosechas en memoria a JPA;
- controles de JWT, propiedad, catálogos y fecha de Colombia;
- pruebas unitarias Mockito y pruebas REST MockMvc;
- explicación de Strategy y Observer;
- enlace al tablero y riesgos conocidos.

El check `build` ejecuta `mvnw -B verify` con JDK 17 LTS. Checkstyle, JUnit y los umbrales
JaCoCo de 60 % para instrucciones, ramas y líneas deben pasar antes del merge. El workflow publica el HTML de JaCoCo como
artefacto para evitar depender de una captura aislada.

La ejecución 35 aprobó `verify`: cero infracciones de Checkstyle, 55 pruebas sin fallos
y 473 de 483 líneas cubiertas (97,93 %). El artefacto publicado se llama `jacoco-35`.

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

## Evidencias registradas

- PR #52 y commit validado `1377b1338fa78519b0350e24278abb0a1df7bd57`.
- Ejecución 35 de `Java CI / build`, aprobada.
- 55 pruebas; 0 fallos, 0 errores y 0 omitidas.
- JaCoCo: 473/483 líneas, equivalentes a 97,93 %.
- Artefacto `jacoco-35`, ID `12406703171`.
- La aprobación externa permanece sin acreditarse.

No se publican contraseñas, claves JWT, tokens ni archivos `.env`. Las variables reales
se configuran fuera del repositorio.
