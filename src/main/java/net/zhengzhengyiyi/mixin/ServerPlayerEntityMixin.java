package net.zhengzhengyiyi.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ClickType;
import net.zhengzhengyiyi.event.PlayerEventHandler;
import net.zhengzhengyiyi.unlock.PlayerUnlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Split into two targets:
 *  - PlayerEntity  → onPickupSlotClick (declared there, not in ServerPlayerEntity)
 *  - ServerPlayerEntity → writeCustomData / readCustomData (persistence)
 */
@Mixin(targets = {
    "net.minecraft.entity.player.PlayerEntity",
    "net.minecraft.server.network.ServerPlayerEntity"
})
public abstract class ServerPlayerEntityMixin {

    // ── Item pickup (declared on PlayerEntity) ────────────────────────────────

    /**
     * onPickupSlotClick is declared on PlayerEntity.
     * Guard with instanceof so it only fires on the server side.
     */
    @Inject(method = "onPickupSlotClick", at = @At("TAIL"), require = 0)
    private void onPickupSlotClick(ItemStack cursorStack, ItemStack slotStack,
                                   ClickType clickType, CallbackInfo ci) {
        if (!((Object)this instanceof ServerPlayerEntity self)) return;
        if (!self.getEntityWorld().isClient() && !slotStack.isEmpty()) {
            PlayerEventHandler.onItemPickup(self, slotStack.copy());
        }
    }

    // ── Persistence (declared on ServerPlayerEntity) ──────────────────────────

    @Inject(method = "writeCustomData", at = @At("TAIL"), require = 0)
    private void writeUnlockData(WriteView view, CallbackInfo ci) {
        if (!((Object)this instanceof ServerPlayerEntity self)) return;
        PlayerUnlockState state = PlayerUnlockState.CACHE.get(self.getUuid());
        if (state == null) return;

        WriteView sub = view.get("player_unlock_data");
        sub.putInt("currency", state.getCurrency());
        WriteView.ListView list = sub.getList("unlocks");
        for (net.minecraft.registry.entry.RegistryEntry<net.zhengzhengyiyi.unlock.PlayerUnlock> entry : state.getUnlocks()) {
            entry.getKey().ifPresent(key -> list.add().putString("id", key.getValue().toString()));
        }
    }

    @Inject(method = "readCustomData", at = @At("TAIL"), require = 0)
    private void readUnlockData(ReadView view, CallbackInfo ci) {
        if (!((Object)this instanceof ServerPlayerEntity self)) return;
        java.util.Optional<ReadView> subOpt = view.getOptionalReadView("player_unlock_data");
        if (subOpt.isEmpty()) return;

        ReadView sub = subOpt.get();
        int currency = sub.getInt("currency", 0);

        java.util.List<net.minecraft.registry.entry.RegistryEntry<net.zhengzhengyiyi.unlock.PlayerUnlock>> unlocks = new java.util.ArrayList<>();
        sub.getListReadView("unlocks").forEach(item -> {
            String idStr = item.getString("id", "");
            if (!idStr.isEmpty()) {
                net.minecraft.util.Identifier id = net.minecraft.util.Identifier.tryParse(idStr);
                if (id != null) {
                    net.zhengzhengyiyi.AprilsLegacy.PLAYER_UNLOCK.getOptionalValue(id).ifPresent(unlock -> {
                        net.minecraft.registry.entry.RegistryEntry<net.zhengzhengyiyi.unlock.PlayerUnlock> entry =
                            net.zhengzhengyiyi.AprilsLegacy.PLAYER_UNLOCK.getEntry(unlock);
                        if (entry != null) unlocks.add(entry);
                    });
                }
            }
        });

        PlayerUnlockState.CACHE.put(self.getUuid(),
            PlayerUnlockState.fromData(self.getUuid(), currency, unlocks));
    }
}
