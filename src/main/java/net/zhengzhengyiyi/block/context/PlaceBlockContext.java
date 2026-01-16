package net.zhengzhengyiyi.block.context;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PlaceBlockContext extends ItemPlacementContext {
   public PlaceBlockContext(World world, Hand hand, ItemStack itemStack, BlockHitResult blockHitResult) {
      super(world, null, hand, itemStack, blockHitResult);
      this.canReplaceExisting = world.getBlockState(blockHitResult.getBlockPos()).canReplace(this);
   }

   public static PlaceBlockContext create(World world, BlockPos blockPos, Direction direction, ItemStack itemStack) {
      return new PlaceBlockContext(
         world,
         Hand.MAIN_HAND,
         itemStack,
         new BlockHitResult(
            new Vec3d(
               blockPos.getX() + 0.5 + direction.getOffsetX() * 0.5,
               blockPos.getY() + 0.5 + direction.getOffsetY() * 0.5,
               blockPos.getZ() + 0.5 + direction.getOffsetZ() * 0.5
            ),
            direction,
            blockPos,
            false
         )
      );
   }

   @Override
   public Direction getPlayerLookDirection() {
      return this.getHitResult().getSide();
   }

   @Override
   public Direction getVerticalPlayerLookDirection() {
      return this.getHitResult().getSide() == Direction.UP ? Direction.UP : Direction.DOWN;
   }

   @Override
   public Direction[] getPlacementDirections() {
      Direction direction = this.getHitResult().getSide();
      Direction[] directions = new Direction[]{direction, null, null, null, null, direction.getOpposite()};
      int i = 0;

      for (Direction direction2 : Direction.values()) {
         if (direction2 != direction && direction2 != direction.getOpposite()) {
            directions[++i] = direction;
         }
      }

      return directions;
   }
}