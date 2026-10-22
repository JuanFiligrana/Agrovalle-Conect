# Correspondencia con los requisitos de la entrega

| Requisito | Archivo o evidencia | Situación |
|---|---|---|
| Java 25 y Spring Boot | `pom.xml`, código y GitHub Actions | Configurado; resultado sujeto al check remoto |
| Conventional Commits y TDD | Historial de la rama Sprint 2 | Commit de pruebas rojas seguido por implementación |
| Pull Request y CI | PR de Sprint 2 y check `build` | Se acredita únicamente con sus enlaces remotos |
| Peer Review | Aprobación de una persona distinta al autor | No acreditada en ejecución individual |
| WIP máximo 3/2 | `docs/kanban-policies.md` y tablero | Política documentada; configuración visual por comprobar |
| HU-01 en ISO/IEC/IEEE 29148 | `docs/especificacion-hu01.md` | Documentado |
| HU-02 y HU-04 en ISO/IEC/IEEE 29148 | `docs/especificacion-sprint2.md` | Documentado y trazado a pruebas |
| Sprint 2 funcional | `docs/sprint2.md`, código y MockMvc | HU-02 y HU-04 implementadas; CI pendiente |
| PostgreSQL normalizado | `schema.sql`, entidades y D02 | Usuarios, fincas, categorías, productos y cosechas |
| SOLID | Puertos, servicios y adaptadores | SRP, OCP, LSP, ISP y DIP explicados |
| GOF Strategy | `strategies`, `CalculadoraFleteService` y pruebas | Implementado |
| GOF Observer | Evento, listener, endpoint y pruebas | Implementado; avisos de demostración volátiles |
| JUnit y Mockito | `src/test/java` | Pruebas unitarias definidas |
| Integración REST | `Sprint2EndpointsTests` | MockMvc con seguridad y persistencia |
| JaCoCo mínimo 60 % | `pom.xml` y artefacto del workflow | Umbral configurado; porcentaje real lo informa CI |
| D01–D07 | `docs/Diseño` | Archivos editables, SVG, PNG y explicaciones presentes |
| Ceremonias en tercera persona | `docs/ceremonias-sprint2.md` | Daily, Review y Retrospective documentadas |
| Informe final | `docs/entrega-sprint2` | Se genera con enlaces y cifras verificadas |

No se asigna una nota ni se supone aprobación docente. Una política o un documento no
sustituye la evidencia remota de CI, revisión y configuración del tablero.
