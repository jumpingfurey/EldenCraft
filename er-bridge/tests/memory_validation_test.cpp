#include "common.h"
#include <cstdio>
int main() {
    void* page = VirtualAlloc(nullptr, 8192, MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);
    if (!page) return 1;
    // Resident pages exercise the working-set fast path, not only the fallback.
    ((volatile unsigned char*)page)[0] = 1;
    ((volatile unsigned char*)page)[4096] = 1;
    {
        mb::MemoryValidationScope outer;
        if (!mb::mem_readable(page, 4096)) return 2;
        { mb::MemoryValidationScope inner; if (!mb::mem_readable(page, 4)) return 3; }
        if (!mb::mem_readable(page, 4096)) return 4;
    }
    DWORD previous;
    if (!VirtualProtect((unsigned char*)page + 4096, 4096, PAGE_NOACCESS, &previous)) return 8;
    {
        mb::MemoryValidationScope boundary;
        if (!mb::mem_readable(page, 4096) || mb::mem_readable(page, 8192)) return 9;
    }
    if (!VirtualProtect((unsigned char*)page + 4096, 4096, PAGE_READWRITE | PAGE_GUARD, &previous)) return 10;
    {
        mb::MemoryValidationScope guarded;
        if (mb::mem_readable((unsigned char*)page + 4096, 1)) return 11;
    }
    if (!VirtualProtect(page, 4096, PAGE_NOACCESS, &previous)) return 5;
    {
        mb::MemoryValidationScope nextFrame;
        if (mb::mem_readable(page, 1)) return 6;
    }
    VirtualFree(page, 0, MEM_RELEASE);
    if (mb::mem_readable(page, 1) || mb::mem_readable((void*)1, 4)) return 7;
    std::puts("PASS: nested frame scopes work; no-access and freed mappings rejected in later scopes.");
    return 0;
}
