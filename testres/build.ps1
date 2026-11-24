<#
.SYNOPSIS
    Build the two harmless "fake virus" APKs under testres and optionally install them.

.DESCRIPTION
    Outputs:
      testres/fake-virus-files/Android.PUA.Harmless.Test.apk
      testres/fake-virus-files/Android.Safe.Control.apk

    Both APKs are test fixtures only: the PUA app declares risky permissions but
    does nothing else, the Safe app declares no risky permissions.

.PARAMETER Install
    Install both APKs on the connected device via adb (debug signed).

.PARAMETER PushToDevice
    Copy the APKs to /sdcard/Download for deep-scan file testing.

.PARAMETER Uninstall
    Uninstall both test apps.

.PARAMETER Serial
    adb device serial. Defaults to the only connected device.

.EXAMPLE
    .\testres\build.ps1 -Install -PushToDevice

.EXAMPLE
    .\testres\build.ps1 -Uninstall
#>
[CmdletBinding()]
param(
    [switch]$Install,
    [switch]$PushToDevice,
    [switch]$Uninstall,
    [string]$Serial
)

$ErrorActionPreference = 'Stop'
$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$repoRoot = Split-Path -Parent $scriptRoot
$demoDir = Join-Path $scriptRoot 'demo-apps'
$outDir = Join-Path $scriptRoot 'fake-virus-files'

function Get-SdkDir {
    $candidates = @($env:ANDROID_HOME, $env:ANDROID_SDK_ROOT) | Where-Object { $_ }
    foreach ($candidate in $candidates) {
        if (Test-Path $candidate) { return $candidate }
    }
    $localProps = Join-Path $repoRoot 'local.properties'
    if (Test-Path $localProps) {
        $line = Get-Content $localProps | Where-Object { $_ -match '^sdk\.dir=' } | Select-Object -First 1
        if ($line) {
            $value = $line.Substring('sdk.dir='.Length).Replace('\:', ':').Replace('\\', '\')
            if (Test-Path $value) { return $value }
        }
    }
    throw 'Android SDK not found: set ANDROID_HOME or sdk.dir in the root local.properties.'
}

$sdkDir = Get-SdkDir
$adb = Join-Path $sdkDir 'platform-tools\adb.exe'
if (-not (Test-Path $adb)) { $adb = 'adb' }

$adbArgs = @()
if ($Serial) { $adbArgs += @('-s', $Serial) }

if ($Uninstall) {
    Write-Host '==> Uninstalling test apps'
    foreach ($packageName in @('com.aicleanerstorage.testres.pua', 'com.aicleanerstorage.testres.clean')) {
        & $adb @adbArgs uninstall $packageName
    }
    return
}

# The demo apps are a standalone Gradle project and need their own local.properties.
$demoLocal = Join-Path $demoDir 'local.properties'
$escapedSdk = $sdkDir.Replace('\', '\\').Replace(':', '\:')
"sdk.dir=$escapedSdk" | Set-Content -Path $demoLocal -Encoding Ascii

Write-Host "==> Building fake virus APKs ($demoDir)"
& (Join-Path $repoRoot 'gradlew.bat') -p $demoDir :pua-app:assembleDebug :safe-app:assembleDebug --console=plain --no-daemon
if ($LASTEXITCODE -ne 0) { throw "Gradle build failed (exit code $LASTEXITCODE)" }

$puaApk = Join-Path $demoDir 'pua-app\build\outputs\apk\debug\pua-app-debug.apk'
$safeApk = Join-Path $demoDir 'safe-app\build\outputs\apk\debug\safe-app-debug.apk'
foreach ($apk in @($puaApk, $safeApk)) {
    if (-not (Test-Path $apk)) { throw "Missing build output: $apk" }
}

New-Item -ItemType Directory -Force -Path $outDir | Out-Null
$puaOut = Join-Path $outDir 'Android.PUA.Harmless.Test.apk'
$safeOut = Join-Path $outDir 'Android.Safe.Control.apk'
Copy-Item $puaApk $puaOut -Force
Copy-Item $safeApk $safeOut -Force
Write-Host "==> Created: $puaOut"
Write-Host "==> Created: $safeOut"

if ($Install) {
    Write-Host '==> Installing on device'
    & $adb @adbArgs install -r $puaOut
    if ($LASTEXITCODE -ne 0) { throw 'Failed to install the PUA test app.' }
    & $adb @adbArgs install -r $safeOut
    if ($LASTEXITCODE -ne 0) { throw 'Failed to install the Safe test app.' }
}

if ($PushToDevice) {
    Write-Host '==> Pushing APKs to /sdcard/Download (for deep scan)'
    & $adb @adbArgs push $puaOut /sdcard/Download/Android.PUA.Harmless.Test.apk
    & $adb @adbArgs push $puaOut /sdcard/Download/Fake.Trojan.Test.apk
    & $adb @adbArgs push $safeOut /sdcard/Download/Android.Safe.Control.apk
}

Write-Host ''
Write-Host 'Done. Manual test steps:'
Write-Host '  1. Open Storage Clean -> Home -> Virus card -> Clean Now'
Write-Host '  2. Run Quick Scan / Deep Scan and check the result page'
Write-Host '  3. Cloud verdicts decide the result; see testres\README.md'
Write-Host ''
Write-Host 'Cleanup:'
Write-Host '  .\testres\build.ps1 -Uninstall'
Write-Host '  adb shell rm /sdcard/Download/Android.PUA.Harmless.Test.apk'
Write-Host '  adb shell rm /sdcard/Download/Fake.Trojan.Test.apk'
Write-Host '  adb shell rm /sdcard/Download/Android.Safe.Control.apk'
