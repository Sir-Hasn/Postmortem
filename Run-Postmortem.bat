@echo off
setlocal EnableDelayedExpansion

set "PROJECT_DIR=%~dp0"
set "SRC_DIR=%PROJECT_DIR%src"
set "OUT_DIR=%PROJECT_DIR%out\production\Main"
set "SQLITE_JAR=%PROJECT_DIR%lib\sqlite-jdbc-3.51.2.0.jar"

set "FX_LIB=%USERPROFILE%\Desktop\javafx-sdk-25.0.1\lib"
if not exist "%FX_LIB%\javafx.controls.jar" set "FX_LIB=%USERPROFILE%\Downloads\javafx-sdk-25.0.2\lib"

if not exist "%FX_LIB%\javafx.controls.jar" (
  echo JavaFX SDK not found.
  echo Expected one of:
  echo   %USERPROFILE%\Desktop\javafx-sdk-25.0.1\lib
  echo   %USERPROFILE%\Downloads\javafx-sdk-25.0.2\lib
  echo.
  echo Edit this file and set FX_LIB to your JavaFX SDK lib folder.
  pause
  exit /b 1
)

if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"

pushd "%PROJECT_DIR%"
break > "%TEMP%\postmortem_sources.txt"
for /r "src" %%F in (*.java) do (
  set "REL=%%F"
  set "REL=!REL:%PROJECT_DIR%=!"
  echo !REL!>> "%TEMP%\postmortem_sources.txt"
)

echo Compiling Postmortem...
javac --module-path "%FX_LIB%" --add-modules javafx.controls,javafx.fxml -cp "%OUT_DIR%;%SQLITE_JAR%" -d "%OUT_DIR%" @"%TEMP%\postmortem_sources.txt"
if errorlevel 1 (
  echo.
  echo Build failed.
  popd
  pause
  exit /b 1
)

xcopy /E /I /Y "%SRC_DIR%\postmortem\resources" "%OUT_DIR%\postmortem\resources" >nul

echo Launching Postmortem...
java --module-path "%FX_LIB%" --add-modules javafx.controls,javafx.fxml -cp "%OUT_DIR%;%SQLITE_JAR%" postmortem.Main

popd

endlocal
