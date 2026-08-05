# Build a Windows .msi with a bundled Java runtime (jpackage).
# Requires: JDK 17+ with jpackage, Maven 3.8+, WiX Toolset 3.x on PATH
# Run from PowerShell:  .\scripts\package-windows.ps1

$ErrorActionPreference = "Stop"

$Root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Set-Location $Root

$AppName = "Floor Estimate Pro"
$AppVersion = if ($env:APP_VERSION) { $env:APP_VERSION } else { "1.0.0" }
$JarName = "floor-estimate-pro.jar"
$DistDir = Join-Path $Root "dist\windows"
$InputDir = Join-Path $Root "target\jpackage-input"

Write-Host "==> Building fat JAR"
& mvn -B -q package "-DskipTests"
if ($LASTEXITCODE -ne 0) { throw "Maven package failed" }

New-Item -ItemType Directory -Force -Path $InputDir | Out-Null
Copy-Item (Join-Path $Root "target\$JarName") (Join-Path $InputDir $JarName) -Force

if (Test-Path $DistDir) { Remove-Item -Recurse -Force $DistDir }
New-Item -ItemType Directory -Force -Path $DistDir | Out-Null

$jpackage = Get-Command jpackage -ErrorAction SilentlyContinue
if (-not $jpackage) {
    throw "jpackage not found. Install JDK 17+ and ensure bin/ is on PATH."
}

Write-Host "==> Running jpackage (msi)"
& jpackage `
  --type msi `
  --name $AppName `
  --app-version $AppVersion `
  --input $InputDir `
  --main-jar $JarName `
  --main-class com.floorestimatepro.App `
  --dest $DistDir `
  --vendor "Floor Estimate Pro" `
  --description "Floor-plan square-footage and cost estimates" `
  --win-menu `
  --win-shortcut `
  --java-options "-Dfile.encoding=UTF-8"

if ($LASTEXITCODE -ne 0) { throw "jpackage failed (is WiX Toolset installed?)" }

Write-Host "==> Done: $DistDir"
Get-ChildItem $DistDir
