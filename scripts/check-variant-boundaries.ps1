param([string]$Root = (Split-Path -Parent $PSScriptRoot))
$ErrorActionPreference = 'Stop'
& (Join-Path $PSScriptRoot 'check-mvi-boundaries.ps1') -Root $Root
& (Join-Path $PSScriptRoot 'check-permission-ad-boundaries.ps1') -Root $Root
Write-Output 'Variant boundaries passed against the active multi-module source tree.'
