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

# 2e. data holds implementations only: contracts belong to domain.
if (Test-CheckEnabled 'dataContracts' -and (Test-Path -LiteralPath $dataDir)) {
    $bad = Get-KotlinFiles $dataDir | Select-String -Pattern '^interface '
    if ($bad) {
        $bad | Select-Object -First 20 | ForEach-Object { Add-Failure 'dataContracts' ("{0}:{1} data must not declare interfaces" -f (RelativePath $_.Path), $_.LineNumber) }
    } else {
        Add-Pass 'dataContracts'
    }
}

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

# 11. domain models: data class under domain/model/** must be <Concept>Model.
if (Test-CheckEnabled 'domainModelNaming' -and (Test-Path -LiteralPath (Join-Path $domainDir 'model'))) {
    $bad = @()
    foreach ($file in Get-KotlinFiles (Join-Path $domainDir 'model')) {
        $text = Get-Content -LiteralPath $file.FullName -Raw
        foreach ($type in [regex]::Matches($text, '(?m)^(?:(?:public|internal) )?data class (\w+)')) {
            $name = $type.Groups[1].Value
            if ($name -cnotmatch 'Model$' -or $name -cne $file.BaseName) {
                $bad += ("{0}: model data class must be <Concept>Model and match the file" -f (RelativePath $file.FullName))
            }
        }
    }
    if ($bad.Count -eq 0) { Add-Pass 'domainModelNaming' } else {
        $bad | Select-Object -First 20 | ForEach-Object { Add-Failure 'domainModelNaming' $_ }
    }
}

# 12. every error branch in domain/error documents when it happens.
if (Test-CheckEnabled 'errorComments' -and (Test-Path -LiteralPath (Join-Path $domainDir 'error'))) {
    $missing = @()
    foreach ($file in Get-KotlinFiles (Join-Path $domainDir 'error')) {
        $lines = Get-Content -LiteralPath $file.FullName
        for ($i = 0; $i -lt $lines.Count; $i++) {
            if ($lines[$i] -notmatch '^\s*data (object|class)\s+\w+') { continue }
            $previous = $i - 1
            while ($previous -ge 0 -and $lines[$previous].Trim() -eq '') { $previous-- }
            if ($previous -lt 0) { continue }
            if ($lines[$previous].Trim() -notmatch '^(\/\*\*|\*|\/\/)') {
                $missing += ("{0}:{1}" -f (RelativePath $file.FullName), ($i + 1))
            }
        }
    }
    if ($missing.Count -eq 0) { Add-Pass 'errorComments' } else {
        $missing | Select-Object -First 20 | ForEach-Object { Add-Failure 'errorComments' $_ }
    }
}

# 13. one top-level public type per file (shared exceptions, not profile values).
if (Test-CheckEnabled 'publicTypes') {
    $exceptions = '(Contract|ErrorText|Previews|Reducer|Models)\.kt$'
    $exact = @('PreviewFixtures.kt', 'Theme.kt', 'AppThemeTokens.kt', 'AppModule.kt',
        'CollectEffects.kt', 'AppDestination.kt', 'BatteryChartModels.kt', 'VirusAndroidEffects.kt')
    $bad = Get-KotlinFiles $src | Where-Object {
        $_.Name -notmatch $exceptions -and $_.Name -notin $exact
    } | ForEach-Object {
        $text = Get-Content -LiteralPath $_.FullName -Raw
        $types = [regex]::Matches($text, '(?m)^(?:public )?(?:(?:data|sealed|enum|value|abstract|open) )*(?:class|interface|object) (\w+)')
        if ($types.Count -gt 1 -or ($types.Count -eq 1 -and $types[0].Groups[1].Value -cne $_.BaseName)) {
            "{0}: public type/file mismatch or multiple public types" -f (RelativePath $_.FullName)
        }
    }
    if ($bad.Count -eq 0) { Add-Pass 'publicTypes' } else {
        $bad | Select-Object -First 20 | ForEach-Object { Add-Failure 'publicTypes' $_ }
    }
}

