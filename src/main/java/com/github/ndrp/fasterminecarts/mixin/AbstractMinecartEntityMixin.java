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
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends Entity {

	// Needed because Entity has no default constructor
	public AbstractMinecartEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "getMaxOffRailSpeed", at = @At("HEAD"), cancellable = true)
	protected void onGetMaxOffRailSpeed(CallbackInfoReturnable<Double> cir) {
		FasterMinecartsConfig config = AutoConfig.getConfigHolder(FasterMinecartsConfig.class).getConfig();

		if (config.automaticMinecartSlowDown) {
			BlockPos blockPos = this.getBlockPos();
			BlockState state = this.world.getBlockState(blockPos);
			Block under = world.getBlockState(getBlockPos().down()).getBlock();

			// Return if above soul sand
			// Can't place rails on honey blocks
			if (!(state.getBlock() instanceof AbstractRailBlock) || under instanceof SoulSandBlock) {
				cir.setReturnValue(config.slowSpeed);
				return;
			}

			Vec3d v = this.getVelocity();

			if (Math.abs(v.getX()) < 0.5 && Math.abs(v.getZ()) < 0.5) { // Return early if at a speed where we can't
																		// possibly derail
				cir.setReturnValue(0.4 + config.maxSpeed / 10D);
				return;
			}

			final int additionalOffset = 1;
			final int offset = (int) (0.4 + config.maxSpeed / 10D) + additionalOffset;

			AbstractRailBlock abstractRailBlock = (AbstractRailBlock) state.getBlock();
			RailShape railShape = (RailShape) state.get(abstractRailBlock.getShapeProperty());
			Vec3i nextRailOffset = MinecartUtility.getNextRailOffsetByVelocity(railShape, v);

			if (nextRailOffset == null) {
				cir.setReturnValue(config.slowSpeed);
				return;
			}

			for (int i = 0; i < offset; i++) {
				RailShape railShapeAtOffset = null;

				railShapeAtOffset = MinecartUtility.getRailShapeAtOffset(
						new Vec3i(nextRailOffset.getX() * i, 0, nextRailOffset.getZ() * i), blockPos, this.world);

				if (railShapeAtOffset == null) {
					cir.setReturnValue(config.slowSpeed);
					return;
				}

				switch (railShapeAtOffset) {
				case SOUTH_EAST:
				case SOUTH_WEST:
				case NORTH_WEST:
				case NORTH_EAST:
					cir.setReturnValue(config.slowSpeed);
					return;
				default:
				}
			}
			cir.setReturnValue(0.4 + config.maxSpeed / 10D);

		} else {
			Block under = world.getBlockState(getBlockPos().down()).getBlock();
			// Can't place rails on honey blocks
			if (!(world.getBlockState(getBlockPos()).getBlock() instanceof AbstractRailBlock)
					|| under instanceof SoulSandBlock) {
				cir.setReturnValue(config.slowSpeed);
			} else {
				cir.setReturnValue(0.4 + config.maxSpeed / 10D);
			}
		}
	}
}
