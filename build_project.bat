@echo off
echo ========================================
echo  COMPILANDO PROYECTO IoTMobileApp
echo ========================================
echo.

cd /d C:\AndroidProjects\IoTMobileApp

echo Limpiando proyecto...
call gradlew.bat clean

echo.
echo Compilando proyecto...
call gradlew.bat assembleDebug

echo.
echo ========================================
echo  COMPILACION COMPLETA
echo ========================================
echo.
echo Si no hubo errores, ahora puedes ejecutar la app en Android Studio.
echo Presiona cualquier tecla para cerrar...
pause > nul
