// First Windows bring-up uses the upstream transparent GLFW overlay.
// The upstream compositor accesses private D3DMetal layouts, which do not exist
// in Microsoft's D3D12 runtime. Never execute those accesses on Windows.
#include "common.h"
namespace mb {
bool compositor_init() {
    log("Windows port: transparent Minecraft overlay; native D3D12 compositing not implemented");
    return false;
}
void compositor_poll() {}
void compositor_depth_poll() {}
void compositor_detach() {}
void compositor_shutdown() {}
void compositor_note_applied_pose(uint64_t) {}
bool compositor_active() { return false; }
}
