#pragma once
#include <stdint.h>

namespace mb {
class ClientLiveness {
    bool initialized = false, seenChange = false;
    uint64_t last = 0, changedAt = 0;
public:
    bool observe(uint64_t heartbeat, uint64_t now) {
        if (!initialized) {
            initialized = true;
            last = heartbeat;
        } else if (heartbeat != last) {
            last = heartbeat;
            changedAt = now;
            seenChange = true;
        }
        return seenChange && now - changedAt <= 1000;
    }
};
}
