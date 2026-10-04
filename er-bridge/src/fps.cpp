#include "common.h"
#include <cmath>
#include <cstdlib>
#include <cstdio>

namespace mb {
static void* g_fpsAddress = nullptr;
static float g_originalTick = 0, g_patchedTick = 0;
// Pattern from uberhalit and luca2040; see FPS-SOURCES.md and MIT notices.
void fps_init() {
    char value[32] = {};
    GetEnvironmentVariableA("ERBRIDGE_FPS", value, sizeof(value));
    int fps = atoi(value);
    FILE* settings = fopen(bridge_path("fps-limit.txt").c_str(), "r");
    if (settings) { int requested; if (fscanf(settings, "%d", &requested) == 1) fps = requested; fclose(settings); }
    if (fps < 61 || fps > 1000) { log("fps: invalid requested limit; unchanged"); return; }
    const uint8_t pattern[] = {0x89,0x73,0,0xC7,0,0,0,0,0,0,0xEB,0,0x89,0x73};
    const uint8_t mask[] = {255,255,0,255,0,0,0,0,0,0,255,0,255,255};
    uintptr_t found[2] = {};
    uintptr_t base = main_module_base();
    size_t count = mem_scan(base, base + main_module_size(), pattern, mask, sizeof(pattern),
        ERMC_SCAN_IMAGE | ERMC_SCAN_EXEC_ONLY, found, 2);
    if (count != 1) { log("fps: expected unique signature, found %zu; unchanged", count); return; }
    float original;
    void* at = (void*)(found[0] + 6);
    if (!mem_read(at, &original, sizeof(original)) || std::fabs(original - 1.0f/60.0f) > 0.000001f) {
        log("fps: original frame tick not 1/60; unchanged"); return;
    }
    float tick = 1.0f / fps;
    if (!mem_write(at, &tick, sizeof(tick))) { log("fps: patch failed"); return; }
    g_fpsAddress = at; g_originalTick = original; g_patchedTick = tick;
    log("fps: offline frame limit raised to %d (memory only)", fps);
}
void fps_shutdown() {
    float current;
    if (g_fpsAddress && mem_read(g_fpsAddress, &current, sizeof(current)) && current == g_patchedTick)
        mem_write(g_fpsAddress, &g_originalTick, sizeof(g_originalTick));
    g_fpsAddress = nullptr;
}
}
