# Publicación y revisión en GitHub

Este paquete contiene archivos nuevos; no sustituye ni reescribe el historial del
repositorio del equipo. La URL prevista es https://github.com/JuanFiligrana/Agrovalle-Conect.
Si usan otra URL, actualizar README, badge, Word y PDF antes de entregar.

## Integrar sin perder el repositorio anterior
1. Conservar el proyecto recibido en una carpeta aparte y respaldar el repositorio anterior.
2. Clonar el repositorio del equipo en una carpeta NUEVA desde la terminal:

```powershell
git clone https://github.com/JuanFiligrana/Agrovalle-Conect.git AgroValle-revision
cd AgroValle-revision
git switch -c chore/sprint-0-completo
```

3. Copiar dentro de AgroValle-revision todo el contenido de proyecto de este paquete,
   incluyendo .github, .husky y .mvn. Conservar la carpeta .git del clon. No anidar proyecto.
4. Revisar los cambios de documentación y borrar solo archivos obsoletos que el equipo
   identifique expresamente. Esta entrega no elimina archivos del repositorio remoto.
5. En CMD, ejecutar `call configurar-java.cmd` antes de los comandos de validación y Git.
   En PowerShell, seleccionar JAVA_HOME del JDK 25 en esa terminal. Abrir VS Code desde
   una terminal configurada permite que sus commits hereden ese entorno.
6. Ejecutar `npm.cmd ci` y `mvnw.cmd clean verify` (en PowerShell, `./mvnw.cmd clean verify`).
   Confirmar hooks con `git config --get core.hooksPath`.

## Commits por cambios coherentes
No fabricar fechas ni atribuir cambios a otros integrantes. Tras revisar cada grupo:

```powershell
git add pom.xml mvnw mvnw.cmd .mvn .gitattributes src iniciar.cmd configurar-java.cmd verificar.cmd
git commit -m "build: preparar base Java 25 y pruebas del Sprint 0"
git add .gitignore checkstyle.xml package.json package-lock.json .husky .github
git commit -m "build: automatizar calidad local e integracion continua"
git add README.md BACKLOG.md docs
git commit -m "docs: definir vision backlog BDD y contrato de calidad"
git push -u origin chore/sprint-0-completo
```

Las validaciones deben pasar antes de cada commit. Los hooks revisan el árbol local;
por ello conviene verificar también el conjunto de cambios que queda en cada commit.
Para evitar dependencias entre commits iniciales, el equipo puede preferir un único
commit honesto de configuración; no se presenta esta propuesta como historial ya creado.

## Pull Request y revisión
Crear un PR desde chore/sprint-0-completo hacia main. Describir base, configuración y
documentación, adjuntar la salida de verify y asignar como reviewer a otro integrante.
El revisor debe abrir Files changed, comprobar código y requisitos, comentar hallazgos
y aprobar con Review changes > Approve cuando estén resueltos. El autor no se aprueba
su propio trabajo. Esperar el check build exitoso antes de integrar.

La transición a Trunk-Based es una decisión propuesta para esta entrega nueva;
confirmarla en equipo y retirar referencias contradictorias a GitFlow.

## Evidencias a registrar
- URL del PR y del check build aprobado.
- Nombre del revisor y enlace a su aprobación real.
- Captura del historial de commits y del diagrama Mermaid renderizado.
- Aceptación del DoD por los cuatro integrantes.
- Acta de Planning Poker con estimaciones finales.
- Confirmación de repositorio público, abriéndolo sin sesión.

Si no puedes hacer push, el propietario debe darte acceso de colaborador o recibir
el cambio mediante un fork y Pull Request. No compartir credenciales ni tokens.

## Protección de main
En las opciones de protección o rulesets, exigir un Pull Request, al menos una aprobación
externa al autor y el check build. La interfaz depende del tipo de cuenta y permisos.
No marcar el requisito como satisfecho hasta ver la configuración aplicada.
