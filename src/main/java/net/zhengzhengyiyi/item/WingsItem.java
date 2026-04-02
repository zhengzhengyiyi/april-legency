package net.zhengzhengyiyi.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Wings item that grants levitation and slow falling.
 * Based on craftmine class_11048.
 */
public class WingsItem extends Item {
   public WingsItem(Item.Settings settings) {
      super(settings);
   }

   @Override
   public ActionResult use(World world, PlayerEntity user, Hand hand) {
      if (world instanceof ServerWorld && user instanceof ServerPlayerEntity serverPlayer) {
         serverPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 100));
         serverPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 200));
      }
      return ActionResult.SUCCESS;
   }
}
