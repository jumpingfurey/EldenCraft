import dev.ermc.bridge.link.*;
public class BridgeSmoke {
    public static void main(String[] args) throws Exception {
        BridgeShm shm = BridgeShm.open();
        GameState s = new GameState();
        if (!shm.readState(s) || s.frame != 987654321L || s.camPos[0] != -123.5f ||
            s.camPos[1] != 42.25f || s.camPos[2] != 900f || s.winW != 1280 || s.winH != 720) {
            throw new AssertionError("Native-to-Java camera or viewport differs");
        }
        ControlState c = new ControlState();
        c.mcFrame = 11223344L;
        c.camPos[0] = -77.25f;
        c.hunterPos[1] = 5.5f;
        c.fovYDeg = 80f;
        shm.writeControl(c);
        shm.bumpMcHeartbeat();
        System.out.println("PASS: Native-to-Java state matches; Java control published.");
    }
}
