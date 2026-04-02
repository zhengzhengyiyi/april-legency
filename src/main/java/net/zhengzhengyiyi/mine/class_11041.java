package net.zhengzhengyiyi.mine;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.zhengzhengyiyi.component.ModDataComponentTypes;
import net.zhengzhengyiyi.mine.class_11056;

public class class_11041 extends Slot {
   final MineEffectGenerator field_58800;

   public class_11041(Inventory inventory, int i, int j, int k, MineEffectGenerator arg) {
      super(inventory, i, j, k);
      this.field_58800 = arg;
   }

   @Override
   public boolean canInsert(ItemStack stack) {
      return true;
   }

   @Override
   public boolean canTakeItems(PlayerEntity playerEntity) {
      boolean bl = this.method_69517();
      if (!bl) {
         this.field_58800.setCursorStack(ItemStack.EMPTY);
      }

      return super.canTakeItems(playerEntity) && bl;
   }

   public boolean method_69517() {
      if (this.getStack().isEmpty()) {
         return true;
      } else {
         class_11056 lv = this.getStack().get(ModDataComponentTypes.WORLD_MODIFIERS);
         return lv == null
            ? false
            : this.field_58800.method_69543().allMatch(arg2 -> arg2.method_69925(lv.effects()))
               && this.field_58800.method_69543().noneMatch(arg2 -> lv.effects().contains(arg2));
      }
   }

   @Override
   public void onTakeItem(PlayerEntity player, ItemStack stack) {
      this.setStackNoCallbacks(stack.copyWithCount(1));
   }

   @Override
   public void setStack(ItemStack stack, ItemStack previousStack) {
      if (!previousStack.isEmpty()) {
         this.setStackNoCallbacks(previousStack.copyWithCount(1));
      }
   }

   @Override
   public boolean isEnabled() {
      return super.isEnabled() && (!this.field_58800.method_69541() || this.field_58800.method_69548());
   }
}
