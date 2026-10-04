@echo off
setlocal

:: ==============================
:: Configuration
:: ==============================
set PROJECT_DIR=%~dp0
set SRC_DIR=%PROJECT_DIR%src
set BIN_DIR=%PROJECT_DIR%bin
set JAR_NAME=Framework.jar

echo =============================
echo Compilation du framework...
echo =============================

:: Création du dossier bin
if not exist "%BIN_DIR%" (
    mkdir "%BIN_DIR%"
)

:: Suppression des anciens .class
del /S /Q "%BIN_DIR%\*.class" >nul 2>&1

:: Compilation
javac -d "%BIN_DIR%" ^
    %SRC_DIR%\framework\annotation\*.java

if errorlevel 1 (
    echo.
    echo Erreur pendant la compilation.
    pause
    exit /b
)

echo.
echo =============================
echo Creation du JAR...
echo =============================

jar cf "%PROJECT_DIR%%JAR_NAME%" -C "%BIN_DIR%" .

if errorlevel 1 (
    echo.
    echo Impossible de creer le JAR.
    pause
    exit /b
)

echo.
echo =============================
echo Framework compile avec succes !
echo =============================
echo.
echo JAR genere :
echo %PROJECT_DIR%%JAR_NAME%
echo.

pause