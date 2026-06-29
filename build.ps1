# Clean and Create Bin folder
if (Test-Path -Path bin) {
    Remove-Item -Path bin -Recurse -Force
}
New-Item -ItemType Directory -Path bin -Force

# Copy Resources folder into bin for runtime classpath resolution
if (Test-Path -Path src\resources) {
    New-Item -ItemType Directory -Path bin\resources -Force
    Copy-Item -Path src\resources\* -Destination bin\resources -Force
    Write-Host "Resources copied successfully."
}

# Find all Java source files recursively
$javaFiles = Get-ChildItem -Path src -Filter *.java -Recurse | Select-Object -ExpandProperty FullName

if ($javaFiles.Count -eq 0) {
    Write-Error "No Java source files found in src/."
    exit 1
}

Write-Host "Compiling $($javaFiles.Count) Java source files..."
& javac -cp "lib/*" -d bin -encoding UTF-8 $javaFiles

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation successful!"
    Write-Host "Launching SPaRMS..."
    & java -cp "bin;lib/*" main.Main
} else {
    Write-Error "Compilation failed."
}
