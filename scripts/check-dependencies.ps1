param(
    [string]$Root = (Split-Path -Parent $PSScriptRoot),
    [switch]$Strict
)
$ErrorActionPreference = 'Stop'
$repositoryRoot = (Resolve-Path -LiteralPath $Root).Path
& (Join-Path $PSScriptRoot 'check-mvi-boundaries.ps1') -Root $repositoryRoot
$failures = [Collections.Generic.List[string]]::new()
$modules = @('app') + @(Get-ChildItem -LiteralPath (Join-Path $repositoryRoot 'core'), (Join-Path $repositoryRoot 'feature') -Directory |
    ForEach-Object { [IO.Path]::GetRelativePath($repositoryRoot, $_.FullName) })
$sdkPattern = '(?m)^import (com\.pdffox\.adv\.|com\.google\.android\.gms\.ads\.|com\.google\.firebase\.|com\.facebook\.|com\.pangle\.|com\.tiktok\.|com\.singular\.|thinkingdata\.|com\.trustlook\.)'
foreach ($module in $modules) {
    foreach ($language in @('java', 'kotlin')) {
        $source = Join-Path $repositoryRoot "$module/src/main/$language"
        if (-not (Test-Path -LiteralPath $source)) { continue }
        foreach ($file in Get-ChildItem -LiteralPath $source -Recurse -File -Filter '*.kt') {
            $relative = [IO.Path]::GetRelativePath($repositoryRoot, $file.FullName).Replace('\', '/')
            $code = [IO.File]::ReadAllText($file.FullName)
            if ($code -match $sdkPattern -and $relative -notmatch '/app/platform/|/feature/antivirus/data/') {
                $failures.Add("SDK imported outside an adapter: $relative")
            }
        }
    }
}
if ($failures.Count) { $failures | Write-Output; throw 'Dependency boundary check failed' }
Write-Output 'Dependency boundary check passed (all module boundaries enforced).'
