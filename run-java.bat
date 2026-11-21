@echo off
cd /d "%~dp0"
if exist out rmdir /s /q out
mkdir out
 dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d out @sources.txt
if errorlevel 1 (
  echo.
  echo Compilation failed. Check the errors above.
  del sources.txt
  pause
  exit /b 1
)
del sources.txt
java -cp out com.smartcampus.main.Main --console
pause
