param(
    [string]$Root = (Get-Location).Path
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
    if ([int]$config.version.code -ne 1 -or [string]$config.version.name -ne '1.0.0') {
        Add-Failure 'Storage Clean initial version must be 1.0.0 (1)'
    }

    foreach ($property in @('privacyUrl', 'termsUrl')) {
        if ([string]$config.legal.$property -notmatch '^https://') {
            Add-Failure "legal.$property must use HTTPS"
        }
    }

    foreach ($forbiddenProperty in @('advertising', 'analytics', 'services')) {
        if ($config.PSObject.Properties.Name -contains $forbiddenProperty) {
            Add-Failure "product.json must not define $forbiddenProperty"
        }
    }
    if (Test-Path -LiteralPath (Join-Path $rootPath 'config/ad_policy.json')) {
        Add-Failure 'config/ad_policy.json must not exist'
    }

    if (Test-Path -LiteralPath (Join-Path $rootPath 'app/google-services.json')) {
        Add-Failure 'app/google-services.json must not exist in the white package'
    }

    $localPath = Join-Path $rootPath 'config/product.local.properties'
    if (Test-Path -LiteralPath $localPath) {
        $local = Get-Content -LiteralPath $localPath -Raw | ConvertFrom-StringData
        $advKeys = @($local.GetEnumerator() | Where-Object { $_.Key -like 'ADV_*' })
        if ($advKeys.Count -gt 0) { Add-Failure 'product.local.properties must not define ADV_* keys' }
    }

    if ($failures.Count -gt 0) {
        Write-Host 'Product config check failed:'
        $failures | ForEach-Object { Write-Host "- $_" }
        exit 1
    }

    Write-Host "Product config check passed for Storage Clean ($($config.identity.applicationId))."
    exit 0
} catch {
    Write-Host ('Product config check could not complete: ' + $_.Exception.Message)
    exit 2
}
