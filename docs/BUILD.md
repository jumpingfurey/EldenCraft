# Build and package on Windows

## Native bridge

Install Visual Studio's C++ x64 tools, Windows SDK and CMake 3.25 or newer. From an x64 Developer PowerShell:

```powershell
cmake -S er-bridge -B build/native -A x64
cmake --build build/native --config Release --parallel
ctest --test-dir build/native -C Release --output-on-failure
```

The loader is built as `dinput8.dll` and packaged as `EldenCraftLoader.dll`. The core is `erbridge_core.dll`. The launcher injects the loader through Mod Engine; do not copy it into the Steam game folder.

## Fabric mod

Install JDK 25+ for Gradle 9.7.1 and a Java 21 toolchain for Minecraft. Make both visible to Gradle. From the repository root:

```powershell
./mc-bridge/gradlew.bat -p mc-bridge --no-configuration-cache build
```

If Java 21 is not discovered, set `ERBRIDGE_JDK21` to its installation and append `-Porg.gradle.java.installations.fromEnv=ERBRIDGE_JDK21`. Java 21 is for compilation/gameplay; Gradle's own runtime requires the newer JDK.

The remapped player JAR is `mc-bridge/build/libs/er-bridge-0.1.0.jar`. Building resolves Minecraft development dependencies; they must not be committed or redistributed.

## Clean player package

Obtain Mod Engine 3 and Fabric API 0.116.17+1.21.1 from their official projects. The Mod Engine directory must contain `me3.exe`, `me3_mod_host.dll` and `me3-launcher.exe`. Then:

```powershell
./Build-Package.ps1 -NativeDirectory ./build/native/Release -Me3Directory 'D:/tools/me3/bin' -FabricApiJar 'D:/downloads/fabric-api-0.116.17+1.21.1.jar'
```

Output is `dist/EldenCraft-Windows-Prototype-0.1.0.zip`, containing the binaries, source, licenses, checksums and launcher. The script creates a new staging folder on every run and copies only explicit source paths.

Follow [PLAY.md](PLAY.md) for installation. Players use their own authenticated Minecraft Launcher profile. The portable launcher does not automatically launch Minecraft.

## Runtime

Both games must share `%TEMP%/EldenCraft` by default. For development, `ERBRIDGE_DATA` (native) and `erbridge.dataDir` (Java property) must point to the same directory. A changing Minecraft heartbeat is required before native world scans/control activate. Development commands are described in [ARCHITECTURE.md](ARCHITECTURE.md).
