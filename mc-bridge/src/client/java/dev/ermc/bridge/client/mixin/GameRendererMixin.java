package dev.ermc.bridge.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ermc.bridge.client.CameraSync;
import dev.ermc.bridge.client.FramePassthrough;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import dev.ermc.bridge.client.Overlay;
import net.minecraft.client.Camera;
import net.minecraft.client.Options;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	/** Rounded terrain at an enemy's feet must not steal the crosshair from its body. */
	@Inject(method = "pick", at = @At("RETURN"))
	private void erbridge$pickEnemy(float partialTick, CallbackInfo ci) {
		Minecraft mc = Minecraft.getInstance();
		if (!Overlay.active() || mc.player == null || mc.level == null
			|| !(mc.hitResult instanceof net.minecraft.world.phys.BlockHitResult block)
			|| !mc.level.getBlockState(block.getBlockPos()).is(dev.ermc.bridge.ErBridgeMod.TERRAIN)) return;
		net.minecraft.world.phys.Vec3 eye = mc.player.getEyePosition(partialTick);
		double reach = mc.player.entityInteractionRange();
		net.minecraft.world.phys.Vec3 end = eye.add(mc.player.getViewVector(partialTick).scale(reach));
		double nearest = reach * reach;
		for (net.minecraft.world.entity.Entity entity : mc.level.entitiesForRendering()) {
			if (!(entity instanceof dev.ermc.bridge.entity.ErEntity proxy) || !proxy.isTargetableEnemy()
				|| !proxy.visibleFrom(mc.player)) continue;
			var point = proxy.getBoundingBox().inflate(proxy.getPickRadius()).clip(eye, end);
			if (point.isPresent() && point.get().distanceToSqr(eye) < nearest) {
				nearest = point.get().distanceToSqr(eye);
				mc.hitResult = new net.minecraft.world.phys.EntityHitResult(proxy, point.get());
				mc.crosshairPickEntity = proxy;
			}
		}
	}
	/** Both games must use exactly the same projection, so no FOV effects (sprint, etc.). */
	@Inject(method = "getFov", at = @At("HEAD"), cancellable = true)
	private void erbridge$fov(Camera camera, float partialTick, boolean useFovSetting, CallbackInfoReturnable<Double> cir) {
		if (Overlay.active() && useFovSetting) {
			cir.setReturnValue((double) CameraSync.fov());
		}
	}

	/** View bobbing / hurt tilt would move Minecraft's world against the host game's. */
	@Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
	private void erbridge$noBob(PoseStack poseStack, float partialTick, CallbackInfo ci) {
		if (Overlay.active()) {
			ci.cancel();
		}
	}

	@Inject(method = "bobHurt", at = @At("HEAD"), cancellable = true)
	private void erbridge$noHurtTilt(PoseStack poseStack, float partialTick, CallbackInfo ci) {
		if (Overlay.active()) {
			ci.cancel();
		}
	}

	/** Frame passthrough: grab world color + depth before the hand is drawn. */
	@Inject(method = "renderLevel", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V",
		shift = At.Shift.AFTER))
	private void erbridge$afterWorld(DeltaTracker deltaTracker, CallbackInfo ci) {
		FramePassthrough.afterWorld(Minecraft.getInstance());
	}

	/** Frame passthrough: the hand (and screen effects) gets a layer of its own, lit like the world. */
	@Inject(method = "renderLevel", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(Lnet/minecraft/client/Camera;FLorg/joml/Matrix4f;)V",
		shift = At.Shift.AFTER))
	private void erbridge$afterHand(DeltaTracker deltaTracker, CallbackInfo ci) {
		FramePassthrough.afterHand(Minecraft.getInstance());
	}

	/**
	 * Clicking the host game's window behind us must not pause Minecraft; nor may its loading
	 * screens, which can switch the overlay off for a moment. A pause menu opened then stays
	 * invisible while Elden Ring shows its own screen, yet takes clicks: one closed the world
	 * ("Save and Quit to Title").
	 */
	@Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;pauseOnLostFocus:Z"))
	private boolean erbridge$pauseOnLostFocus(Options options) {
		return options.pauseOnLostFocus && !Overlay.active() && !dev.ermc.bridge.TerrainManager.isBridgeWorld();
	}
}
