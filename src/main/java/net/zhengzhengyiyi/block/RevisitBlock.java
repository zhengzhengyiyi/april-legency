package net.zhengzhengyiyi.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.zhengzhengyiyi.component.ModDataComponentTypes;

public class RevisitBlock extends Block {
   public static final MapCodec<RevisitBlock> CODEC = createCodec(RevisitBlock::new);

   @Override
   public MapCodec<? extends RevisitBlock> getCodec() {
      return CODEC;
   }

   protected RevisitBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   @Override
   protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      if (stack.contains(ModDataComponentTypes.MINE_COMPLETED) && stack.contains(ModDataComponentTypes.DIMENSION_ID)) {
         RegistryKey<DimensionOptions> dimensionKey = stack.get(ModDataComponentTypes.DIMENSION_ID);
         if (dimensionKey != null) {
            MiningPortalBlock.createPortal(world, pos.up(), dimensionKey, true);
            stack.decrementUnlessCreative(1, player);
            return ActionResult.SUCCESS;
         }
      }
      return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
   }
}
