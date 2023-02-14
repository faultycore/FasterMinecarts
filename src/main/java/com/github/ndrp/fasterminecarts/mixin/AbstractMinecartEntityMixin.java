package com.github.ndrp.fasterminecarts.mixin;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoulSandBlock;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.github.ndrp.fasterminecarts.config.FasterMinecartsConfig;
import com.github.ndrp.fasterminecarts.util.MinecartUtility;
import me.shedaniel.autoconfig.AutoConfig;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends Entity {

	// Needed because Entity has no default constructor
	public AbstractMinecartEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "getMaxOffRailSpeed", at = @At("HEAD"), cancellable = true)
	protected void onGetMaxOffRailSpeed(CallbackInfoReturnable<Double> cir) {
		FasterMinecartsConfig config = AutoConfig.getConfigHolder(FasterMinecartsConfig.class).getConfig();
		BlockPos blockPos = this.getBlockPos();
		BlockState state = this.world.getBlockState(blockPos);
		double slowSpeed = (config.slowSpeed/20D);
		double maxSpeed = (config.maxSpeed/20D);

		Block under = world.getBlockState(getBlockPos().down()).getBlock();
		// Return if above soul sand
		if (!(state.getBlock() instanceof AbstractRailBlock) || config.soulSandSlowDown || under instanceof SoulSandBlock) {
			cir.setReturnValue(slowSpeed);
			return;
		}

		if (config.automaticMinecartSlowDown) {

			Vec3d v = this.getVelocity();

			if (Math.abs(v.getX()) < 0.5 && Math.abs(v.getZ()) < 0.5) { // Return early if at a speed where we can't
																		// possibly derail
				cir.setReturnValue(maxSpeed);
				return;
			}

			final int additionalOffset = 1;
			final int offset = (int) (maxSpeed) + additionalOffset;

			AbstractRailBlock abstractRailBlock = (AbstractRailBlock) state.getBlock();
			RailShape railShape = (RailShape) state.get(abstractRailBlock.getShapeProperty());
			Vec3i nextRailOffset = MinecartUtility.getNextRailOffsetByVelocity(railShape, v);

			if (nextRailOffset == null) {
				cir.setReturnValue(slowSpeed);
				return;
			}

			for (int i = 0; i < offset; i++) {
				RailShape railShapeAtOffset = null;

				railShapeAtOffset = MinecartUtility.getRailShapeAtOffset(
						new Vec3i(nextRailOffset.getX() * i, 0, nextRailOffset.getZ() * i), blockPos, this.world);

				if (railShapeAtOffset == null) {
					cir.setReturnValue(slowSpeed);
					return;
				}

				switch (railShapeAtOffset) {
				case SOUTH_EAST:
				case SOUTH_WEST:
				case NORTH_WEST:
				case NORTH_EAST:
					cir.setReturnValue(slowSpeed);
					return;
				default:
				}
			}
			cir.setReturnValue(maxSpeed);

		} else {

				cir.setReturnValue(maxSpeed);
		}
	}
}
