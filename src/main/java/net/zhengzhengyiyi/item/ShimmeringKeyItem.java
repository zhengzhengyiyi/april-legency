package net.zhengzhengyiyi.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.zhengzhengyiyi.block.ShimmeringDoorBlock;

/**
 * A key that can open the ShimmeringDoor.
 * Based on craftmine class_11051.
 */
public class ShimmeringKeyItem extends Item {
   public ShimmeringKeyItem(Item.Settings settings) {
      super(settings);
   }

   @Override
   public boolean hasGlint(ItemStack stack) {
      return true;
   }

   @Override
   public ActionResult useOnBlock(ItemUsageContext context) {
      PlayerEntity player = context.getPlayer();
      World world = context.getWorld();
      BlockPos pos = context.getBlockPos();
      BlockState state = world.getBlockState(pos);

      if (state.getBlock() instanceof ShimmeringDoorBlock) {
         if (state.get(DoorBlock.OPEN)) {
            return ActionResult.FAIL;
         }
         // Door is closed - unlock it
         world.setBlockState(pos, state.with(DoorBlock.OPEN, true));
         context.getStack().decrementUnlessCreative(1, player);
         return ActionResult.SUCCESS_SERVER;
      }

      return super.useOnBlock(context);
   }
}
