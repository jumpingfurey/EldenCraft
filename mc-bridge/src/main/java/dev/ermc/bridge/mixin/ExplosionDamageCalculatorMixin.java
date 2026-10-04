package dev.ermc.bridge.mixin;

import dev.ermc.bridge.ErBridgeMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Optional;

/** Collision stand-ins must not absorb TNT's blast energy or become destructible blocks. */
@Mixin(ExplosionDamageCalculator.class)
public abstract class ExplosionDamageCalculatorMixin {
	@Inject(method = "getBlockExplosionResistance", at = @At("HEAD"), cancellable = true)
	private void erbridge$terrainResistance(Explosion explosion, BlockGetter level, BlockPos pos,
			BlockState state, FluidState fluid, CallbackInfoReturnable<Optional<Float>> cir) {
		if (state.is(ErBridgeMod.TERRAIN)) cir.setReturnValue(Optional.of(0.0F));
	}
	@Inject(method = "shouldBlockExplode", at = @At("HEAD"), cancellable = true)
	private void erbridge$keepTerrain(Explosion explosion, BlockGetter level, BlockPos pos,
			BlockState state, float power, CallbackInfoReturnable<Boolean> cir) {
		if (state.is(ErBridgeMod.TERRAIN)) cir.setReturnValue(false);
	}
}
