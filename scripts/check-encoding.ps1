# Strict UTF-8 check for source, docs and build configuration.
# Usage: .\scripts\check-encoding.ps1 [-Root <repo root>]
# Windows PowerShell 5.1+; messages are ASCII-only on purpose.

param(
    [string]$Root = (Split-Path -Parent $PSScriptRoot)
)

$ErrorActionPreference = 'Stop'
$repositoryRoot = (Resolve-Path -LiteralPath $Root).Path
$strictUtf8 = New-Object System.Text.UTF8Encoding($false, $true)

$scanRoots = @(
    (Join-Path $repositoryRoot 'app/src'),
    (Join-Path $repositoryRoot 'docs'),
    (Join-Path $repositoryRoot 'scripts'),
    (Join-Path $repositoryRoot 'config'),
    (Join-Path $repositoryRoot 'profiles'),
    (Join-Path $repositoryRoot 'gradle')
)

$scanFiles = @()
foreach ($root in $scanRoots) {
    if (-not (Test-Path -LiteralPath $root)) { continue }
    $scanFiles += Get-ChildItem -LiteralPath $root -Recurse -File -ErrorAction SilentlyContinue
}

foreach ($name in @('AGENTS.md', 'README.md')) {
    $file = Join-Path $repositoryRoot $name
    if (Test-Path -LiteralPath $file) { $scanFiles += Get-Item -LiteralPath $file }
}

$extensions = @(
    '.kt', '.kts', '.md', '.xml', '.json', '.ps1', '.toml',
    '.properties', '.yml', '.yaml', '.gradle', '.pro', '.txt'
)

# High-signal byte sequences produced when UTF-8 Chinese is decoded as GBK.
$mojibakePatterns = @(
    '锟斤拷',
    '鍨冨溇',
    '鏂囦欢',
    '鐨勬',
    '鈥'
)

$failures = New-Object 'System.Collections.Generic.List[string]'
$checked = 0

foreach ($file in $scanFiles) {
    if ($file.FullName -eq $PSCommandPath) { continue }
    if ($extensions -notcontains $file.Extension.ToLowerInvariant()) { continue }
    $checked++
    $bytes = [System.IO.File]::ReadAllBytes($file.FullName)
    try {
        $text = $strictUtf8.GetString($bytes)
    } catch {
        $relative = $file.FullName.Substring($repositoryRoot.Length + 1)
        $failures.Add("$relative : not valid UTF-8")
        continue
    }

    if ($text.Contains([char]0xFFFD)) {
        $relative = $file.FullName.Substring($repositoryRoot.Length + 1)
        $failures.Add("$relative : contains U+FFFD replacement character")
        continue
    }

    foreach ($pattern in $mojibakePatterns) {
        if ($text.Contains($pattern)) {
            $relative = $file.FullName.Substring($repositoryRoot.Length + 1)
            $failures.Add("$relative : contains mojibake sequence '$pattern'")
            break
        }
    }
}

if ($failures.Count -gt 0) {
    $failures | Select-Object -First 40 | ForEach-Object { Write-Host "FAIL $_" }
    Write-Host ("{0} encoding failure(s) in {1} file(s)" -f $failures.Count, $checked)
    exit 1
}

Write-Host ("Encoding check passed: {0} file(s)" -f $checked)
exit 0
