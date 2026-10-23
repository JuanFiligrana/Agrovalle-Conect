# Evidencia verificable del Sprint 2

> **Registro histórico:** los valores siguientes pertenecen a la ejecución 35 del PR #52
> con Java 25. La configuración actual del proyecto usa Java 17; esta tabla se conserva
> para no alterar evidencia ya producida.

Fecha de verificación: 22 de octubre de 2026, zona `America/Bogota`.

## Enlaces públicos

- Repositorio: <https://github.com/JuanFiligrana/Agrovalle-Conect>
- Tablero: <https://github.com/users/JuanFiligrana/projects/1>
- Pull Request: <https://github.com/JuanFiligrana/Agrovalle-Conect/pull/52>
- GitHub Actions, ejecución 35: <https://github.com/JuanFiligrana/Agrovalle-Conect/actions/runs/39735124556>

## Resultado de automatización

| Control | Resultado acreditado |
|---|---:|
| Java | Temurin JDK 25 |
| Checkstyle | 0 infracciones |
| JUnit | 55 pruebas, 0 fallos, 0 errores, 0 omitidas |
| Clases analizadas por JaCoCo | 59 |
| Líneas | 473 cubiertas de 483; **97,93 %** |
| Ramas | 83 cubiertas de 106; 78,30 % |
| Instrucciones | 2.522 cubiertas de 2.614; 96,48 % |
| Umbral obligatorio | 60 % de líneas; aprobado |
| Artefacto | `jacoco-35`, ID `12406703171` |

Los porcentajes se calcularon desde `jacoco.csv`, publicado dentro del artefacto HTML de
la ejecución. La copia trazable está en `docs/evidencias/sprint2-jacoco.csv` y el resumen
estructurado en `docs/evidencias/sprint2-resumen.json`.

## Trazabilidad del ajuste de CI

La ejecución 34 falló antes de compilar por cuatro infracciones
`AbbreviationAsWordInName` en nombres de pruebas. El commit
`test: corregir nombres exigidos por Checkstyle` aplicó únicamente esos cambios. La
ejecución 35 completó `mvnw -B verify` y publicó el reporte JaCoCo.

## Límites declarados

La ejecución es individual. El PR no contiene una aprobación de una persona distinta al
autor, por lo que la revisión por pares no se acredita ni se simula. La aceptación técnica
registrada corresponde solamente a los controles automáticos anteriores.
