#include "common.h"
#include <stdexcept>

namespace mb {
std::string bridge_path(const char* name) {
    char root[32768] = {};
    DWORD n = GetEnvironmentVariableA("ERBRIDGE_DATA", root, sizeof(root));
    if (!n || n >= sizeof(root)) {
        GetTempPathA(sizeof(root), root);
        std::string base = std::string(root) + "EldenCraft";
        CreateDirectoryA(base.c_str(), nullptr);
        return base + "\\" + name;
    }
    CreateDirectoryA(root, nullptr);
    return std::string(root) + "\\" + name;
}
}
