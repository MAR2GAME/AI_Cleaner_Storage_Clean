# Installs the repository hooks so pre-commit runs scripts/check-rules.ps1.
# Usage: .\scripts\install-hooks.ps1
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot

git -C $root config core.hooksPath .githooks
if ($LASTEXITCODE -ne 0) { throw 'Failed to set core.hooksPath' }

Write-Host 'pre-commit hook installed (.githooks)'
