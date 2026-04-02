package net.zhengzhengyiyi.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.screen.slot.Slot;

@Mixin(Slot.class)
public class SlotMixin {
   @Mutable
   @Shadow @Final public int x;

   @Mutable
   @Shadow @Final public int y;
}
