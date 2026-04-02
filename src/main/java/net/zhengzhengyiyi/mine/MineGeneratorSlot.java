package net.zhengzhengyiyi.mine;

import java.util.List;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.zhengzhengyiyi.component.ModDataComponentTypes;
import net.zhengzhengyiyi.item.ModItems;

public class MineGeneratorSlot extends Slot {
   public MineEffectGenerator generator;
   public boolean locked;

   public MineGeneratorSlot(Inventory inventory, int index, int x, int y, MineEffectGenerator generator, boolean locked) {
      super(inventory, index, x, y);
      this.locked = locked;
      this.generator = generator;
   }

   @Override
   public boolean canInsert(ItemStack stack) {
	   if (this.generator.method_69547() || this.generator.method_69548() || this.generator.method_69541() || this.locked) {
         return false;
      } else if (!stack.isOf(ModItems.MINE_INGREDIENT)) {
         return false;
      } else {
         class_11056 component = stack.get(ModDataComponentTypes.WORLD_MODIFIERS);
         if (component == null) {
            return false;
         } else {
            List<MineEffect> availableEffects = this.generator.method_69543().toList();
            return component.effects().stream().allMatch(effect -> effect.method_69925(availableEffects));
         }
      }
   }

   @Override
   public boolean canTakeItems(PlayerEntity player) {
	   return !this.generator.method_69547() && !this.generator.method_69548() && !this.generator.method_69541() && !this.locked
         ? super.canTakeItems(player)
         : false;
   }

   @Override
   public void markDirty() {
      super.markDirty();
      this.generator.onContentChanged(this.inventory);
   }

   @Override
   public boolean isEnabled() {
      return super.isEnabled() && !this.generator.method_69548() && !this.generator.method_69541();
   }
}