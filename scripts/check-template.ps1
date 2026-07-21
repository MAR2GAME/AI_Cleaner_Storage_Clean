param(
    [string]$ExpectedProfileKey = 'ai_cleaner_storage_clean'
)

$ErrorActionPreference = 'Stop'
$root = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$config = Get-Content -LiteralPath (Join-Path $root 'config/product.json') -Raw | ConvertFrom-Json
$manifestPath = Join-Path $root 'app/src/main/AndroidManifest.xml'
$sourcePath = Join-Path $root 'app/src/main/java'

if ($config.identity.profileKey -ne $ExpectedProfileKey) {
    throw "Expected profileKey '$ExpectedProfileKey', found '$($config.identity.profileKey)'."
}
if ($config.identity.applicationId -ne 'com.aicleanerstorage.spaceclean') {
    throw "Unexpected applicationId '$($config.identity.applicationId)'."
}
if (Test-Path -LiteralPath (Join-Path $root 'app/google-services.json')) {
    throw 'google-services.json must not be present in the white package.'
}

$manifest = Get-Content -LiteralPath $manifestPath -Raw
$packagePattern = [regex]::Escape([string]$config.identity.applicationId)
$componentPattern = 'android:name="({0}[^"]+)"' -f $packagePattern
$componentNames = [regex]::Matches($manifest, $componentPattern) |
    ForEach-Object { $_.Groups[1].Value }
$sources = Get-ChildItem -LiteralPath $sourcePath -Recurse -Filter *.kt |
    ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }
foreach ($component in $componentNames) {
    $className = $component.Substring($component.LastIndexOf('.') + 1)
    if (-not ($sources -match "(class|object)\s+$className\b")) {
        throw "Manifest component has no matching Kotlin class: $component"
    }
}

foreach ($relativePath in @(
    'app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml',
    'app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml'
)) {
    if (-not (Test-Path -LiteralPath (Join-Path $root $relativePath))) {
        throw "Missing launcher resource: $relativePath"
    }
}

Write-Host "Template check passed: $($config.identity.applicationId) ($ExpectedProfileKey)"
