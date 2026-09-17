@echo off
setlocal
cd /d "%~dp0"
call configurar-java.cmd
if errorlevel 1 goto :error
call mvnw.cmd clean verify
if errorlevel 1 goto :error
echo VALIDACION CORRECTA: compilacion, estilo, pruebas y cobertura.
pause
exit /b 0
:error
echo VALIDACION FALLIDA. Revisa el mensaje anterior.
pause
exit /b 1
