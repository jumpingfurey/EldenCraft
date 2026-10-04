# EldenCraft Windows prototype

This experimental bridge runs Minecraft Java alongside Elden Ring. It is not a finished native renderer: builds can still appear through Elden Ring terrain, lighting does not match, and camera alignment may drift. Floors approximate the map; proper wall collision is unfinished. The configured frame limit is 300, not a promise of 300 FPS.

## One-time setup

1. Own and install both Elden Ring and Minecraft: Java Edition. Sign in to Minecraft yourself using the official launcher.
2. Extract this package into a permanent folder. This exact native build supports **Elden Ring App 1.17.1 / executable 2.7.1.0**, Windows 64-bit, and **Minecraft 1.21.1**. It refuses other Elden Ring executable versions.
3. Install **Fabric Loader 0.19.5 for Minecraft 1.21.1** using the [official Fabric installer](https://fabricmc.net/use/installer/). The tested loader version is bundled as a requirement, not as an installer.
4. In Minecraft Launcher's **Installations**, edit the Fabric installation, name it **EldenCraft**, and set **Game directory** to the extracted package's `minecraft` folder. That folder already contains the bridge and Fabric API in `mods`. Use a dedicated installation so normal worlds and other mods stay separate. See the [official Fabric mod installation guide](https://docs.fabricmc.net/players/installing-mods).
5. Run `Start EldenCraft Offline.cmd`. Steam library detection finds the game in most installations. If it fails, run PowerShell in this folder with `./Start-Offline.ps1 -GameExe 'D:\SteamLibrary\steamapps\common\ELDEN RING\Game\eldenring.exe'`, substituting your own path. The launcher remembers the path locally.
6. Load a character in Elden Ring and choose **borderless** display. Launch **EldenCraft** in Minecraft Launcher. Minecraft opens the separate **ER Bridge** world automatically when the host connects. In Minecraft video settings, disable VSync and start with render distance 6 and simulation distance 5 to reduce the load of running both games.

The launcher uses the separate `ER0000.eldencraft` save and makes a save backup before launch. A new mod save may require creating a character. No original Elden Ring saves are supplied in this package.

## Every time you play

Save and close any existing Elden Ring session. Start `Start EldenCraft Offline.cmd`, then select **EldenCraft → Play** in Minecraft Launcher. Load your Elden Ring character. Keep Minecraft focused to move/build. **F8** switches to Elden Ring menus and back; **R** performs Elden Ring interactions; **E** opens Minecraft inventory. Minecraft starts in Survival; `/gamemode creative` enables free building and disables ordinary damage.

Exit both games normally to save. For ordinary Elden Ring, use Steam. Keep this prototype offline; it is not a multiplayer mod.

## Sharing

Share the original clean ZIP, which contains mod binaries, source, notices and tools. Do not zip your used folder: it gains personal worlds, save backups, configuration paths and logs. Others must install their own games and sign into their own Minecraft accounts. This build has been tested through the development client; the official-launcher installation is packaged but has not been live-tested with an authenticated profile here.

No Minecraft/Elden Ring assets, accounts, authentication tokens or personal saves are included. Bridge, FPS sources, MinHook, Fabric API and Mod Engine notices are retained under `licenses`; Fabric API also retains the licenses inside its JAR. Source is under `source`. Native build instructions: MSVC x64, CMake and Ninja; `source/er-bridge` is the CMake project. Java source builds with the supplied Gradle wrapper, JDK 25+ for Gradle and a Java 21 toolchain, using `./gradlew build` in `source/mc-bridge`.

## Troubleshooting

- Connection: native logs are in `%TEMP%\EldenCraft`; Minecraft logs are in this package's `minecraft/logs` folder. Both must run under the same Windows user.
- Wrong Minecraft instance: make sure its game directory is this package's `minecraft`, and it uses Fabric 1.21.1.
- Other tools/dev instance: close the existing bridge client before using this package. Do not set a custom `ERBRIDGE_DATA` or `erbridge.dataDir` unless both games use the same directory.
- Builds remain visible through walls: the Windows DirectX compositor/depth integration is unfinished. This cannot be solved by changing the FPS limit or collision hitboxes.
- Safety limits: source layouts were verified for one Elden Ring executable version. Do not bypass the version check after a game update.
