package net.zhengzhengyiyi.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.PortalManager;
import net.zhengzhengyiyi.block.NeitherPortalEntity;

@Mixin(PortalManager.class)
public class PortalManagerMixin {
	@Unique
	private int dimensionId = 0;
	
	@Shadow private BlockPos getPortalPos() {return null;}
	
	@Unique private World world;
	
	@Inject(method="setInPortal", at=@At("TAIL"))
	private void setInPortal(boolean inPortal, CallbackInfo ci) {
		if (world != null) {
            BlockEntity blockEntity = world.getBlockEntity(getPortalPos());
            if (blockEntity instanceof NeitherPortalEntity) {
            	dimensionId = ((NeitherPortalEntity)blockEntity).getDimensionId();
            }
        }
	}
	
	@Inject(method="tick", at=@At("HEAD"))
	private void tick(ServerWorld world, Entity entity, boolean canUsePortals, CallbackInfoReturnable<Boolean> cir) {
		this.world = world;
	}
}
