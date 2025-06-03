@echo off
setlocal

echo [INFO] Creo o aggiorno settings.gradle...

(
    echo pluginManagement {
    echo     repositories {
    echo         maven { url = 'https://maven.fabricmc.net/' }
    echo         gradlePluginPortal()
    echo         mavenCentral()
    echo     }
    echo }
    echo.
    echo rootProject.name = 'ersambucone-client'
) > settings.gradle

echo [OK] settings.gradle aggiornato correttamente.
pause
