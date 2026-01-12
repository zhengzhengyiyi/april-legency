package net.zhengzhengyiyi.mixin;

import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WeightedPressurePlateBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.zhengzhengyiyi.generator.MoonLabGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractPressurePlateBlock.class)
public abstract class WeightedPressurePlateMixin {

    @Inject(
        method = "onEntityCollision(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)V",
        at = @At("TAIL")
    )
    private void injectRocketTrigger(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl, CallbackInfo ci) {
        if (!world.isClient() && (Object) this instanceof WeightedPressurePlateBlock) {
            if (entity instanceof ServerPlayerEntity) {
                ServerWorld serverWorld = (ServerWorld) world;
                BlockPos downPos = pos.down();
                BlockEntity blockEntity = world.getBlockEntity(downPos);

                if (blockEntity instanceof DropperBlockEntity) {
                    world.setBlockState(downPos, Blocks.LAVA.getDefaultState(), 3);
                    MoonLabGenerator.spawnRocketLaunch(serverWorld, downPos.down());
                }
            }
        }
    }
}
