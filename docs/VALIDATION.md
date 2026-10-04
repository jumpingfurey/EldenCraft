# Validation status

This records local development checks, not certification of the complete mod.

- Native MSVC build completed; client-liveness and memory-validation tests passed (2/2). Tests cover heartbeat liveness, readable/resident pages, cross-page no-access boundaries, guarded pages, nested validation scopes and freed mappings.
- Fabric compilation and remapped player JAR assembly completed.
- The portable launcher's file/version preflight passed against executable 2.7.1.0.
- Live development gameplay displayed Minecraft blocks/HUD over Elden Ring. Movement, floor queries and enemy proxies connected.
- Bow damage and primed TNT forwarded damage to Elden Ring enemies; native HP changes/deaths were observed. Minecraft health/damage commands were accepted.
- Generated walls were removed after trapping the player; the player confirmed they could leave that spot.
- Profiling removed costly region-query overhead. Native callbacks subsequently measured roughly 0.01–0.07 ms; combined-game FPS varied around 77–110 on the development PC.
- The revised camera-pose alignment built and launched, but visual improvement during jumping is unconfirmed.
- The clean player package was checked for excluded saves, account data, logs and caches.

## Unverified or unfinished

Authenticated official-launcher gameplay has not been tested here. The live checks used a development client. Long sessions, map-wide collision, all enemy types, balance, multiplayer and other game versions are unverified.

Native DirectX 12 composition, depth occlusion and matching host lighting are absent. Collision is approximate and does not provide real walls. The 300 FPS limit is a ceiling, not an achieved performance claim. Tutorial prompts have not been disabled.
