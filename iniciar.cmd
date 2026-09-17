@echo off
setlocal
cd /d "%~dp0"
call configurar-java.cmd
if errorlevel 1 goto :error
set "AGRO_PUERTO=8081"
if not "%~1"=="" set "AGRO_PUERTO=%~1"
echo.
echo Espera a ver Started AgrovalleApplication y abre:
echo http://localhost:%AGRO_PUERTO%/
echo Deja esta ventana abierta. Ctrl+C detiene la aplicacion.
echo Si el puerto esta ocupado, ejecuta iniciar.cmd 8082
call mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=%AGRO_PUERTO%"
if errorlevel 1 goto :error
exit /b 0
:error
echo No se pudo iniciar. Copia el error que aparece arriba.
pause
exit /b 1
