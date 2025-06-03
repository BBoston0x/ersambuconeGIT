@echo off
setlocal

echo [INFO] Setup Gradle Wrapper ufficiale...

if exist gradlew.bat (
    echo [SKIP] Gradle Wrapper gia' presente
    goto :build
)

:: Scarica gradlew.bat
powershell -Command "Invoke-WebRequest -Uri https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradlew.bat -OutFile gradlew.bat"

:: Crea cartelle wrapper
if not exist gradle\wrapper mkdir gradle\wrapper

:: Scarica gradle-wrapper.properties
powershell -Command "Invoke-WebRequest -Uri https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.properties -OutFile gradle\wrapper\gradle-wrapper.properties"

:: Scarica gradle-wrapper.jar
set TEMP_ZIP=gradle-wrapper-temp.zip
powershell -Command "Invoke-WebRequest -Uri https://services.gradle.org/distributions/gradle-8.3-bin.zip -OutFile %TEMP_ZIP%"

:: Estrai solo gradle-wrapper.jar
powershell -Command ^
  "Add-Type -AssemblyName System.IO.Compression.FileSystem; ^
   [IO.Compression.ZipFile]::ExtractToDirectory('%TEMP_ZIP%', 'gradle-temp')"

move /Y gradle-temp\gradle-8.3\lib\gradle-wrapper.jar gradle\wrapper\

rd /S /Q gradle-temp
del %TEMP_ZIP%

echo [OK] Wrapper ufficiale configurato.

:build
echo.
echo [INFO] Eseguo build con Gradle Wrapper...
call gradlew.bat build

if exist build\libs\ersambucone-client-1.0.0.jar (
    if not exist output mkdir output
    copy /Y build\libs\ersambucone-client-1.0.0.jar output\
    echo [OK] Build completata. Jar copiato in output\
) else (
    echo [ERROR] Build fallita. Controlla gli errori sopra.
)

pause
