param([string]$Root = (Split-Path -Parent $PSScriptRoot))
$ErrorActionPreference = 'Stop'
$repositoryRoot = (Resolve-Path -LiteralPath $Root).Path
$failures = [Collections.Generic.List[string]]::new()
$modules = @('app', 'core/contracts', 'core/navigation', 'core/presentation', 'core/ui') + @(
    'startup', 'onboarding', 'home', 'settings', 'junkclean', 'antivirus', 'files', 'applock', 'notificationcleaner', 'toolbox'
) | ForEach-Object { if ($_ -match '^(app$|core/)') { $_ } else { "feature/$_" } }
$checked = 0
foreach ($module in $modules) {
    $moduleRoot = Join-Path $repositoryRoot $module
    if (-not (Test-Path (Join-Path $moduleRoot 'build.gradle.kts'))) { $failures.Add("Missing module: $module"); continue }
    $gradle = [IO.File]::ReadAllText((Join-Path $moduleRoot 'build.gradle.kts'))
    if ($module -ne 'app' -and $gradle -match 'project\(":(app|feature:)') { $failures.Add("$module has an upstream module dependency") }
    foreach ($set in @('main', 'test', 'androidTest')) {
        foreach ($language in @('java', 'kotlin')) {
            $sourceRoot = Join-Path $moduleRoot "src/$set/$language"
            if (-not (Test-Path $sourceRoot)) { continue }
            foreach ($file in Get-ChildItem $sourceRoot -Recurse -File -Filter '*.kt') {
                $text = [IO.File]::ReadAllText($file.FullName)
                $package = [regex]::Match($text, '(?m)^package ([\w.]+)').Groups[1].Value
                if (-not $package) { continue }
                $checked++
                $relative = [IO.Path]::GetRelativePath($sourceRoot, $file.FullName).Replace('\', '/')
                if ($package -match '\.common(\.|$)') { $failures.Add("$module/$relative uses retired common package: $package") }
                $expected = [IO.Path]::GetDirectoryName($relative).Replace('\', '.').Replace('/', '.')
                if ($package -ne $expected) { $failures.Add("$module/$relative package does not match directory") }
                $imports = [regex]::Matches($text, '(?m)^import ([^\r\n]+)') | ForEach-Object { $_.Groups[1].Value }
                foreach ($import in $imports) {
                    if ($import -match '^com\.aicleanerstorage\.spaceclean\.common\.') { $failures.Add("$module/$relative imports old common package: $import") }
                    if ($set -ne 'main') { continue }
                    if ($module.StartsWith('core/') -and $import -match '^com\.aicleanerstorage\.spaceclean\.(app|feature)\.') { $failures.Add("$module/${relative}: core depends on implementation $import") }
                    if ($module.StartsWith('feature/')) {
                        $name = $module.Split('/')[1]
                        if ($import -match '^com\.aicleanerstorage\.spaceclean\.app\.' -or ($import -match '^com\.aicleanerstorage\.spaceclean\.feature\.([^\.]+)\.' -and $Matches[1] -ne $name)) { $failures.Add("$module/$relative depends on another module: $import") }
                        if ($package -match '\.domain(\.|$)' -and $import -match '^(android|androidx)\.|\.feature\.[^.]+\.(data|view)\.') { $failures.Add("$module/$relative domain depends on $import") }
                        if ($package -match '\.presentation(\.|$)' -and $import -match '^android\.|\.feature\.[^.]+\.data\.|\.view\.[^.]+\.ui\.') { $failures.Add("$module/$relative presentation depends on $import") }
                        if ($package -match '\.data(\.|$)' -and $import -match '\.feature\.[^.]+\.view\.') { $failures.Add("$module/$relative data depends on $import") }
                    }
                }
                if ($set -eq 'main' -and ($module.StartsWith('feature/') -or $module -eq 'core/ui')) {
                    $pureUiFile = $file.Name -match '(?i)(Screens?|Views?|ContentView|ResultView|Components)\.kt$'
                    if ($pureUiFile -and $text -match '\b(AppDestination|AppNavigator|NavHostController|\w*ViewModel|\w*Repository|ActivityResultLauncher|koinViewModel|rememberLauncherForActivityResult)\b') { $failures.Add("$module/$relative contains screen-side orchestration or app navigation") }
                    if ($file.Name -like '*ViewModel.kt' -and $text -match '(MutableStateFlow|MutableSharedFlow)\s*[<(]') { $failures.Add("$module/$relative owns a second state/effect source") }
                }
            }
        }
    }
}
if ($checked -eq 0) { $failures.Add('No sources checked') }
if ($failures.Count) { $failures | Write-Output; throw "Module/MVI boundary check failed: $($failures.Count)" }
Write-Output "Module/MVI boundaries passed: $checked Kotlin sources in $($modules.Count) modules."
