package com.github.ndrp.fasterminecarts.mixin;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
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

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends Entity {
	
	// Needed because Entity has no default constructor
	public AbstractMinecartEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}
	
	@Inject(method = "getMaxOffRailSpeed", at = @At("HEAD"), cancellable = true)
	private void onGetMaxOffRailSpeed(CallbackInfoReturnable<Double> cir) {
		
		final double topSpeed = 1.6;
		
		Vec3d v = this.getVelocity();
		
		if (Math.abs(v.getX()) < 0.5 && Math.abs(v.getZ()) < 0.5) { // return early if at a speed where we can't possibly derail
			cir.setReturnValue(topSpeed);
			return;
		}
		
		BlockPos blockPos = this.getBlockPos();
		BlockState state = this.world.getBlockState(blockPos);
		
		if (state.getBlock() instanceof AbstractRailBlock) {
			final double slowdownSpeed = 0.3;
			final int additionalOffset = 1;
			final int offset = (int)topSpeed + additionalOffset;
			
			AbstractRailBlock abstractRailBlock = (AbstractRailBlock)state.getBlock();
			RailShape railShape = (RailShape)state.get(abstractRailBlock.getShapeProperty());
			Vec3i nextRailOffset = getNextRailOffsetByVelocity(railShape, v);
			
			if (nextRailOffset == null) {
				cir.setReturnValue(slowdownSpeed);
				return;
			}
				
			for (int i = 0; i <= offset; i++) {	
				RailShape railShapeAtOffset = null;
				
				railShapeAtOffset = getRailShapeAtOffset(new Vec3i(nextRailOffset.getX() * i, 0, nextRailOffset.getZ() * i), blockPos);
				
				if (railShapeAtOffset == null) {
					cir.setReturnValue(slowdownSpeed);
					return;
				}
				
				switch (railShapeAtOffset) {
				case SOUTH_EAST:
				case SOUTH_WEST:
				case NORTH_WEST:
				case NORTH_EAST:
					cir.setReturnValue(slowdownSpeed);
					return;
				default:
				}	
			}
			cir.setReturnValue(topSpeed);
		} 
	}
	
	/*
	 * Returns null if the block at the given offset is not a rail
	 */
	private RailShape getRailShapeAtOffset(Vec3i railOffset, BlockPos blockPos) {
		
		BlockState state = this.world.getBlockState(blockPos.add(railOffset));
		
		if (state.getBlock() instanceof AbstractRailBlock) {
			AbstractRailBlock abstractRailBlock = (AbstractRailBlock)state.getBlock();
			RailShape railShape = (RailShape) state.get(abstractRailBlock.getShapeProperty());
			return railShape;
		} else {
			return null;
		}
	}
	
	/*
	 * Returns null if the rail is a curved rail
	 */
	private Vec3i getNextRailOffsetByVelocity(RailShape railShape, Vec3d velocity) {
		
		if (railShape == RailShape.EAST_WEST) {
			double x = velocity.getX();
			x = (x > 0) ? 1 : ((x == 0) ? 0 : -1);
			return new Vec3i(x, 0, 0);
		} else if (railShape == RailShape.NORTH_SOUTH) {
			double z = velocity.getZ();
			z = (z > 0) ? 1 : ((z == 0) ? 0 : -1);
			return new Vec3i(0, 0, z);
		}
		return null;
	}
}

