package net.zhengzhengyiyi.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.screen.slot.Slot;
import net.zhengzhengyiyi.accessor.SlotPositionAccessor;

@Mixin(Slot.class)
public class SlotMixin implements SlotPositionAccessor {
   @Mutable
   @Shadow @Final public int x;

   @Mutable
   @Shadow @Final public int y;

   @Override
   public void setSlotPos(int newX, int newY) {
      this.x = newX;
      this.y = newY;
   }
}
