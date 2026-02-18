package net.zhengzhengyiyi.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.zhengzhengyiyi.accessor.MineServerWorldAccessor;
import net.zhengzhengyiyi.component.ModDataComponentTypes;
import net.zhengzhengyiyi.mine.MineEffect;
import net.zhengzhengyiyi.mine.class_11056;

import org.jetbrains.annotations.Nullable;

public class MineCrafterBlock extends BlockWithEntity {
   public static final MapCodec<MineCrafterBlock> field_58921 = createCodec(MineCrafterBlock::new);

   @Override
   public MapCodec<MineCrafterBlock> getCodec() {
      return field_58921;
   }

   protected MineCrafterBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   @Override
   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if (world instanceof ServerWorld && world.getBlockEntity(pos) instanceof MineCrafterBlockEntity lv) {
         player.openHandledScreen(lv);
      }

      return ActionResult.SUCCESS;
   }

   @Override
   protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      if (stack.contains(ModDataComponentTypes.WORLD_EFFECT_UNLOCK) && stack.contains(ModDataComponentTypes.WORLD_MODIFIERS)) {
         if (world instanceof ServerWorld serverWorld) {
            class_11056 lv = stack.get(ModDataComponentTypes.WORLD_MODIFIERS);

            for (MineEffect lv2 : lv.effects()) {
               ((MineServerWorldAccessor)serverWorld).method_69083(lv2);
            }
         }

         stack.setCount(0);
         return ActionResult.CONSUME;
      } else {
         return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
      }
   }

   @Override
   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new MineCrafterBlockEntity(pos, state);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
      return validateTicker(type, ModBlocks.MINE_CRAFTER_BLOCKENTITY, MineCrafterBlockEntity::tick);
   }
}
