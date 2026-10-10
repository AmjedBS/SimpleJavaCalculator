@echo off
setlocal

set "APP_NAME=FXCalculator"
set "APP_VERSION=1.0.1"
set "APP_MAIN_CLASS=FXCalculator"
set "APP_LIB_DIR=lib"
set "APP_OUTPUT_DIR=dist"
set "APP_WORK_DIR=%TEMP%\FXCalculator-build-%RANDOM%"
set "APP_IMAGE=%APP_OUTPUT_DIR%\%APP_NAME%"

pushd "%~dp0"
if errorlevel 1 (
    echo ERROR: Could not switch to the project directory.
    exit /b 1
)

if not defined JAVA_HOME set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot"
if not defined JAVAFX_JMODS_DIR set "JAVAFX_JMODS_DIR=%APP_LIB_DIR%\javafx-jmods-21.0.12"

if not exist "%JAVA_HOME%\bin\javac.exe" (
    echo ERROR: JDK not found at "%JAVA_HOME%".
    echo Install JDK 21 or set JAVA_HOME to your JDK 21 directory.
    goto build_failed
)
if not exist "%JAVA_HOME%\bin\jpackage.exe" (
    echo ERROR: jpackage was not found in "%JAVA_HOME%\bin".
    goto build_failed
)
if not exist "%APP_LIB_DIR%\javafx.controls.jar" (
    echo ERROR: JavaFX 21.0.12 SDK JARs were not found in "%APP_LIB_DIR%".
    goto build_failed
)
if not exist "%JAVAFX_JMODS_DIR%\javafx.controls.jmod" (
    echo ERROR: JavaFX JMODs were not found in "%JAVAFX_JMODS_DIR%".
    echo Download the Windows x64 JavaFX 21.0.12 JMOD archive from https://gluonhq.com/products/javafx/
    echo Extract the archive's javafx-jmods-21.0.12 folder into "%APP_LIB_DIR%", or set JAVAFX_JMODS_DIR to that folder.
    goto build_failed
)
if not exist "%JAVAFX_JMODS_DIR%\javafx.graphics.jmod" (
    echo ERROR: javafx.graphics.jmod was not found in "%JAVAFX_JMODS_DIR%".
    goto build_failed
)
if not exist "%JAVA_HOME%\jmods\java.desktop.jmod" (
    echo ERROR: JDK 21 JMODs were not found under "%JAVA_HOME%\jmods".
    goto build_failed
)

set "PATH=%JAVA_HOME%\bin;%PATH%"
if not exist "%APP_OUTPUT_DIR%" mkdir "%APP_OUTPUT_DIR%"
if errorlevel 1 (
    echo ERROR: Could not create "%APP_OUTPUT_DIR%".
    goto build_failed
)

mkdir "%APP_WORK_DIR%\classes"
mkdir "%APP_WORK_DIR%\input"
mkdir "%APP_WORK_DIR%\output"
if errorlevel 1 (
    echo ERROR: Could not create temporary build directories.
    goto build_failed
)

echo [1/3] Compiling FXCalculator with CalculatorEngine...
javac --module-path "%APP_LIB_DIR%" --add-modules javafx.controls,javafx.graphics -d "%APP_WORK_DIR%\classes" FXCalculator.java CalculatorEngine.java
if errorlevel 1 goto build_failed

echo [2/3] Creating application JAR...
jar cfe "%APP_WORK_DIR%\input\%APP_NAME%.jar" "%APP_MAIN_CLASS%" -C "%APP_WORK_DIR%\classes" .
if errorlevel 1 goto build_failed

echo [3/3] Creating standalone Windows application image...
jpackage ^
    --type app-image ^
    --name "%APP_NAME%" ^
    --app-version "%APP_VERSION%" ^
    --description "A tactile AMOLED calculator with real-time audio" ^
    --input "%APP_WORK_DIR%\input" ^
    --main-jar "%APP_NAME%.jar" ^
    --main-class "%APP_MAIN_CLASS%" ^
    --module-path "%JAVA_HOME%\jmods;%JAVAFX_JMODS_DIR%" ^
    --add-modules javafx.controls,javafx.graphics ^
    --java-options "--add-modules=javafx.controls,javafx.graphics" ^
    --dest "%APP_WORK_DIR%\output"
if errorlevel 1 goto build_failed

if not exist "%APP_WORK_DIR%\output\%APP_NAME%\%APP_NAME%.exe" (
    echo ERROR: jpackage did not create the expected application executable.
    goto build_failed
)
if not exist "%APP_WORK_DIR%\output\%APP_NAME%\runtime\release" (
    echo ERROR: The packaged Java runtime is missing from the application image.
    goto build_failed
)

if exist "%APP_IMAGE%" rmdir /s /q "%APP_IMAGE%"
if errorlevel 1 (
    echo ERROR: Could not replace the previous application image at "%APP_IMAGE%".
    goto build_failed
)
xcopy /E /I /Y "%APP_WORK_DIR%\output\%APP_NAME%" "%APP_IMAGE%" >nul
if errorlevel 1 (
    echo ERROR: Could not copy the application image to "%APP_IMAGE%".
    if exist "%APP_IMAGE%" rmdir /s /q "%APP_IMAGE%"
    goto build_failed
)
if not exist "%APP_IMAGE%\%APP_NAME%.exe" (
    echo ERROR: The application executable is missing from "%APP_IMAGE%".
    if exist "%APP_IMAGE%" rmdir /s /q "%APP_IMAGE%"
    goto build_failed
)

if exist "%APP_LIB_DIR%\fx-jmods-21.0.12.zip" del /q "%APP_LIB_DIR%\fx-jmods-21.0.12.zip"
rmdir /s /q "%APP_WORK_DIR%"
popd
echo.
echo BUILD COMPLETE
echo Run: %~dp0%APP_IMAGE%\%APP_NAME%.exe
echo Copy the entire "%APP_IMAGE%" folder to run it on another Windows x64 PC.
exit /b 0

:build_failed
set "BUILD_ERROR=%errorlevel%"
if "%BUILD_ERROR%"=="0" set "BUILD_ERROR=1"
if exist "%APP_WORK_DIR%" rmdir /s /q "%APP_WORK_DIR%"
popd
echo.
echo BUILD FAILED. Resolve the error above and run build_final.cmd again.
exit /b %BUILD_ERROR%
