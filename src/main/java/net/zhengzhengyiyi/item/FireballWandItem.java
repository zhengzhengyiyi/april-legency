package net.zhengzhengyiyi.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * A wand that shoots fireballs (or dragon fireballs if the player has the right effects).
 * Based on craftmine class_11049.
 */
public class FireballWandItem extends Item {
   public FireballWandItem(Item.Settings settings) {
      super(settings);
   }

   @Override
   public ActionResult use(World world, PlayerEntity user, Hand hand) {
      if (world instanceof ServerWorld && user instanceof ServerPlayerEntity serverPlayer) {
         Vec3d direction = user.getRotationVec(1.0F).normalize();
         double spawnX = user.getX() + direction.x * 4.0;
         double spawnY = user.getBodyY(0.5);
         double spawnZ = user.getZ() + direction.z * 4.0;

         // Dragon fireball if player has both dragon-related effects, otherwise normal fireball
         FireballEntity fireball = new FireballEntity(world, user, direction, 1);
         fireball.accelerationPower = 1.0;
         fireball.setPosition(spawnX, spawnY, spawnZ);
         world.spawnEntity(fireball);
      }
      return ActionResult.SUCCESS;
   }
}
