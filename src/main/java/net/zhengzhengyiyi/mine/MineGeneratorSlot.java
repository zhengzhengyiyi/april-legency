package net.zhengzhengyiyi.mine;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.zhengzhengyiyi.component.ModDataComponentTypes;
import net.zhengzhengyiyi.item.ModItems;

public class MineGeneratorSlot extends Slot {
   public MineEffectGenerator generator;
   public boolean locked;
   public float field_58825;
   public float field_58826;
   private boolean enabled = true;

   public MineGeneratorSlot(Inventory inventory, int index, int x, int y, MineEffectGenerator generator, boolean locked) {
      super(inventory, index, x, y);
      this.locked = locked;
      this.generator = generator;
   }

   public void setPos(int newX, int newY) {
      ((net.zhengzhengyiyi.accessor.SlotPositionAccessor) this).setSlotPos(newX, newY);
   }

   public void method_69556(boolean enabled) {
      this.enabled = enabled;
   }

   public boolean method_69555() {
      return this.generator.method_69547();
   }

   public boolean method_69553() {
      return this.generator.method_69547();
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
