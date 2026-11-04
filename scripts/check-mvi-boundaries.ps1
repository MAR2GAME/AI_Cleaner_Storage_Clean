param([string]$Root = (Split-Path -Parent $PSScriptRoot))

$ErrorActionPreference = 'Stop'
$repositoryRoot = (Resolve-Path -LiteralPath $Root).Path
$mainRoot = Join-Path $repositoryRoot 'app/src/main/java'
$namespace = 'com.aicleanerstorage.spaceclean'
$packageRoot = Join-Path $mainRoot 'com/aicleanerstorage/spaceclean'
$failures = [Collections.Generic.List[string]]::new()
$required = @(
    'core/presentation/MviViewModel.kt',
    'core/presentation/IntentPolicy.kt',
    'core/presentation/IntentInterceptor.kt',
    'core/presentation/InterceptResult.kt',
    'feature/junkclean/presentation/JunkCleanContract.kt',
    'feature/junkclean/presentation/JunkCleanReducer.kt',
    'feature/junkclean/presentation/JunkCleanViewModel.kt',
    'feature/junkclean/ui/JunkCleanRoute.kt',
    'feature/junkclean/ui/JunkCleanEffectHandler.kt',
    'feature/junkclean/ui/JunkDeleteAuthorizationLauncher.kt',
    'feature/junkclean/ui/JunkCleanScreen.kt',
    'feature/junkclean/domain/CleanRepository.kt',
    'feature/junkclean/domain/CleanSessionStore.kt',
    'feature/junkclean/data/CleanRepositoryImpl.kt',
    'feature/junkclean/data/session/SharedScanState.kt'
)
foreach ($relative in $required) {
    if (-not (Test-Path -LiteralPath (Join-Path $packageRoot $relative) -PathType Leaf)) {
        $failures.Add("Missing required source: $relative")
    }
}

$checked = 0
foreach ($sourceSet in @('main', 'test', 'androidTest')) {
    $sourceRoot = Join-Path $repositoryRoot "app/src/$sourceSet/java"
    if (-not (Test-Path -LiteralPath $sourceRoot)) { continue }
    foreach ($file in Get-ChildItem -LiteralPath $sourceRoot -Recurse -File -Filter '*.kt') {
        $text = [IO.File]::ReadAllText($file.FullName)
        $relative = [IO.Path]::GetRelativePath($sourceRoot, $file.FullName).Replace('\', '/')
        $code = [regex]::Replace($text, '(?s)/\*.*?\*/|(?m)//[^\r\n]*', '')
        if ($code -match 'com\.aicleanerstorage\.spaceclean\.common\.presentation\b') {
            $failures.Add("${relative}: old MVI package reference")
        }
        if ($code -cmatch 'com\.aicleanerstorage\.spaceclean\.feature\.junkclean\.(?:(?:model|scanner)\b|[A-Z]\w*)') {
            $failures.Add("${relative}: old junkclean package reference")
        }
        $package = [regex]::Match($code, '(?m)^\s*package\s+([\w.]+)').Groups[1].Value
        $scoped = $package -match '\.(core\.presentation|feature\.junkclean)(\.|$)' -or
            $relative -match '/(core/presentation|feature/junkclean)/'
        if (-not $scoped) { continue }
        $checked++
        $expected = ([IO.Path]::GetDirectoryName($relative)).Replace('\', '.').Replace('/', '.')
        if ($package -ne $expected) { $failures.Add("${relative}: package '$package' differs from '$expected'") }
        if ($sourceSet -ne 'main') { continue }

        $forbidden = @()
        if ($package -match '\.core\.presentation(\.|$)') {
            $forbidden += 'com\.aicleanerstorage\.spaceclean\.(app|feature)\.'
        }
        if ($package -match '\.feature\.junkclean\.domain(\.|$)') {
            $forbidden += '\b(android|androidx)\.'
            $forbidden += 'com\.aicleanerstorage\.spaceclean\.(app\.|feature\.junkclean\.(presentation|ui|data)\.|common\.ui\.)'
        }
        if ($package -match '\.feature\.junkclean\.presentation(\.|$)') {
            $forbidden += '\bandroid\.'
            $forbidden += 'com\.aicleanerstorage\.spaceclean\.(app\.|feature\.junkclean\.(ui|data)\.)'
            $forbidden += '\bimport\s+[^\r\n]*RepositoryImpl\b'
        }
        if ($package -match '\.feature\.junkclean\.data(\.|$)') {
            $forbidden += 'com\.aicleanerstorage\.spaceclean\.(app\.|feature\.junkclean\.(presentation|ui)\.)'
        }
        if ($package -match '\.feature\.junkclean\.ui(\.|$)') {
            $forbidden += 'com\.aicleanerstorage\.spaceclean\.(app\.|feature\.junkclean\.data\.)'
        }
        if ($file.Name -like '*Screen.kt') {
            $forbidden += '\b\w*(ViewModel|Repository|Navigator)\b|\bAppDestination\b'
        }
        foreach ($pattern in $forbidden) {
            if ($code -match $pattern) { $failures.Add("${relative}: forbidden dependency '$($Matches[0])'") }
        }
    }
}
if ($checked -eq 0) { $failures.Add('No scoped Kotlin sources were checked') }
if ($failures.Count -gt 0) {
    $failures | ForEach-Object { Write-Output $_ }
    throw "MVI boundary check failed: $($failures.Count) violation(s)"
}
Write-Output "MVI boundary check passed: $checked scoped Kotlin files; old MVI references checked across all source sets."
