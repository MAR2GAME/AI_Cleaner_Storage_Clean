param(
    [string]$Root = (Get-Location).Path,
    [switch]$WhitePackage
)

$ErrorActionPreference = 'Stop'
$failures = New-Object 'System.Collections.Generic.List[string]'

function Add-Failure([string]$Message) {
    $failures.Add($Message) | Out-Null
}

try {
    $rootPath = [IO.Path]::GetFullPath((Resolve-Path -LiteralPath $Root).Path)
    $configPath = Join-Path $rootPath 'config/product.json'
    $config = Get-Content -LiteralPath $configPath -Raw -Encoding UTF8 | ConvertFrom-Json

    if ($config.schemaVersion -ne 1) { Add-Failure 'schemaVersion must be 1' }
    if ([string]$config.identity.applicationId -ne 'com.aicleanerstorage.spaceclean') {
        Add-Failure 'identity.applicationId must be com.aicleanerstorage.spaceclean'
    }
    if ([string]$config.identity.appName -ne 'Storage Clean') { Add-Failure 'identity.appName must be Storage Clean' }
    if ([string]$config.identity.profileKey -ne 'ai_cleaner_storage_clean') {
        Add-Failure 'identity.profileKey must be ai_cleaner_storage_clean'
    }
    if ([string]$config.identity.themeKey -ne 'storage_clean') {
        Add-Failure 'identity.themeKey must be storage_clean'
    }
    if ($config.version.code -lt 1 -or [string]$config.version.name -notmatch '^\d+\.\d+\.\d+$') {
        Add-Failure 'version.code must be positive and version.name must use semantic versioning'
    }

    foreach ($property in @('privacyUrl', 'termsUrl')) {
        if ([string]$config.legal.$property -notmatch '^https://') {
            Add-Failure "legal.$property must use HTTPS"
        }
    }

    if ($WhitePackage) {
        foreach ($forbiddenProperty in @('advertising', 'analytics', 'services')) {
            if ($config.PSObject.Properties.Name -contains $forbiddenProperty) {
                Add-Failure "product.json must not define $forbiddenProperty"
            }
        }
    }
    if ($WhitePackage -and (Test-Path -LiteralPath (Join-Path $rootPath 'config/ad_policy.json'))) {
        Add-Failure 'config/ad_policy.json must not exist'
    }

    if ($WhitePackage -and (Test-Path -LiteralPath (Join-Path $rootPath 'app/google-services.json'))) {
        Add-Failure 'app/google-services.json must not exist in the white package'
    }

    $commercialSourcePatterns = if ($WhitePackage) { @(
        'com\.aicleanerstorage\.spaceclean\.common\.(ads|analytics)',
        'com\.pdffox\.adv',
        'com\.google\.android\.gms\.ads',
        'com\.google\.firebase',
        'com\.singular'
    ) } else { @() }
    $sourceFiles = Get-ChildItem -LiteralPath (Join-Path $rootPath 'app/src') -Recurse -File -Filter '*.kt'
    foreach ($pattern in $commercialSourcePatterns) {
        if (Select-String -LiteralPath $sourceFiles.FullName -Pattern $pattern -Quiet) {
            Add-Failure "white package source must not reference commercial integration: $pattern"
        }
    }

    $buildFiles = @(
        (Join-Path $rootPath 'app/build.gradle.kts'),
        (Join-Path $rootPath 'gradle/libs.versions.toml')
    ) | Where-Object { Test-Path -LiteralPath $_ }
    $forbiddenBuildPatterns = @()
    if ($WhitePackage) {
        $forbiddenBuildPatterns = @('pdffox-advertise', 'pdffox\.advertise', 'firebase-crashlytics', 'google-services')
    }
    foreach ($pattern in $forbiddenBuildPatterns) {
        if (Select-String -LiteralPath $buildFiles -Pattern $pattern -Quiet) {
            Add-Failure "white package build must not include commercial integration: $pattern"
        }
    }

    if ($failures.Count -gt 0) {
        Write-Host 'Product config check failed:'
        $failures | ForEach-Object { Write-Host "- $_" }
        exit 1
    }

    if ($WhitePackage) { Write-Host "White product config check passed for Storage Clean ($($config.identity.applicationId))." }
    else { Write-Host "Product config check passed for Storage Clean ($($config.identity.applicationId))." }
    exit 0
} catch {
    Write-Host ('Product config check could not complete: ' + $_.Exception.Message)
    exit 2
}
