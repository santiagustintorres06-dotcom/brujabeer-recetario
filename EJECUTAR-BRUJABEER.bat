@echo off
chcp 65001 >nul 2>&1
title BrujaBeer - Recetario Cervecero

echo.
echo  =============================================
echo    BrujaBeer - Recetario de Cerveza Artesanal
echo  =============================================
echo.

REM --- Configurar Java ---
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"

REM --- Configurar Maven ---
set "MAVEN_HOME=%~dp0tools\apache-maven-3.9.16"
set "PATH=%MAVEN_HOME%\bin;%PATH%"

REM --- Verificar Java ---
java -version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Java no encontrado.
    echo Descarga Java 21 desde: https://adoptium.net/temurin/releases/?version=21
    pause
    exit /b 1
)

echo [OK] Java encontrado.
echo [OK] Compilando e iniciando BrujaBeer...
echo.
echo     Esto puede tardar 15-30 segundos la primera vez.
echo     La ventana se abrira automaticamente.
echo.

REM --- Compilar y ejecutar ---
cd /d "%~dp0"
call mvn compile javafx:run -q

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] La aplicacion se cerro con errores.
    echo Revisa los mensajes anteriores.
)

pause
