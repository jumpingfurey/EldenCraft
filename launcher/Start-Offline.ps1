[CmdletBinding()]
param([string]$GameExe, [ValidateRange(61,1000)][int]$TargetFps=300, [switch]$CheckOnly)
$ErrorActionPreference = 'Stop'
$configPath = Join-Path $PSScriptRoot 'eldencraft-config.json'
if (!$GameExe -and (Test-Path -LiteralPath $configPath)) {
    $GameExe = (Get-Content -LiteralPath $configPath -Raw | ConvertFrom-Json).GameExe
}
if (!$GameExe) {
    $steamRoots = @("${env:ProgramFiles(x86)}\Steam")
    try { $steamRoots += (Get-ItemProperty 'HKCU:\Software\Valve\Steam' -ErrorAction Stop).SteamPath } catch {}
    foreach ($steamRoot in @($steamRoots)) {
        $libraries = Join-Path $steamRoot 'steamapps/libraryfolders.vdf'
        if (Test-Path -LiteralPath $libraries) {
            foreach ($match in [regex]::Matches((Get-Content -LiteralPath $libraries -Raw), '"path"\s+"([^"]+)"')) {
                $steamRoots += $match.Groups[1].Value.Replace('\\','\')
            }
        }
    }
    foreach ($steamRoot in ($steamRoots | Select-Object -Unique)) {
        $candidate = Join-Path $steamRoot 'steamapps/common/ELDEN RING/Game/eldenring.exe'
        if (Test-Path -LiteralPath $candidate) { $GameExe = $candidate; break }
    }
}
if (!$GameExe -or !(Test-Path -LiteralPath $GameExe)) {
    throw 'Elden Ring was not found. Run Start-Offline.ps1 -GameExe with the full path to eldenring.exe. See START-HERE.md.'
}
$GameExe = (Resolve-Path -LiteralPath $GameExe).Path
if ((Get-Item -LiteralPath $GameExe).VersionInfo.FileVersion -ne '2.7.1.0') {
    throw 'This prototype supports executable 2.7.1.0 (Elden Ring App 1.17.1) only. Other versions require a port.'
}
$loader = Join-Path $PSScriptRoot 'bin/EldenCraftLoader.dll'
$core = Join-Path $PSScriptRoot 'bin/erbridge/erbridge_core.dll'
$me3 = Join-Path $PSScriptRoot 'tools/me3/bin/me3.exe'
foreach ($required in @($loader,$core,$me3,(Join-Path $PSScriptRoot 'mods/er-bridge-0.1.0.jar'))) {
    if (!(Test-Path -LiteralPath $required)) { throw "Package file missing: $required" }
}
if ($CheckOnly) { Write-Output 'Files and game version match. This does not test Minecraft login or gameplay.'; return }
if (Get-Process eldenring -ErrorAction SilentlyContinue) { throw 'Save and close Elden Ring before starting the offline mod.' }
@{GameExe=$GameExe} | ConvertTo-Json | Set-Content -LiteralPath $configPath -Encoding UTF8
# The separately launched official Minecraft client uses this same default temp directory.
$dataDir = Join-Path $env:TEMP 'EldenCraft'
New-Item -ItemType Directory -Path $dataDir -Force | Out-Null
Set-Content -LiteralPath (Join-Path $dataDir 'fps-limit.txt') -Value $TargetFps -Encoding ascii
$env:ERBRIDGE = '1'
$env:ERBRIDGE_FPS = [string]$TargetFps
$env:ERBRIDGE_DATA = $dataDir
$saves = Join-Path $env:APPDATA 'EldenRing'
if (Test-Path -LiteralPath $saves) {
    $backup = Join-Path $PSScriptRoot ('save-backups/' + (Get-Date -Format 'yyyyMMdd-HHmmss-fff'))
    New-Item -ItemType Directory -Path $backup -Force | Out-Null
    Copy-Item -LiteralPath $saves -Destination (Join-Path $backup 'EldenRing') -Recurse
    Write-Output "Save backup: $backup"
}
$launchArgs = @('launch','-g','eldenring','--exe',('"'+$GameExe+'"'),'--native',('"'+$loader+'"'),
    '--savefile','ER0000.eldencraft','--online=false','--no-mem-patch=true')
Start-Process -FilePath $me3 -ArgumentList $launchArgs -WorkingDirectory $PSScriptRoot -WindowStyle Hidden `
    -RedirectStandardOutput (Join-Path $dataDir 'launcher.log') -RedirectStandardError (Join-Path $dataDir 'launcher-error.log') | Out-Null
Write-Output 'Elden Ring is starting offline. Launch your Fabric 1.21.1 EldenCraft installation in the official Minecraft Launcher.'
Write-Output 'Load your Elden Ring character in borderless mode. The bridge opens its Minecraft world automatically. F8 switches controls.'
