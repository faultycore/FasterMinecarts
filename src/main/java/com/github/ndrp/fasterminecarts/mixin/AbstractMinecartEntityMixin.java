package com.github.ndrp.fasterminecarts.mixin;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoulSandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends Entity {
	
	// Needed because Entity has no default constructor
	public AbstractMinecartEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}
	
	@Inject(method = "getMaxOffRailSpeed", at = @At("HEAD"), cancellable = true)
	protected void onGetMaxOffRailSpeed(CallbackInfoReturnable<Double> cir) {
		Block under = world.getBlockState(getBlockPos().down()).getBlock();
		// Can't place rails on honey blocks
		if (!(world.getBlockState(getBlockPos()).getBlock() instanceof AbstractRailBlock)
				|| under instanceof SoulSandBlock)
			cir.setReturnValue(0.3);
		else
			cir.setReturnValue(1.6);
	}
}

