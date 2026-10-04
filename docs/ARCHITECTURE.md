# Architecture and remaining work

The Mod Engine loader starts a version-checked native Elden Ring bridge. The Fabric mod exchanges state, controls, ray requests, entities and damage through memory-mapped files. Minecraft movement drives the host camera/player; an invisible host stand-in receives damage. Minecraft enemy proxies forward damage into the host.

The Windows compositor is a stub. A transparent GLFW Minecraft window supplies visible blocks/HUD over the host's borderless window. It uses the last reported host camera pose, but independent presentation means imperfect alignment. Implementing host-frame DirectX 12 composition with depth/lighting is the main rendering work still needed.

Floor collision samples nearby columns with vertical rays. Sampling prioritizes feet and walking lookahead, retries misses and holds a falling player while an unknown floor resolves. Generated horizontal walls were removed because their inferred normals created invisible traps. Real wall collision remains unfinished.

Enemy publication rotates through loaded candidates at about 30 Hz, retaining cached proxies between slices. Memory validation caches readable regions within one callback, uses resident working-set protection information when possible, and falls back to region queries. Three consecutive callbacks of at least 50 ms suspend bridge work.

The FPS patch changes process memory only, validates a unique signature, and attempts to restore its original value on shutdown. A higher cap does not guarantee a higher achieved FPS.

## Developer command mailbox

Write one command line to `mc_cmd.txt` in the shared runtime directory. Commands include `status`, `save`, `quit`, `terrain reset`, `switch host`, `switch mc`, and `run <Minecraft server command>`. This is a development interface; use only with your own local game.

Version changes require reviewing native signatures/structures and revalidating gameplay. Do not remove version checks just to make another executable launch.
