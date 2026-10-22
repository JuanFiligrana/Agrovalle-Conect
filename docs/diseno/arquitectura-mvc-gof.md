# Arquitectura MVC en cuatro capas y patrones aplicados

## Separación de responsabilidades

| Capa | Componentes del proyecto | Responsabilidad |
|---|---|---|
| Presentación / API REST | `controllers`, DTO y validaciones | Recibir HTTP/JSON, validar el contrato y devolver estados HTTP. |
| Servicio / dominio | `services`, reglas y transacciones | Ejecutar los casos de uso sin acceder directamente a SQL. |
| Acceso a datos | `ports` y adaptadores `repositories.jpa` | Abstraer y materializar las operaciones de persistencia. |
| Persistencia | PostgreSQL y `schema.sql` | Conservar datos con claves, restricciones e integridad referencial. |

El detalle gráfico y la correspondencia con el código están en
[D03 - Arquitectura MVC](D03-Arquitectura-MVC/D03-Arquitectura-MVC/README-D03.md).

## Patrones presentes

- **Repository:** los servicios dependen de puertos pequeños; los adaptadores JPA
  implementan esos contratos.
- **DTO:** los contratos HTTP están separados de las entidades persistentes.
- **IoC / inyección de dependencias:** Spring crea los componentes y los entrega por
  constructor; no se implementa un Singleton manual.
- **Data Mapper:** JPA/Hibernate transforma entidades y relaciones en filas del esquema.
- **Strategy:** intercambia reglas de cálculo de flete sin modificar el contexto.
- **Observer:** publica un evento de cosecha y permite reaccionar sin acoplar el emisor
  a los receptores.

La explicación y los diagramas de clases de los patrones comportamentales se encuentran
en [patrones.md](patrones.md).
