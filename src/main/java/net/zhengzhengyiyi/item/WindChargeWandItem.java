package net.zhengzhengyiyi.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.WindChargeItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Launches a wind charge forward.
 * Based on craftmine class_11053.
 */
public class WindChargeWandItem extends Item {
   public WindChargeWandItem(Item.Settings settings) {
      super(settings);
   }

   @Override
   public ActionResult use(World world, PlayerEntity user, Hand hand) {
      if (world instanceof ServerWorld serverWorld) {
         ProjectileEntity.spawnWithVelocity(
            (sw, entity, stack) -> new WindChargeEntity(user, world, user.getX(), user.getEyeY(), user.getZ()),
            serverWorld,
            Items.WIND_CHARGE.getDefaultStack().copy(),
            user,
            0.0F,
            WindChargeItem.POWER,
            0.0F
         );
      }
      return ActionResult.SUCCESS;
   }
}
