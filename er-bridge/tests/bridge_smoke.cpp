#include "common.h"
#include <cstdio>
#include <cstring>
int main(int argc, char** argv) {
    if (argc != 2 || !mb::shm_open()) return 1;
    int result = 0;
    if (std::strcmp(argv[1], "publish") == 0) {
        mb::state_begin_write();
        auto* s = mb::shm_state();
        s->frame = 987654321;
        s->camPos[0] = -123.5f;
        s->camPos[1] = 42.25f;
        s->camPos[2] = 900.0f;
        s->winW = 1280;
        s->winH = 720;
        mb::state_end_write();
        std::puts("Native host published test camera and viewport.");
    } else if (std::strcmp(argv[1], "verify") == 0) {
        ErmcControl c{};
        if (!mb::control_snapshot(&c) || c.mcFrame != 11223344 || c.camPos[0] != -77.25f ||
            c.hunterPos[1] != 5.5f || c.fovYDeg != 80.0f) {
            std::fputs("FAIL: Java-to-native control fields differ.\n", stderr);
            result = 2;
        } else std::puts("PASS: Java-to-native control snapshot matches.");
    } else result = 3;
    mb::shm_close();
    return result;
}
