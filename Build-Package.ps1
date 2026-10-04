[CmdletBinding()]
param(
    [Parameter(Mandatory)][string]$NativeDirectory,
    [Parameter(Mandatory)][string]$Me3Directory,
    [Parameter(Mandatory)][string]$FabricApiJar
)
$ErrorActionPreference = 'Stop'
$repo = $PSScriptRoot
$native = (Resolve-Path -LiteralPath $NativeDirectory).Path
$me3 = (Resolve-Path -LiteralPath $Me3Directory).Path
$api = (Resolve-Path -LiteralPath $FabricApiJar).Path
$jar = Join-Path $repo 'mc-bridge/build/libs/er-bridge-0.1.0.jar'
$loader = Join-Path $native 'dinput8.dll'
$core = Join-Path $native 'erbridge_core.dll'
foreach ($required in @($jar,$loader,$core,$api)) {
    if (!(Test-Path -LiteralPath $required -PathType Leaf)) { throw "Required artifact missing: $required" }
}
if ([IO.Path]::GetFileName($api) -ne 'fabric-api-0.116.17+1.21.1.jar') { throw 'Expected Fabric API 0.116.17+1.21.1.' }
$dist = Join-Path $repo 'dist'
$stage = Join-Path $dist ('stage-'+[guid]::NewGuid().ToString('N'))
$package = Join-Path $stage 'EldenCraft'
foreach ($dir in @('bin/erbridge','mods','minecraft/mods','tools/me3/bin','licenses','source/er-bridge/third_party/minhook','source/mc-bridge','source/docs','source/launcher')) {
    New-Item -ItemType Directory -Path (Join-Path $package $dir) -Force | Out-Null
}
Copy-Item -LiteralPath $loader -Destination (Join-Path $package 'bin/EldenCraftLoader.dll')
Copy-Item -LiteralPath $core -Destination (Join-Path $package 'bin/erbridge/erbridge_core.dll')
foreach ($target in @('mods','minecraft/mods')) {
    Copy-Item -LiteralPath $jar,$api -Destination (Join-Path $package $target)
}
foreach ($file in @('me3.exe','me3_mod_host.dll','me3-launcher.exe')) {
    Copy-Item -LiteralPath (Join-Path $me3 $file) -Destination (Join-Path $package 'tools/me3/bin')
}
Copy-Item -LiteralPath (Join-Path $repo 'LICENSE') -Destination (Join-Path $package 'licenses')
Get-ChildItem -LiteralPath (Join-Path $repo 'licenses') -File | Copy-Item -Destination (Join-Path $package 'licenses')
Copy-Item -LiteralPath (Join-Path $repo 'THIRD-PARTY-NOTICES.md') -Destination (Join-Path $package 'licenses')
Copy-Item -LiteralPath (Join-Path $repo 'er-bridge/third_party/minhook/LICENSE.txt') -Destination (Join-Path $package 'licenses/MinHook-LICENSE.txt')
foreach ($file in @('Start-Offline.ps1','Start EldenCraft Offline.cmd')) {
    Copy-Item -LiteralPath (Join-Path $repo ('launcher/'+$file)) -Destination $package
    Copy-Item -LiteralPath (Join-Path $repo ('launcher/'+$file)) -Destination (Join-Path $package 'source/launcher')
}
Copy-Item -LiteralPath (Join-Path $repo 'docs/PLAY.md') -Destination (Join-Path $package 'START-HERE.md')
foreach ($part in @('src','include','tests','CMakeLists.txt')) {
    Copy-Item -LiteralPath (Join-Path $repo ('er-bridge/'+$part)) -Destination (Join-Path $package 'source/er-bridge') -Recurse
}
foreach ($part in @('src','include','cmake','dll_resources','CMakeLists.txt','LICENSE.txt')) {
    Copy-Item -LiteralPath (Join-Path $repo ('er-bridge/third_party/minhook/'+$part)) -Destination (Join-Path $package 'source/er-bridge/third_party/minhook') -Recurse
}
foreach ($part in @('src','gradle','build.gradle','settings.gradle','gradle.properties','gradlew','gradlew.bat')) {
    Copy-Item -LiteralPath (Join-Path $repo ('mc-bridge/'+$part)) -Destination (Join-Path $package 'source/mc-bridge') -Recurse
}
foreach ($file in @('README.md','LICENSE','THIRD-PARTY-NOTICES.md','CONTRIBUTING.md','Build-Package.ps1','.gitignore')) {
    Copy-Item -LiteralPath (Join-Path $repo $file) -Destination (Join-Path $package 'source')
}
Get-ChildItem -LiteralPath (Join-Path $repo 'docs') -File | Copy-Item -Destination (Join-Path $package 'source/docs')
Copy-Item -LiteralPath (Join-Path $repo 'licenses') -Destination (Join-Path $package 'source') -Recurse
$files = @(Get-ChildItem -LiteralPath $package -File -Recurse)
$forbidden = $files | Where-Object { $_.FullName -match '[\\/](saves|save-backups|logs|runtime|\.git|\.gradle|build|run)[\\/]' -or $_.Name -match '\.(sl2|eldencraft|shm|pem|key)$' }
if ($forbidden) { throw 'Private or generated data found in package; refusing to archive.' }
$manifest = $files | Sort-Object FullName | ForEach-Object {
    (Get-FileHash -LiteralPath $_.FullName -Algorithm SHA256).Hash.ToLower()+'  '+$_.FullName.Substring($package.Length+1).Replace('\','/')
}
Set-Content -LiteralPath (Join-Path $package 'SHA256SUMS.txt') -Value $manifest -Encoding ascii
$zip = Join-Path $dist 'EldenCraft-Windows-Prototype-0.1.0.zip'
Compress-Archive -LiteralPath $package -DestinationPath $zip -Force
Write-Output "Package: $zip"
Write-Output "Staging retained: $stage"
