# =====================================================================
# Console-Based Stock Trading Platform
# Launcher Script for Windows PowerShell
# =====================================================================

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$jdkPaths = @(
    "C:\Users\$env:USERNAME\jdk\jdk-21.0.6+7\bin",
    "$env:JAVA_HOME\bin",
    "C:\Program Files\Java\jdk-21\bin",
    "C:\Program Files\Java\jdk-17\bin"
)

foreach ($path in $jdkPaths) {
    if (Test-Path "$path\javac.exe") {
        $env:PATH = "$path;$env:PATH"
        break
    }
}

Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host " Starting Console-Based Stock Trading Platform..." -ForegroundColor Cyan
Write-Host "====================================================================" -ForegroundColor Cyan

Write-Host "[*] Compiling Java source files..." -ForegroundColor Yellow
& javac -encoding UTF-8 *.java

if ($LASTEXITCODE -eq 0) {
    Write-Host "[*] Compilation successful! Launching platform...`n" -ForegroundColor Green
    & java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" "-Dstderr.encoding=UTF-8" Main
} else {
    Write-Host "`n[!] Compilation failed. Please ensure JDK 8+ is installed." -ForegroundColor Red
}
