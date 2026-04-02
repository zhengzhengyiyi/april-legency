package net.zhengzhengyiyi.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

public class LeafLitterBlock extends PlantBlock {
   public static final MapCodec<LeafLitterBlock> CODEC = createCodec(LeafLitterBlock::new);
   public static final EnumProperty<Direction> HORIZONTAL_FACING = Properties.HORIZONTAL_FACING;
   public static final IntProperty LAYERS = IntProperty.of("layers", 1, 4);
   
   private static final VoxelShape[] SHAPES = new VoxelShape[]{
      Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0),
      Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
      Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
      Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0)
   };

   public LeafLitterBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState(this.stateManager.getDefaultState()
         .with(HORIZONTAL_FACING, Direction.NORTH)
         .with(LAYERS, 1));
   }

   @Override
   protected MapCodec<LeafLitterBlock> getCodec() {
      return CODEC;
   }

   @Override
   public BlockState rotate(BlockState state, BlockRotation rotation) {
      return state.with(HORIZONTAL_FACING, rotation.rotate(state.get(HORIZONTAL_FACING)));
   }

   @Override
   public BlockState mirror(BlockState state, BlockMirror mirror) {
      return state.rotate(mirror.getRotation(state.get(HORIZONTAL_FACING)));
   }

   @Override
   public boolean canReplace(BlockState state, ItemPlacementContext context) {
      if (context.getStack().isOf(this.asItem()) && state.get(LAYERS) < 4) {
         return true;
      }
      return super.canReplace(state, context);
   }

   @Override
   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      BlockPos blockPos = pos.down();
      return world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, Direction.UP);
   }

   @Override
   public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPES[state.get(LAYERS) - 1];
   }

   @Override
   public BlockState getPlacementState(ItemPlacementContext ctx) {
      BlockState existingState = ctx.getWorld().getBlockState(ctx.getBlockPos());
      if (existingState.isOf(this)) {
         int layers = existingState.get(LAYERS);
         return existingState.with(LAYERS, Math.min(4, layers + 1));
      }
      return this.getDefaultState().with(HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
   }

   @Override
   protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
      builder.add(HORIZONTAL_FACING, LAYERS);
   }
}
