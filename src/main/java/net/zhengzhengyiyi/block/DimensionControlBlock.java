package net.zhengzhengyiyi.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.zhengzhengyiyi.screen.DimensionControlScreenHandler;

public class DimensionControlBlock extends Block {
   public static final MapCodec<DimensionControlBlock> CODEC = createCodec(DimensionControlBlock::new);
   private static final Text TITLE = Text.translatable("container.dimension_control");

   @Override
   public MapCodec<DimensionControlBlock> getCodec() {
      return CODEC;
   }

   public DimensionControlBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   @Override
   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if (!world.isClient()) {
         player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
      }

      return ActionResult.SUCCESS;
   }

   @Override
   protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
      return new SimpleNamedScreenHandlerFactory(
         (syncId, playerInventory, player) -> new DimensionControlScreenHandler(syncId, playerInventory, ScreenHandlerContext.create(world, pos)), 
         TITLE
      );
   }
}
