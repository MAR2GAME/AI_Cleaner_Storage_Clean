# MVI rule checks for Storage Clean.
# Usage: .\scripts\check-rules.ps1 [-Profile profiles\android-compose-mvi.json]
# Windows PowerShell 5.1+; messages are ASCII-only on purpose.

param(
    [string]$Profile = 'profiles/android-compose-mvi.json'
)

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$profilePath =
    if ([System.IO.Path]::IsPathRooted($Profile)) { $Profile } else { Join-Path $root $Profile }
if (-not (Test-Path -LiteralPath $profilePath)) {
    Write-Error "Profile not found: $profilePath"
    exit 2
}
$data = Get-Content -LiteralPath $profilePath -Raw | ConvertFrom-Json
$src = Join-Path $root $data.sourceRoot
$pkgPath = $data.packageRoot -replace '\.', '/'
$failures = 0

function Test-CheckEnabled([string]$name) {
    if (-not $data.checks) { return $true }
    $prop = $data.checks.PSObject.Properties[$name]
    if ($null -eq $prop) { return $true }
    return [bool]$prop.Value
}

function Add-Failure([string]$check, [string]$message) {
    $script:failures++
    Write-Host ("FAIL [{0}] {1}" -f $check, $message)
}

function Add-Pass([string]$check) {
    Write-Host ("ok   [{0}]" -f $check)
}

function RelativePath([string]$path) {
    return $path.Substring($root.Length + 1)
}

function Get-KotlinFiles([string]$path) {
    Get-ChildItem -Recurse -LiteralPath $path -Filter *.kt -ErrorAction SilentlyContinue
}

function Run-FilePatternCheck(
    [string]$check,
    [string]$path,
    [string[]]$patterns,
    [string]$label
) {
    if (-not (Test-CheckEnabled $check)) { return }
    if (-not (Test-Path -LiteralPath $path)) { return }
    $hits = @()
    foreach ($file in Get-KotlinFiles $path) {
        foreach ($pattern in $patterns) {
            $hits += Select-String -LiteralPath $file.FullName -Pattern $pattern
        }
    }
    if ($hits.Count -eq 0) {
        Add-Pass $check
    } else {
        $hits | Select-Object -First 20 | ForEach-Object {
            Add-Failure $check ("{0}:{1} {2}" -f (RelativePath $_.Path), $_.LineNumber, $label)
        }
    }
}

$domainDir = Join-Path $src "$pkgPath/domain"
$dataDir = Join-Path $src "$pkgPath/data"
$viewDir = Join-Path $src "$pkgPath/app/view"

# 1. domain stays plain Kotlin.
Run-FilePatternCheck 'domainPurity' $domainDir @('^import (android|androidx)\.') 'domain must not import android'

# 2. layer direction.
Run-FilePatternCheck 'layerDirection' $domainDir @('^import com\.aicleanerstorage\.spaceclean\.(app|data|base)\.') 'domain must not import app/data/base'
Run-FilePatternCheck 'layerDirection' $dataDir @('^import com\.aicleanerstorage\.spaceclean\.app\.(view|root)\.') 'data must not import view/root'

# 2b. UI/root never import data implementations.
if (Test-CheckEnabled 'dataImplementationIsolation') {
    $importPattern = '^import ' + [regex]::Escape($data.packageRoot) + '\.data\.'
    $hits = @()
    foreach ($dir in @((Join-Path $src "$pkgPath/app/view"), (Join-Path $src "$pkgPath/app/root"))) {
        if (-not (Test-Path -LiteralPath $dir)) { continue }
        $hits += Get-KotlinFiles $dir | Select-String -Pattern $importPattern
    }
    if ($hits.Count -eq 0) {
        Add-Pass 'dataImplementationIsolation'
    } else {
        $hits | Select-Object -First 20 | ForEach-Object { Add-Failure 'dataImplementationIsolation' ("{0}:{1} view/root must not import data" -f (RelativePath $_.Path), $_.LineNumber) }
    }
}

# 2c. repository implementations stay internal; DI is the only entry point.
if (Test-CheckEnabled 'dataVisibility' -and (Test-Path -LiteralPath $dataDir)) {
    $bad = Get-KotlinFiles $dataDir | Where-Object { $_.Name -like '*RepositoryImpl.kt' } |
        Select-String -Pattern '^(?:public )?(?:abstract |open )?class \w+'
    if ($bad) {
        $bad | Select-Object -First 20 | ForEach-Object { Add-Failure 'dataVisibility' ("{0}:{1} implementation must be internal" -f (RelativePath $_.Path), $_.LineNumber) }
    } else {
        Add-Pass 'dataVisibility'
    }
}

# 2d. domain never depends on the first-party kit modules.
Run-FilePatternCheck 'domainKitIsolation' $domainDir @('^import com\.aicleanerstorage\.(cleanerkit|devicekit|networkkit)\.') 'domain must not import kit modules'

# 3. pages never import the composition root.
Run-FilePatternCheck 'viewDoesNotImportRoot' $viewDir @('^import com\.aicleanerstorage\.spaceclean\.app\.root\.') 'view must not import root'

# 4. ui/part stays stateless.
if (Test-CheckEnabled 'uiPartStateless' -and (Test-Path -LiteralPath $viewDir)) {
    $bad = Get-KotlinFiles $viewDir | Where-Object { $_.FullName -match '[\\/]ui[\\/]part[\\/]' } |
        Select-String -Pattern 'ViewModel|koinInject|collectAsState'
    if ($bad) {
        $bad | Select-Object -First 20 | ForEach-Object { Add-Failure 'uiPartStateless' ("{0}:{1}" -f (RelativePath $_.Path), $_.LineNumber) }
    } else {
        Add-Pass 'uiPartStateless'
    }
}

