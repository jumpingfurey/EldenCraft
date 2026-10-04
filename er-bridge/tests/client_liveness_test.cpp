#include "client_liveness.h"
#include <cstdio>
int main() {
    mb::ClientLiveness client;
    // A nonzero heartbeat saved by a crashed process must not enable game work.
    if (client.observe(100, 10) || client.observe(100, 2000)) return 1;
    if (!client.observe(101, 2001)) return 2;
    if (!client.observe(101, 3001)) return 3;
    if (client.observe(101, 3002)) return 4;
    // A restarted client can reset its counter and reconnect.
    if (!client.observe(1, 4000)) return 5;
    if (client.observe(1, 5001)) return 6;
    std::puts("PASS: stale heartbeat stays inactive, live client connects, expires and reconnects.");
    return 0;
}
