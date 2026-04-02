package net.zhengzhengyiyi.mine;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class class_11042 extends Slot {
   final MineEffectGenerator field_58801;

   public class_11042(Inventory inventory, int i, int j, int k, MineEffectGenerator arg) {
      super(inventory, i, j, k);
      this.field_58801 = arg;
   }

   @Override
   public boolean canTakeItems(PlayerEntity playerEntity) {
      return false;
   }

   @Override
   public boolean canInsert(ItemStack stack) {
      return false;
   }

   @Override
   public boolean isEnabled() {
      return super.isEnabled() && !this.getStack().isEmpty() && (!this.field_58801.method_69541() || this.field_58801.method_69548());
   }
}
