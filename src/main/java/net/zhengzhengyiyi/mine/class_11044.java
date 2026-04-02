package net.zhengzhengyiyi.mine;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.zhengzhengyiyi.component.ModDataComponentTypes;

public class class_11044 extends Slot {
   private final MineEffectGenerator field_58821;

   public class_11044(Inventory inventory, int i, int j, int k, MineEffectGenerator arg) {
      super(inventory, i, j, k);
      this.field_58821 = arg;
   }

   @Override
   public boolean canInsert(ItemStack stack) {
      return false;
   }

   @Override
   public boolean canTakeItems(PlayerEntity playerEntity) {
      ItemStack itemStack = this.getStack();
      boolean bl = itemStack.contains(ModDataComponentTypes.MINE_ACTIVE);
      Boolean boolean_ = itemStack.get(ModDataComponentTypes.MINE_COMPLETED);
      if (!bl && boolean_ == null) {
         if (playerEntity instanceof ServerPlayerEntity serverPlayerEntity) {
            this.field_58821.method_69526(serverPlayerEntity, itemStack);
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean method_69553() {
      return this.field_58821.method_69547();
   }
}
