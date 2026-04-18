package net.zhengzhengyiyi.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ClickType;
import net.zhengzhengyiyi.event.PlayerEventHandler;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    /**
     * Intercept when a player picks up an item in a slot click.
     * onPickupSlotClick is declared on PlayerEntity, inherited by ServerPlayerEntity.
     */
    @Inject(method = "onPickupSlotClick", at = @At("TAIL"))
    private void onPickupSlotClick(ItemStack cursorStack, ItemStack slotStack, ClickType clickType, CallbackInfo ci) {
        ServerPlayerEntity self = (ServerPlayerEntity)(Object)this;
        if (!self.getEntityWorld().isClient() && !slotStack.isEmpty()) {
            PlayerEventHandler.onItemPickup(self, slotStack.copy());
        }
    }
}
