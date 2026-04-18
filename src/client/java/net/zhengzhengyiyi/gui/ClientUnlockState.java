package net.zhengzhengyiyi.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.entry.RegistryEntry;
import net.zhengzhengyiyi.unlock.PlayerUnlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Client-side store for the player's unlocks and currency balance.
 * Populated by ClientPacket4 (S2C sync) sent after every purchase.
 *
 * Mirrors class_11139's obtained map but simplified — the client only
 * needs to know what it has purchased and how much currency it holds.
 */
@Environment(EnvType.CLIENT)
public final class ClientUnlockState {

    private static final List<RegistryEntry<PlayerUnlock>> unlocks = new ArrayList<>();
    private static int currency = 0;

    private ClientUnlockState() {}

    /** Called when ClientPacket4 arrives. */
    public static void apply(List<RegistryEntry<PlayerUnlock>> newUnlocks, int newCurrency) {
        unlocks.clear();
        unlocks.addAll(newUnlocks);
        currency = newCurrency;
    }

    public static List<RegistryEntry<PlayerUnlock>> getUnlocks() {
        return Collections.unmodifiableList(unlocks);
    }

    public static int getCurrency() {
        return currency;
    }

    public static boolean hasUnlock(RegistryEntry<PlayerUnlock> unlock) {
        return unlocks.stream().anyMatch(e -> e.value() == unlock.value());
    }

    /** True if a given unlock's parent is already purchased (or it has no parent). */
    public static boolean canAfford(RegistryEntry<PlayerUnlock> unlock) {
        return currency >= unlock.value().unlockPrice();
    }

    public static boolean parentUnlocked(RegistryEntry<PlayerUnlock> unlock) {
        return unlock.value().parent().isEmpty() || hasUnlock(unlock.value().parent().get());
    }
}
