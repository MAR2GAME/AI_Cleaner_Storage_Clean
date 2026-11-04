param([string]$Root = (Split-Path -Parent $PSScriptRoot))

$ErrorActionPreference = 'Stop'
$sourceRoot = Join-Path $Root 'app/src'
$mainRoot = Join-Path $sourceRoot 'main/java/com/aicleanerstorage/spaceclean'
$failures = [Collections.Generic.List[string]]::new()
$obsolete = '\b(PermissionCoordinator|AppPermissionCoordinator|PermissionSession|PermissionLaunch|PermissionEngine|PermissionHandler|RuntimePermissionDenialStore|AdRuntime|AdRuntimeFacade|InterstitialExecutor|InterstitialAdRunner|RuntimeAdDriver|AdFeatureFlow|LocalFeatureFlow|AdNavigationPolicy)\b'
$main = @{}
foreach ($file in Get-ChildItem -LiteralPath $sourceRoot -Recurse -File -Filter '*.kt') {
    $code = [IO.File]::ReadAllText($file.FullName)
    $code = [regex]::Replace($code, '(?s)/\*.*?\*/|(?m)//[^\r\n]*', '')
    if ($code -match $obsolete) { $failures.Add("Old framework reference: $($file.FullName)") }
    if ($file.FullName.StartsWith([IO.Path]::GetFullPath($mainRoot))) {
        $relative = [IO.Path]::GetRelativePath($mainRoot, $file.FullName).Replace('\', '/')
        $main[$relative] = $code
    }
}

foreach ($entry in $main.GetEnumerator()) {
    if ($entry.Value -match 'AdvertiseSdkAdapter\.showInterstitial\(' -and $entry.Key -ne 'app/commercial/AdvertiseAdDriver.kt') {
        $failures.Add("SDK playback bypasses Driver: $($entry.Key)")
    }
    if ($entry.Value -match 'ActivityResultContracts\.Request(Multiple)?Permissions\(' -and $entry.Key -ne 'app/permission/AppPermissionHost.kt') {
        $failures.Add("Additional permission launcher: $($entry.Key)")
    }
    if ($entry.Key -match '(^app/commercial/|^common/ads/(AdDriver|ColdStartAds|ExternalAppOpenGuard)\.kt$)' -and $entry.Value -match 'import .*app\.navigation\.') {
        $failures.Add("Playback depends on navigation: $($entry.Key)")
    }
}

$allMain = ($main.Values -join "`n")
if ([regex]::Matches($allMain, 'class\s+\w+[^\{]*:\s*FeatureFlow\s*\{').Count -ne 1) {
    $failures.Add('FeatureFlow must have exactly one production implementation')
}
if ([regex]::Matches($allMain, 'class\s+\w+[^\{]*:\s*AdDriver\s*\{').Count -ne 1) {
    $failures.Add('AdDriver must have exactly one production implementation')
}
$bindings = $main['app/di/PresentationModule.kt']
if ($bindings -notmatch 'single\s*\{\s*PermissionInterceptor\(' -or
    $bindings -notmatch 'single<PermissionGateway>\s*\{\s*get<PermissionInterceptor>\(\)\s*\}') {
    $failures.Add('PermissionGateway must bind directly to the singleton interceptor')
}
if ([regex]::Matches($allMain, '\bAppPermissionHost\(').Count -ne 2) {
    $failures.Add('Expected one permission Host definition and one root mount')
}
if ($failures.Count -gt 0) {
    $failures | ForEach-Object { Write-Error $_ -ErrorAction Continue }
    exit 1
}
Write-Output "Permission/ad boundaries passed ($($main.Count) production Kotlin files)."
