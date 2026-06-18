@echo off
setlocal

cd /d "%~dp0"

set "JAVA_EXE="
set "JAVAC_EXE="
set "EVENTS_JAR=libs\HotelEventsObs.jar"
set "JSON_JAR=libs\json-20251224.jar"

if exist "libs\HotelEventsObs 1\HotelEventsObs.jar" set "EVENTS_JAR=libs\HotelEventsObs 1\HotelEventsObs.jar"

if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
    if exist "%JAVA_HOME%\bin\javac.exe" set "JAVAC_EXE=%JAVA_HOME%\bin\javac.exe"
)

if not defined JAVA_EXE if exist "C:\Users\mahmo\.jdks\ms-25.0.2\bin\java.exe" set "JAVA_EXE=C:\Users\mahmo\.jdks\ms-25.0.2\bin\java.exe"
if not defined JAVAC_EXE if exist "C:\Users\mahmo\.jdks\ms-25.0.2\bin\javac.exe" set "JAVAC_EXE=C:\Users\mahmo\.jdks\ms-25.0.2\bin\javac.exe"

if not defined JAVA_EXE if exist "C:\Users\mahmo\.jdks\liberica-25.0.2\bin\java.exe" set "JAVA_EXE=C:\Users\mahmo\.jdks\liberica-25.0.2\bin\java.exe"
if not defined JAVAC_EXE if exist "C:\Users\mahmo\.jdks\liberica-25.0.2\bin\javac.exe" set "JAVAC_EXE=C:\Users\mahmo\.jdks\liberica-25.0.2\bin\javac.exe"

if not defined JAVA_EXE if exist "C:\Users\mahmo\.jdks\liberica-24.0.2\bin\java.exe" set "JAVA_EXE=C:\Users\mahmo\.jdks\liberica-24.0.2\bin\java.exe"
if not defined JAVAC_EXE if exist "C:\Users\mahmo\.jdks\liberica-24.0.2\bin\javac.exe" set "JAVAC_EXE=C:\Users\mahmo\.jdks\liberica-24.0.2\bin\javac.exe"

if not defined JAVA_EXE if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.2\jbr\bin\java.exe" set "JAVA_EXE=C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.2\jbr\bin\java.exe"
if not defined JAVAC_EXE if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.2\jbr\bin\javac.exe" set "JAVAC_EXE=C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.2\jbr\bin\javac.exe"

if not defined JAVA_EXE if exist "C:\Program Files\Android\Android Studio\jbr\bin\java.exe" set "JAVA_EXE=C:\Program Files\Android\Android Studio\jbr\bin\java.exe"
if not defined JAVAC_EXE if exist "C:\Program Files\Android\Android Studio\jbr\bin\javac.exe" set "JAVAC_EXE=C:\Program Files\Android\Android Studio\jbr\bin\javac.exe"

if not defined JAVA_EXE (
    echo Java niet gevonden. Installeer Java of zet JAVA_HOME goed.
    pause
    exit /b 1
)

if not defined JAVAC_EXE (
    echo Javac niet gevonden. Installeer een JDK of zet JAVA_HOME goed.
    pause
    exit /b 1
)

echo Java bestanden worden gecompileerd...
set "BUILD_DIR=out\production\Hotel sim"
if not exist "%BUILD_DIR%" mkdir "%BUILD_DIR%"

for /r "hotelsim\src" %%F in (*.java) do (
    call set "JAVA_FILES=%%JAVA_FILES%% "%%F""
)

if not defined JAVA_FILES (
    echo Geen Java bronbestanden gevonden.
    pause
    exit /b 1
)

"%JAVAC_EXE%" -encoding UTF-8 -cp "%EVENTS_JAR%;%JSON_JAR%" -d "%BUILD_DIR%" %JAVA_FILES%
if errorlevel 1 (
    echo Compilatie mislukt.
    pause
    exit /b 1
)

echo Programma wordt gestart...
"%JAVA_EXE%" -cp "%BUILD_DIR%;%EVENTS_JAR%;%JSON_JAR%" Main

if errorlevel 1 (
    echo Programma is gestopt met een fout.
) else (
    echo Programma is afgesloten.
)

pause
