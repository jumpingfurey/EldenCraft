package dev.ermc.bridge.link;

import java.nio.file.Path;

public final class BridgePaths {
    private BridgePaths() {}
    public static Path file(String name) {
        String root = System.getProperty("erbridge.dataDir", System.getenv("ERBRIDGE_DATA"));
        if (root == null || root.isBlank()) {
            root = Path.of(System.getProperty("java.io.tmpdir"), "EldenCraft").toString();
        }
        return Path.of(root, name);
    }
}
