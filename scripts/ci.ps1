# Local parity for CI: MVI rules, unit tests, debug assemble.
# Usage: powershell -NoProfile -ExecutionPolicy Bypass -File scripts\ci.ps1
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$gate = Join-Path $PSScriptRoot 'check-rules.ps1'

& powershell.exe -NoProfile -ExecutionPolicy Bypass -File $gate
if ($LASTEXITCODE -ne 0) { throw 'MVI rule checks failed' }

$gradlew = Join-Path $root 'gradlew.bat'

& $gradlew :app:testDebugUnitTest
if ($LASTEXITCODE -ne 0) { throw 'Unit tests failed' }

& $gradlew :app:assembleDebug
if ($LASTEXITCODE -ne 0) { throw 'assembleDebug failed' }

Write-Host 'CI parity passed'
