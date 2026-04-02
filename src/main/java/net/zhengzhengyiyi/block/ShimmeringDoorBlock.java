package net.zhengzhengyiyi.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.zhengzhengyiyi.item.ModItems;

public class ShimmeringDoorBlock extends DoorBlock {
   public static final MapCodec<ShimmeringDoorBlock> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(BlockSetType.CODEC.fieldOf("block_set_type").forGetter(DoorBlock::getBlockSetType), createSettingsCodec())
         .apply(instance, ShimmeringDoorBlock::new)
   );
   private static final MutableText SAY_THE_THING_MSG = Text.translatable("door.say_the_thing");

   @Override
   public MapCodec<? extends ShimmeringDoorBlock> getCodec() {
      return CODEC;
   }

   protected ShimmeringDoorBlock(BlockSetType blockSetType, AbstractBlock.Settings settings) {
      super(blockSetType, settings);
   }

   @Override
   protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      if (!stack.isOf(Items.TRIAL_KEY) && !stack.isOf(Items.OMINOUS_TRIAL_KEY)) {
         return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
      } else {
         // Give the player a no-medal trophy instead of letting them use the key
         player.getInventory().insertStack(TrophyBlock.createTrophyStack(TrophyType.NO_MEDAL));
         return ActionResult.FAIL;
      }
   }

   @Override
   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      player.sendMessage(SAY_THE_THING_MSG, true);
      return ActionResult.PASS;
   }
}
