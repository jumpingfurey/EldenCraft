package dev.ermc.bridge.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.ermc.bridge.entity.ErEntity;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
	@Inject(method = "renderHitbox", at = @At("HEAD"), cancellable = true)
	private static void erbridge$hideProxy(PoseStack stack, VertexConsumer vertices, Entity entity,
			float partialTick, float red, float green, float blue, CallbackInfo ci) {
		if (entity instanceof ErEntity) ci.cancel();
	}
}
