package net.zhengzhengyiyi.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemPlacementContext;
import net.zhengzhengyiyi.component.ModDataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import net.zhengzhengyiyi.item.ModItems;
import org.jetbrains.annotations.Nullable;

public class TrophyBlock extends HorizontalFacingBlock {
   public static final MapCodec<TrophyBlock> CODEC = createCodec(TrophyBlock::new);
   public static final EnumProperty<TrophyType> TROPHY_TYPE = EnumProperty.of("trophy_type", TrophyType.class);

   @Override
   protected MapCodec<TrophyBlock> getCodec() {
      return CODEC;
   }

   public TrophyBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(TROPHY_TYPE, TrophyType.GOLD));
   }

   @Override
   protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
      builder.add(TROPHY_TYPE, FACING);
   }

   @Nullable
   @Override
   public BlockState getPlacementState(ItemPlacementContext ctx) {
      TrophyType type = ctx.getStack().getOrDefault(ModDataComponentTypes.TYPE_TROPHY, TrophyType.GOLD);
      return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite()).with(TROPHY_TYPE, type);
   }

   @Override
   protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
      ItemStack itemStack = super.getPickStack(world, pos, state, includeData);
      itemStack.set(ModDataComponentTypes.TYPE_TROPHY, state.get(TROPHY_TYPE));
      return itemStack;
   }

   public static ItemStack createTrophyStack(TrophyType type) {
      ItemStack itemStack = new ItemStack(ModItems.TROPHY);
      itemStack.set(ModDataComponentTypes.TYPE_TROPHY, type);
      return itemStack;
   }
}
