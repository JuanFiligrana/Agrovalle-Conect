# Correspondencia con los requisitos de la entrega

| Requisito | Archivo o evidencia | Situación |
|---|---|---|
| Java 17 LTS y Spring Boot | `pom.xml`, código y CI | Configurado según la guía oficial |
| Conventional Commits y TDD | Historial de la rama Sprint 2 | Commit de pruebas rojas seguido por implementación |
| Pull Request y CI | PR #52 y ejecución 35 | Check `Java CI / build` aprobado |
| Peer Review | Aprobación de una persona distinta al autor | No acreditada en ejecución individual |
| WIP máximo 3/2 | `docs/kanban-policies.md` y tablero | Política documentada; configuración visual por comprobar |
| HU-01 en ISO/IEC/IEEE 29148 | `docs/ieee29148-req01.md` | Documentado |
| HU-02 y HU-04 en ISO/IEC/IEEE 29148 | `docs/especificacion-sprint2.md` | Documentado y trazado a pruebas |
| Sprint 2 funcional | `docs/sprint-2-planning.md`, código y MockMvc | HU-02 y HU-04 verificadas por 55 pruebas |
| PostgreSQL normalizado | `schema.sql`, entidades y D02 | Usuarios, fincas, categorías, productos y cosechas |
| SOLID | Puertos, servicios y adaptadores | SRP, OCP, LSP, ISP y DIP explicados |
| GOF Strategy | `strategies`, `CalculadoraFleteService` y pruebas | Implementado |
| GOF Observer | Evento, listener, endpoint y pruebas | Implementado; avisos de demostración volátiles |
| JUnit y Mockito | `src/test/java` y ejecución 35 | 55 pruebas; 0 fallos, 0 errores y 0 omitidas |
| Integración REST | `Sprint2EndpointsTests` | 4 pruebas MockMvc con seguridad y persistencia |
| JaCoCo mínimo 60 % | `pom.xml`, `sprint2-jacoco.csv` y artefacto `jacoco-35` | 97,93 % de líneas; umbral aprobado |
| D01–D07 | `docs/diseno` | Archivos editables, SVG, PNG y explicaciones presentes |
| Ceremonias en tercera persona | `docs/ceremonias-sprint2.md` | Daily, Review y Retrospective documentadas |
| Informe final | `output/pdf/Grupo-6303B-Entrega-segundo-corte-AgroValle.pdf` | PDF de 3 páginas: portada y resumen máximo de 2 páginas con enlaces y cifras verificadas |

No se asigna una nota ni se supone aprobación docente. La evidencia remota de CI está
registrada; la revisión por pares continúa sin acreditarse y la configuración del tablero
se comprueba por separado.