# 5. user-visible errors go through UiText, never a raw String in state.
Run-FilePatternCheck 'errorText' $viewDir @('errorMessage:\s*String\?') 'state must use UiText, not String'

# 6. effects are collected through base/mvi/CollectEffects only.
if (Test-CheckEnabled 'collectEffects') {
    $hits = Get-KotlinFiles $src | Select-String -Pattern 'viewModel\.event\.collect' |
        Where-Object { $_.Path -notmatch '[\\/]base[\\/]mvi[\\/]CollectEffects\.kt$' }
    if ($hits) {
        $hits | Select-Object -First 20 | ForEach-Object { Add-Failure 'collectEffects' ("{0}:{1}" -f (RelativePath $_.Path), $_.LineNumber) }
    } else {
        Add-Pass 'collectEffects'
    }
}

# 7. back key always dispatches an action.
if (Test-CheckEnabled 'backHandler') {
    $violations = @()
    foreach ($file in Get-KotlinFiles $src) {
        $lines = Get-Content -LiteralPath $file.FullName
        for ($i = 0; $i -lt $lines.Count; $i++) {
            if ($lines[$i] -notmatch 'BackHandler') { continue }
            if ($lines[$i] -match 'import ') { continue }
            $window = ($lines[$i..([Math]::Min($i + 3, $lines.Count - 1))] -join ' ')
            $dispatches = $window -match 'dispatch\(' -or $window -match 'onAction\(' -or
                $window -match 'onBack\s*=\s*::' -or $window -match 'BackHandler\s*\{\s*\}'
            if (-not $dispatches) {
                $violations += ("{0}:{1}" -f (RelativePath $file.FullName), ($i + 1))
            }
        }
    }
    if ($violations.Count -eq 0) {
        Add-Pass 'backHandler'
    } else {
        $violations | Select-Object -First 20 | ForEach-Object { Add-Failure 'backHandler' $_ }
    }
}

# 8. side effects live under ui/effect and follow the naming rule.
if (Test-CheckEnabled 'effectLocation' -and (Test-Path -LiteralPath $viewDir)) {
    $effectFailures = $script:failures
    foreach ($file in Get-KotlinFiles $viewDir) {
        $parent = Split-Path $file.FullName -Parent
        $parentName = Split-Path $parent -Leaf
        if ($parentName -eq 'ui' -and $file.Name -match 'Effect') {
            Add-Failure 'effectLocation' ("{0} must live in ui/effect/" -f (RelativePath $file.FullName))
        }
        if ($parentName -eq 'effect' -and $file.Name -notmatch '(Effect|Effects|EffectHandler|Events)\.kt$') {
            Add-Failure 'effectLocation' ("{0} must be named *Effect.kt / *Effects.kt / *EffectHandler.kt / *Events.kt" -f (RelativePath $file.FullName))
        }
    }
    if ($script:failures -eq $effectFailures) { Add-Pass 'effectLocation' }
}

# 9. package name equals directory path.
if (Test-CheckEnabled 'packageMatchesDirectory') {
    $bad = @()
    foreach ($file in Get-KotlinFiles $src) {
        $relative = $file.FullName.Substring($src.Length + 1)
        $expected = (Split-Path $relative -Parent) -replace '[\\/]', '.'
        $packageLine = (Select-String -LiteralPath $file.FullName -Pattern '^package ' | Select-Object -First 1).Line
        if (-not $packageLine) { continue }
        $package = $packageLine.Substring(8).Trim()
        if ($package -ne $expected) {
            $bad += ("{0} => {1} (expected {2})" -f $relative, $package, $expected)
        }
    }
    if ($bad.Count -eq 0) { Add-Pass 'packageMatchesDirectory' } else { $bad | Select-Object -First 20 | ForEach-Object { Add-Failure 'packageMatchesDirectory' $_ } }
}

# 10. every locale keeps the default key set.
if (Test-CheckEnabled 'localeParity') {
    $resDir = Join-Path $root 'app/src/main/res'
    $defaultFile = Join-Path $resDir 'values/strings.xml'
    $defaultKeys = (Select-String -LiteralPath $defaultFile -Pattern 'name="([^"]+)"' -AllMatches).Matches |
        ForEach-Object { $_.Groups[1].Value }
    $missingTotal = 0
    Get-ChildItem -LiteralPath $resDir -Directory | Where-Object { $_.Name -like 'values-*' } | ForEach-Object {
        $file = Join-Path $_.FullName 'strings.xml'
        if (-not (Test-Path -LiteralPath $file)) { return }
        $keys = (Select-String -LiteralPath $file -Pattern 'name="([^"]+)"' -AllMatches).Matches |
            ForEach-Object { $_.Groups[1].Value }
        $missing = @($defaultKeys | Where-Object { $_ -notin $keys })
        if ($missing.Count -gt 0) {
            $missingTotal += $missing.Count
            Add-Failure 'localeParity' ("{0} missing {1} key(s): {2}" -f $_.Name, $missing.Count, ($missing -join ', '))
        }
    }
    if ($missingTotal -eq 0) { Add-Pass 'localeParity' }
}

if ($failures -gt 0) {
    Write-Host ("{0} failure(s)" -f $failures)
    exit 1
}
Write-Host 'all checks passed'
exit 0
