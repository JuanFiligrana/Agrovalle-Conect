# AgroValle Connect 🌾🚜

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Ready-blue)

## 1. Visión del Producto
Para los productores agrícolas del Valle del Cauca, que necesitan vender sus cosechas de forma directa, AgroValle Connect es una plataforma web en Java, que conecta la oferta agrícola con la demanda comercial urbana a un precio justo. A diferencia de las cadenas con intermediarios tradicionales, nuestro producto garantiza trazabilidad logística y contratos de API transparentes.

## 2. Equipo de Desarrollo
*   **Integrante 1:** [Juan Eduardo Filigrana Mindinero]
*   **Integrante 2:** [Michael David Caicedo Mina]
*   **Integrante 3:** [Juan Camilo Alvarez]
*   **Integrante 4:** [Jhojan Aragon]

## 3. Estrategia de Control de Versiones: GitFlow
Para este proyecto hemos seleccionado **GitFlow** como nuestra estrategia de ramificación. 
**Justificación:** Esta elección minimiza los conflictos de fusión y permite mantener un control estricto sobre los lanzamientos estructurados de las diferentes versiones de la plataforma. Evita el "Merge Hell" aislando el desarrollo de nuevas historias de usuario en ramas `feature/` que solo se integran a `develop` tras superar el proceso de Pull Request y pruebas automatizadas.

### Diagrama de la Estrategia (Mermaid.js)
```mermaid
gitGraph
    commit id: "Initial"
    branch develop
    checkout develop
    commit id: "Setup-Project"
    branch feature/HU-01
    checkout feature/HU-01
    commit id: "feat: logic-hu-01"
    checkout develop
    merge feature/HU-01
    branch release/v1.0.0
    checkout release/v1.0.0
    commit id: "fix: minor-bug"
    checkout main
    merge release/v1.0.0 tag: "v1.0.0"
    checkout develop
    merge release/v1.0.0