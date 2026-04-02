package net.zhengzhengyiyi.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SideShapeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class CactusFlowerBlock extends PlantBlock {
   public static final MapCodec<CactusFlowerBlock> CODEC = createCodec(CactusFlowerBlock::new);
   private static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);

   @Override
   public MapCodec<? extends CactusFlowerBlock> getCodec() {
      return CODEC;
   }

   public CactusFlowerBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   @Override
   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPE;
   }

   @Override
   protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
      BlockState blockState = world.getBlockState(pos);
      return blockState.isOf(Blocks.CACTUS) || blockState.isOf(Blocks.FARMLAND) || blockState.isSideSolid(world, pos, Direction.UP, SideShapeType.CENTER);
   }
}
