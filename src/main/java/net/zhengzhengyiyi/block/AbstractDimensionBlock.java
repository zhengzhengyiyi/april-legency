package net.zhengzhengyiyi.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface AbstractDimensionBlock {
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl);
	public void convertPortal(World world, BlockPos startPos, BlockState state, int dimId);
}
