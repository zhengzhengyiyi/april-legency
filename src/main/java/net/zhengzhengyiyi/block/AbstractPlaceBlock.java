package net.zhengzhengyiyi.block;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.tick.TickPriority;

public abstract class AbstractPlaceBlock extends FacingBlock {
   public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;
   private static final int TICK_DELAY = 1;

   protected AbstractPlaceBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(TRIGGERED, false));
   }

   @Override
   protected void neighborUpdate(BlockState state,
		   World world,
		   BlockPos pos,
		   Block sourceBlock,
		   @Nullable WireOrientation wireOrientation,
		   boolean notify) {
      boolean hasPower = world.isReceivingRedstonePower(pos);
      boolean isTriggered = state.get(TRIGGERED);
      if (hasPower && !isTriggered) {
         world.scheduleBlockTick(pos, this, TICK_DELAY, this.getTickPriority());
         world.setBlockState(pos, state.with(TRIGGERED, true), Block.NOTIFY_LISTENERS);
      } else if (!hasPower && isTriggered) {
         world.setBlockState(pos, state.with(TRIGGERED, false), Block.NOTIFY_LISTENERS);
      }
   }

   @Override
   public BlockState getPlacementState(ItemPlacementContext ctx) {
      return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
   }

   @Override
   public BlockRenderType getRenderType(BlockState state) {
      return BlockRenderType.MODEL;
   }

   @Override
   public BlockState rotate(BlockState state, BlockRotation rotation) {
      return state.with(FACING, rotation.rotate(state.get(FACING)));
   }

   @Override
   public BlockState mirror(BlockState state, BlockMirror mirror) {
      return state.rotate(mirror.getRotation(state.get(FACING)));
   }

   @Override
   protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
      builder.add(FACING, TRIGGERED);
   }

   protected abstract TickPriority getTickPriority();
   
   protected static <T extends AbstractPlaceBlock> com.mojang.serialization.MapCodec<T> createPlaceBlockCodec(java.util.function.Function<net.minecraft.block.AbstractBlock.Settings, T> factory) {
	    return com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(
	        instance -> instance.group(createSettingsCodec()).apply(instance, factory)
	    );
	}
}
