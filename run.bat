@echo off
:: =====================================================================
:: Console-Based Stock Trading Platform
:: Launcher Batch Script for Windows Command Prompt
:: =====================================================================

:: Set console codepage to UTF-8 to properly render the Indian Rupee (?) symbol
chcp 65001 > nul
cls

echo ====================================================================
echo  Starting Console-Based Stock Trading Platform...
echo ====================================================================

:: Detect javac in PATH, or search standard JDK directories
where javac >nul 2>nul
if %errorlevel% neq 0 (
    if exist "C:\Users\%USERNAME%\jdk\jdk-21.0.6+7\bin\javac.exe" (
        set "PATH=C:\Users\%USERNAME%\jdk\jdk-21.0.6+7\bin;%PATH%"
    ) else if defined JAVA_HOME (
        set "PATH=%JAVA_HOME%\bin;%PATH%"
    ) else if exist "C:\Program Files\Java\jdk-21\bin\javac.exe" (
        set "PATH=C:\Program Files\Java\jdk-21\bin;%PATH%"
    )
)

echo [*] Compiling Java source files...
javac -encoding UTF-8 *.java
if %errorlevel% neq 0 (
    echo.
    echo [!] Compilation error detected. Please ensure JDK 8 or higher is installed.
    pause
    exit /b %errorlevel%
)

echo [*] Compilation successful! Launching platform...
echo.
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 Main

pause
