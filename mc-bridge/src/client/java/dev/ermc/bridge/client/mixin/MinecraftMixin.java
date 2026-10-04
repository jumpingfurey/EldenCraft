package dev.ermc.bridge.client.mixin;

import dev.ermc.bridge.client.FramePassthrough;
import dev.ermc.bridge.client.Overlay;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	private static final int ERBRIDGE_TARGET_FPS = Math.max(61, Math.min(1000,
		Integer.parseInt(System.getenv().getOrDefault("ERBRIDGE_FPS", "300"))));
	@Inject(method = "runTick", at = @At("HEAD"))
	private void erbridge$frame(boolean tick, CallbackInfo ci) {
		Overlay.onFrame((Minecraft) (Object) this);
	}

	/** While frames go to the host game, rendering faster than it (~60 fps) only wastes readbacks. */
	@Inject(method = "getFramerateLimit", at = @At("RETURN"), cancellable = true)
	private void erbridge$capFps(CallbackInfoReturnable<Integer> cir) {
		if (FramePassthrough.wanted() && cir.getReturnValue() > 60) {
			cir.setReturnValue(60);
		} else if (Overlay.active()) {
			cir.setReturnValue(ERBRIDGE_TARGET_FPS);
		}
	}
}
