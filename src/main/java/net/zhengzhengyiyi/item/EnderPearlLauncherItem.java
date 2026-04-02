package net.zhengzhengyiyi.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Launches an ender pearl straight forward at high speed.
 * Based on craftmine class_11052.
 */
public class EnderPearlLauncherItem extends Item {
   public EnderPearlLauncherItem(Item.Settings settings) {
      super(settings);
   }

   @Override
   public ActionResult use(World world, PlayerEntity user, Hand hand) {
      if (world instanceof ServerWorld serverWorld) {
         ProjectileEntity.spawnWithVelocity(
            (sw, entity, stack) -> new EnderPearlEntity(sw, entity, stack),
            serverWorld,
            Items.ENDER_PEARL.getDefaultStack().copy(),
            user,
            0.0F,
            10.0F,
            0.0F
         );
      }
      return ActionResult.SUCCESS;
   }
}
