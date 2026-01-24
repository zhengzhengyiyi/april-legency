package net.zhengzhengyiyi.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.tick.OrderedTick;

public class AntBlock extends HorizontalFacingBlock {
   public AntBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
   }

   @Override
   public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      BlockState floorState = world.getBlockState(pos.down());
      if (floorState.isOf(Blocks.WHITE_CONCRETE)) {
         this.moveAnt(state, world, pos, AntAction.TURN_RIGHT);
      } else if (floorState.isOf(Blocks.BLACK_CONCRETE)) {
         this.moveAnt(state, world, pos, AntAction.TURN_LEFT);
      }
   }

   private void moveAnt(BlockState state, ServerWorld world, BlockPos pos, AntAction action) {
      Direction currentFacing = state.get(FACING);
      Direction newFacing = (action == AntAction.TURN_RIGHT) ? currentFacing.rotateYClockwise() : currentFacing.rotateYCounterclockwise();
      BlockPos nextPos = pos.offset(newFacing);

      if (world.isInBuildLimit(nextPos)) {
         switch (action) {
            case TURN_RIGHT -> {
               world.setBlockState(pos.down(), Blocks.BLACK_CONCRETE.getDefaultState(), 19);
               world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
               world.setBlockState(nextPos, state.with(FACING, newFacing), 3);
            }
            case TURN_LEFT -> {
               world.setBlockState(pos.down(), Blocks.WHITE_CONCRETE.getDefaultState(), 19);
               world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
               world.setBlockState(nextPos, state.with(FACING, newFacing), 3);
            }
         }
      }
   }

   @Override
   public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
      world.getBlockTickScheduler().scheduleTick(OrderedTick.create(this, pos));
   }

   @Override
   protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
      builder.add(FACING);
   }

   enum AntAction {
      TURN_RIGHT,
      TURN_LEFT;
   }

   @SuppressWarnings("unchecked")
   @Override
   protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
	   return (MapCodec<? extends HorizontalFacingBlock>)(Object)CODEC;
   }
}
