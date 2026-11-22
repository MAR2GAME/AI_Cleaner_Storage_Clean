# Local parity for CI: encoding, MVI rules, unit tests, debug assemble.
# Usage: powershell -NoProfile -ExecutionPolicy Bypass -File scripts\ci.ps1
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$encodingGate = Join-Path $PSScriptRoot 'check-encoding.ps1'
$ruleGate = Join-Path $PSScriptRoot 'check-rules.ps1'

& powershell.exe -NoProfile -ExecutionPolicy Bypass -File $encodingGate
if ($LASTEXITCODE -ne 0) { throw 'Encoding checks failed' }

& powershell.exe -NoProfile -ExecutionPolicy Bypass -File $ruleGate
if ($LASTEXITCODE -ne 0) { throw 'MVI rule checks failed' }

$gradlew = Join-Path $root 'gradlew.bat'

& $gradlew :app:ktlintCheck
if ($LASTEXITCODE -ne 0) { throw 'ktlint failed' }

& $gradlew :app:testDebugUnitTest
if ($LASTEXITCODE -ne 0) { throw 'Unit tests failed' }

& $gradlew :app:assembleDebug
if ($LASTEXITCODE -ne 0) { throw 'assembleDebug failed' }

Write-Host 'CI parity passed'
