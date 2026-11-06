param(
    [string]$ExpectedProfileKey = 'ai_cleaner_storage_clean',
    [switch]$WhitePackage
)

$ErrorActionPreference = 'Stop'
$root = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$config = Get-Content -LiteralPath (Join-Path $root 'config/product.json') -Raw | ConvertFrom-Json
$manifestPath = Join-Path $root 'app/src/main/AndroidManifest.xml'
$modulePaths = @('app') + @(
    Get-ChildItem -LiteralPath (Join-Path $root 'core'), (Join-Path $root 'feature') -Directory |
        ForEach-Object { [IO.Path]::GetRelativePath($root, $_.FullName) }
)

if ($config.identity.profileKey -ne $ExpectedProfileKey) {
    throw "Expected profileKey '$ExpectedProfileKey', found '$($config.identity.profileKey)'."
}
if ($config.identity.applicationId -ne 'com.aicleanerstorage.spaceclean') {
    throw "Unexpected applicationId '$($config.identity.applicationId)'."
}
if ($WhitePackage -and (Test-Path -LiteralPath (Join-Path $root 'app/google-services.json'))) {
    throw 'google-services.json must not be present in the white package.'
}

$manifest = Get-Content -LiteralPath $manifestPath -Raw
$packagePattern = [regex]::Escape([string]$config.identity.applicationId)
$componentPattern = 'android:name="({0}[^"]+)"' -f $packagePattern
$componentNames = [regex]::Matches($manifest, $componentPattern) |
    ForEach-Object { $_.Groups[1].Value }
$sources = foreach ($module in $modulePaths) {
    foreach ($language in @('java', 'kotlin')) {
        $sourcePath = Join-Path $root "$module/src/main/$language"
        if (Test-Path -LiteralPath $sourcePath) {
            Get-ChildItem -LiteralPath $sourcePath -Recurse -Filter *.kt |
                ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }
        }
    }
}
foreach ($component in $componentNames) {
    $className = $component.Substring($component.LastIndexOf('.') + 1)
    if (-not ($sources -match "(class|object)\s+$className\b")) {
        throw "Manifest component has no matching Kotlin class: $component"
    }
}

foreach ($relativePath in @(
    'core/ui/src/main/res/mipmap-anydpi-v26/ic_launcher.xml',
    'core/ui/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml'
)) {
    if (-not (Test-Path -LiteralPath (Join-Path $root $relativePath))) {
        throw "Missing launcher resource: $relativePath"
    }
}

if ($WhitePackage) { Write-Host "White package template check passed: $($config.identity.applicationId) ($ExpectedProfileKey)" }
else { Write-Host "Product template check passed: $($config.identity.applicationId) ($ExpectedProfileKey)" }
