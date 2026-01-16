package net.zhengzhengyiyi.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.tick.TickPriority;
import net.zhengzhengyiyi.rules.VoteRules;

public class PickaxeBlock extends AbstractPlaceBlock {
   protected PickaxeBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   @Override
   public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (VoteRules.PICKAXE_BLOCK.isActive()) {
         BlockPos blockPos = pos.offset(state.get(FACING));
         if (!world.getBlockState(blockPos).isAir()) {
            world.breakBlock(blockPos, true);
         }
      }
   }

   @Override
   protected TickPriority getTickPriority() {
	   return TickPriority.EXTREMELY_HIGH;
   }

   @Override
   protected MapCodec<? extends FacingBlock> getCodec() {
	   return null;
   }
}