# 14. naming: Impl suffix only on <Port>Impl; const val in UPPER_SNAKE_CASE.
if (Test-CheckEnabled 'naming') {
    $bad = Get-KotlinFiles $src | Select-String -CaseSensitive -Pattern `
        '^(internal |)(abstract |open |sealed |)(class|interface|object) (?!\w*RepositoryImpl\b)\w+Impl\b|^\s*(private )?const val (?!([A-Z][A-Z0-9_]*)\b)'
    if ($bad) {
        $bad | Select-Object -First 20 | ForEach-Object {
            Add-Failure 'naming' ("{0}:{1}: {2}" -f (RelativePath $_.Path), $_.LineNumber, $_.Line.Trim())
        }
    } else {
        Add-Pass 'naming'
    }
}

# 15. public domain capabilities must have KDoc; port methods document their contract.
if (Test-CheckEnabled 'domainPublicKdoc' -and (Test-Path -LiteralPath $domainDir)) {
    $missingKdoc = @()
    $domainFiles = @()
    $domainFiles += Get-KotlinFiles (Join-Path $domainDir 'port')
    $domainFiles += Get-KotlinFiles (Join-Path $domainDir 'model')
    foreach ($file in $domainFiles) {
        $lines = Get-Content -LiteralPath $file.FullName
        for ($i = 0; $i -lt $lines.Count; $i++) {
            $line = $lines[$i]
            $isMethod = $line -match '^\s{4}(suspend )?fun\s+'
            $isType = $line -match '^(data class|enum class|sealed interface|sealed class|interface|object|class)\s+'
            if (-not ($isMethod -or $isType)) { continue }
            $previous = $i - 1
            while ($previous -ge 0 -and ($lines[$previous].Trim() -eq '' -or $lines[$previous].Trim().StartsWith('@'))) { $previous-- }
            if ($previous -lt 0 -or $lines[$previous].Trim() -notmatch '^(\/\*\*|\*)') {
                $missingKdoc += ("{0}:{1}" -f (RelativePath $file.FullName), ($i + 1))
            }
        }
    }
    if ($missingKdoc.Count -eq 0) { Add-Pass 'domainPublicKdoc' } else {
        $missingKdoc | Select-Object -First 20 | ForEach-Object { Add-Failure 'domainPublicKdoc' ("{0} missing KDoc" -f $_) }
    }
}

# 16. no wildcard imports.
if (Test-CheckEnabled 'wildcardImport') {
    $wildcards = Get-KotlinFiles $src | Select-String -Pattern '^import .*\.\*$'
    if ($wildcards.Count -eq 0) { Add-Pass 'wildcardImport' } else {
        $wildcards | Select-Object -First 20 | ForEach-Object { Add-Failure 'wildcardImport' ("{0}:{1}" -f (RelativePath $_.Path), $_.LineNumber) }
    }
}

# 17. ui/part never owns navigation.
if (Test-CheckEnabled 'uiPartNoNavigator' -and (Test-Path -LiteralPath $viewDir)) {
    $partFiles = Get-KotlinFiles $viewDir | Where-Object { $_.FullName -match '[\\/]ui[\\/]part[\\/]' }
    $navigatorHits = $partFiles | Select-String -Pattern 'AppNavigator|leaveBack\(|\.navigate\('
    if ($navigatorHits.Count -eq 0) { Add-Pass 'uiPartNoNavigator' } else {
        $navigatorHits | Select-Object -First 20 | ForEach-Object { Add-Failure 'uiPartNoNavigator' ("{0}:{1}" -f (RelativePath $_.Path), $_.LineNumber) }
    }
}

# 18. screens do not dispatch actions from LaunchedEffect.
if (Test-CheckEnabled 'screenNoActionEffect' -and (Test-Path -LiteralPath $viewDir)) {
    $effectHits = @()
    foreach ($file in Get-KotlinFiles $viewDir | Where-Object { $_.Name -like '*Screen.kt' }) {
        $lines = Get-Content -LiteralPath $file.FullName
        for ($i = 0; $i -lt $lines.Count; $i++) {
            if ($lines[$i] -notmatch 'LaunchedEffect\(') { continue }
            $window = ($lines[$i..([Math]::Min($i + 5, $lines.Count - 1))] -join ' ')
            if ($window -match 'onAction\(|dispatch\(') {
                $effectHits += ("{0}:{1}" -f (RelativePath $file.FullName), ($i + 1))
            }
        }
    }
    if ($effectHits.Count -eq 0) { Add-Pass 'screenNoActionEffect' } else {
        $effectHits | Select-Object -First 20 | ForEach-Object { Add-Failure 'screenNoActionEffect' $_ }
    }
}

# 19. Compose functions keep parameters at five or fewer.
if (Test-CheckEnabled 'composableParamLimit' -and (Test-Path -LiteralPath $viewDir)) {
    $overLimit = @()
    foreach ($file in Get-KotlinFiles $viewDir) {
        $lines = Get-Content -LiteralPath $file.FullName
        for ($i = 0; $i -lt $lines.Count; $i++) {
            if ($lines[$i] -notmatch '^fun [A-Za-z].*\(') { continue }
            $params = 0
            $j = $i
            while ($j -lt $lines.Count -and $lines[$j] -notmatch '\)\s*(:|\{|=)') {
                if ($lines[$j] -match '^\s+[a-zA-Z_][a-zA-Z0-9_]*\s*:') { $params++ }
                $j++
            }
            if ($params -gt 5) {
                $overLimit += ("{0}:{1} has {2} parameters" -f (RelativePath $file.FullName), ($i + 1), $params)
            }
        }
    }
    if ($overLimit.Count -eq 0) { Add-Pass 'composableParamLimit' } else {
        $overLimit | Select-Object -First 20 | ForEach-Object { Add-Failure 'composableParamLimit' $_ }
    }
}
# 20. keep functions at forty lines or fewer.
if (Test-CheckEnabled 'functionLengthLimit') {
    $longFunctions = @()
    foreach ($file in Get-KotlinFiles $src) {
        $lines = Get-Content -LiteralPath $file.FullName
        $depth = 0
        $inFunction = $false
        $start = 0
        $name = ''
        for ($i = 0; $i -lt $lines.Count; $i++) {
            $line = $lines[$i]
            if (-not $inFunction -and $line -match '^\s*(private |internal |public )?fun\s+([A-Za-z0-9_]+)') {
                $inFunction = $true
                $start = $i
                $name = $Matches[2]
                $depth = 0
            }
            if (-not $inFunction) { continue }
            $depth += ([regex]::Matches($line, '\{')).Count
            $depth -= ([regex]::Matches($line, '\}')).Count
            if ($depth -le 0 -and $i -gt $start) {
                $length = $i - $start + 1
                if ($length -gt 40) {
                    $longFunctions += ("{0}:{1} {2} has {3} lines" -f (RelativePath $file.FullName), ($start + 1), $name, $length)
                }
                $inFunction = $false
            }
        }
    }
    if ($longFunctions.Count -eq 0) { Add-Pass 'functionLengthLimit' } else {
        $longFunctions | Select-Object -First 20 | ForEach-Object { Add-Failure 'functionLengthLimit' $_ }
    }
}
if ($failures -gt 0) {
    Write-Host ("{0} failure(s)" -f $failures)
    exit 1
}
Write-Host 'all checks passed'
exit 0



