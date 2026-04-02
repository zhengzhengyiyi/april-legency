package net.zhengzhengyiyi.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

/**
 * A modified Eye of Ender that also works in mine worlds.
 * Based on craftmine class_11047.
 */
public class EnderEyeItem extends Item {
   public EnderEyeItem(Item.Settings settings) {
      super(settings);
   }

   @Override
   public int getMaxUseTime(ItemStack stack, LivingEntity user) {
      return 0;
   }

   @Override
   public ActionResult use(World world, PlayerEntity user, Hand hand) {
      ItemStack itemStack = user.getStackInHand(hand);
      user.setCurrentHand(hand);
      if (world instanceof ServerWorld serverWorld) {
         BlockPos blockPos = serverWorld.locateStructure(StructureTags.EYE_OF_ENDER_LOCATED, user.getBlockPos(), 100, false);
         if (blockPos == null) {
            return ActionResult.CONSUME;
         }

         EyeOfEnderEntity eyeOfEnder = new EyeOfEnderEntity(world, user.getX(), user.getBodyY(0.5), user.getZ());
         eyeOfEnder.setItem(itemStack);
         eyeOfEnder.initTargetPos(blockPos.toCenterPos());
         world.emitGameEvent(GameEvent.PROJECTILE_SHOOT, eyeOfEnder.getBlockPos().toCenterPos(), GameEvent.Emitter.of(user));
         world.spawnEntity(eyeOfEnder);

         if (user instanceof ServerPlayerEntity serverPlayer) {
            Criteria.USED_ENDER_EYE.trigger(serverPlayer, blockPos);
         }

         float pitch = MathHelper.lerp(world.random.nextFloat(), 0.33F, 0.5F);
         world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 1.0F, pitch);
         itemStack.decrementUnlessCreative(1, user);
         user.incrementStat(Stats.USED.getOrCreateStat(this));
      }

      return ActionResult.SUCCESS_SERVER;
   }
}
