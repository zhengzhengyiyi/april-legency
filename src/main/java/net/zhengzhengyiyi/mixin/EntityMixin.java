package net.zhengzhengyiyi.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.zhengzhengyiyi.ModDimensionTypes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public World world;
    @Shadow public abstract double getX();
    @Shadow public abstract double getY();
    @Shadow public abstract double getZ();
    @Shadow public abstract float getYaw();
    @Shadow public abstract float getPitch();
    
    @Unique
    private int tickCount;

    @Inject(method = "baseTick", at = @At("TAIL"))
    private void onBaseTick(CallbackInfo ci) {
    	tickCount ++;
    	if (tickCount % 30 != 0) return;
        if (!this.world.isClient() && this.world instanceof ServerWorld serverWorld) {
            if (this.getY() > 700.0) {
                    ServerWorld targetWorld;
                    int targetY;

                    if (this.world.getRegistryKey().getValue().equals(World.OVERWORLD.getValue())) {
                        targetWorld = serverWorld.getServer().getWorld(ModDimensionTypes.THE_MOON_KEY);
                        targetY = 600;
                        
                        if (targetWorld != null) ((Entity)(Object)this).teleport(targetWorld, this.getX(), targetY, this.getZ(), Set.of(), this.getYaw(), 90.0F, true);
                    } 
                    else if (this.world.getRegistryKey().getValue().equals(ModDimensionTypes.THE_MOON_KEY.getValue())) {
                        targetWorld = serverWorld.getServer().getOverworld();
                        targetY = 650;
                        
                        if (targetWorld != null) ((Entity)(Object)this).teleport(targetWorld, this.getX(), targetY, this.getZ(), Set.of(), this.getYaw(), 90.0F, false);
                    } else {
                        targetWorld = null;
                        targetY = 0;
                    }
            }
        }
    }
}
