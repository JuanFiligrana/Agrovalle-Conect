@echo off
rem Cambia variables solo en la terminal actual; no modifica Windows ni desinstala Java.
set "AGRO_JDK="
if defined JAVA_HOME call :probar "%JAVA_HOME%"
if defined AGRO_JDK goto :encontrado
for /d %%J in ("C:\Program Files\Java\jdk-25*" "C:\Program Files\Eclipse Adoptium\jdk-25*" "C:\Program Files\Microsoft\jdk-25*" "C:\Program Files\Amazon Corretto\jdk25*" "%USERPROFILE%\.jdks\*25*") do call :probar "%%~J"
if defined AGRO_JDK goto :encontrado
for /f "delims=" %%J in ('where java 2^>nul') do if not defined AGRO_JDK for %%K in ("%%~dpJ..") do call :probar "%%~fK"
if defined AGRO_JDK goto :encontrado
echo No se encontro JDK 25. Instala JDK 25 o indica su carpeta con:
echo set "JAVA_HOME=C:\ruta\a\tu\jdk-25"
echo Despues ejecuta iniciar.cmd de nuevo. Tu Java actual no se elimina.
exit /b 1
:probar
if defined AGRO_JDK exit /b 0
if not exist "%~1\bin\java.exe" exit /b 0
if not exist "%~1\bin\javac.exe" exit /b 0
"%~1\bin\javac.exe" -version 2>&1 | findstr /B /C:"javac 25." >nul
if not errorlevel 1 set "AGRO_JDK=%~1"
exit /b 0
:encontrado
set "JAVA_HOME=%AGRO_JDK%"
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo JDK seleccionado: %JAVA_HOME%
exit /b 0
