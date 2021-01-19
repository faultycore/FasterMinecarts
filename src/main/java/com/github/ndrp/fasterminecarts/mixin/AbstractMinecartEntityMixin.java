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
import com.github.ndrp.fasterminecarts.MinecartUtility;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends Entity {
	
	// Needed because Entity has no default constructor
	public AbstractMinecartEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}
	
	@Inject(method = "getMaxOffRailSpeed", at = @At("HEAD"), cancellable = true)
	protected void onGetMaxOffRailSpeed(CallbackInfoReturnable<Double> cir) {
		
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
			Vec3i nextRailOffset = MinecartUtility.getNextRailOffsetByVelocity(railShape, v);
			
			if (nextRailOffset == null) {
				cir.setReturnValue(slowdownSpeed);
				return;
			}
				
			for (int i = 0; i <= offset; i++) {	
				RailShape railShapeAtOffset = null;
				
				railShapeAtOffset = MinecartUtility.getRailShapeAtOffset(new Vec3i(nextRailOffset.getX() * i, 0, nextRailOffset.getZ() * i), blockPos, this.world);
				
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
}

