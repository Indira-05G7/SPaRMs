# SPaRMS Packaging and Distribution Script
# This script compiles the Java sources and packages them into a clean, deployable distribution folder.

# 1. Clean previous build and distribution outputs
if (Test-Path -Path dist) {
    Remove-Item -Path dist -Recurse -Force
}
if (Test-Path -Path bin) {
    Remove-Item -Path bin -Recurse -Force
}

# 2. Re-create directories
New-Item -ItemType Directory -Path bin -Force
New-Item -ItemType Directory -Path dist -Force
New-Item -ItemType Directory -Path dist\lib -Force

# 3. Copy resource files (SQL schemas, images, lookups) into the compilation output folder
if (Test-Path -Path src\resources) {
    New-Item -ItemType Directory -Path bin\resources -Force
    Copy-Item -Path src\resources\* -Destination bin\resources -Force
    Write-Host "Resources copied successfully to build directory."
}

# 4. Find and compile all Java source files
$javaFiles = Get-ChildItem -Path src -Filter *.java -Recurse | Select-Object -ExpandProperty FullName
if ($javaFiles.Count -eq 0) {
    Write-Error "No Java source files found in src/."
    exit 1
}

Write-Host "Compiling $($javaFiles.Count) Java source files..."
& javac -cp "lib/*" -d bin -encoding UTF-8 $javaFiles

if ($LASTEXITCODE -ne 0) {
    Write-Error "Compilation failed. Packaging aborted."
    exit 1
}
Write-Host "Compilation successful!"

# 5. Create the JAR manifest file
# Specifying Main-Class and Class-Path relative to the JAR location
$manifestContent = @"
Manifest-Version: 1.0
Main-Class: main.Main
Class-Path: lib/flatlaf-3.1.1.jar lib/mysql-connector-j-8.0.33.jar

"@
$manifestContent | Out-File -FilePath bin\manifest.txt -Encoding ascii -NoNewline

# 6. Package compiled classes and resources into the executable JAR
Write-Host "Packaging classes into executable JAR (dist/SPaRMS.jar)..."
& jar cfm dist\SPaRMS.jar bin\manifest.txt -C bin/ .

# Remove temporary manifest from build folder
Remove-Item -Path bin\manifest.txt -Force

# 7. Copy libraries to the distribution lib/ folder
Write-Host "Copying library dependencies to dist/lib..."
Copy-Item -Path lib\* -Destination dist\lib -Force

# 8. Copy database configuration file to distribution folder
if (Test-Path -Path config.properties) {
    Copy-Item -Path config.properties -Destination dist\config.properties -Force
    Write-Host "Copied existing config.properties to dist/."
} else {
    Write-Host "Creating default config.properties in dist/..."
    $defaultConfig = @"
# SPaRMS Database Configuration
db.host=localhost
db.port=3306
db.name=sparms_db
db.user=root
db.password=Indira@0201

# Path to mysqldump executable for database backups
# (Backups will fall back to JDBC-based scripts if this path is incorrect or missing)
mysql.dump.path=C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe
"@
    $defaultConfig | Out-File -FilePath dist\config.properties -Encoding utf8
}

# 9. Create the run.bat launch helper script for Windows users
Write-Host "Generating double-click launcher (dist/run.bat)..."
$runBatContent = @"
@echo off
title SPaRMS Campus Placement & Recruitment System
echo =======================================================================
echo     Smart Placement & Recruitment Management System (SPaRMS)
echo =======================================================================
echo Launching application...
java -jar SPaRMS.jar
if %ERRORLEVEL% neq 0 (
    echo.
    echo Application exited with code %ERRORLEVEL%.
    echo Please verify that MySQL is running and database configuration in config.properties is correct.
    echo.
    pause
)
"@
$runBatContent | Out-File -FilePath dist\run.bat -Encoding ascii

Write-Host ""
Write-Host "======================================================================="
Write-Host "  SPaRMS Distribution Pack Created Successfully!"
Write-Host "======================================================================="
Write-Host "  Location:  $(Resolve-Path -Path dist)"
Write-Host "  Files included:"
Write-Host "    - SPaRMS.jar        (Application core)"
Write-Host "    - config.properties (Configure DB connection details here)"
Write-Host "    - run.bat           (Double-click startup script for Windows)"
Write-Host "    - lib/              (Required dependencies)"
Write-Host "======================================================================="
