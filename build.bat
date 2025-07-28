@echo off
setlocal

set SRC_DIR=src
set BUILD_DIR=build
set CLASS_DIR=%BUILD_DIR%\classes
set JAR_FILE=%BUILD_DIR%\acfdump.jar
set MAIN_CLASS=FileUnpackers.PkmnR_ACF
set "JAVA_FLAGS=-d %CLASS_DIR% -sourcepath %SRC_DIR%"

if not exist "%CLASS_DIR%" (
    mkdir "%CLASS_DIR%"
)
dir /b /s %SRC_DIR%\*.java > sources.txt
javac %JAVA_FLAGS% @sources.txt
if errorlevel 1 (
    echo Compilation failed.
    del sources.txt
    exit /b 1
)
del sources.txt
jar cfe "%JAR_FILE%" %MAIN_CLASS% -C "%CLASS_DIR%" .
endlocal
