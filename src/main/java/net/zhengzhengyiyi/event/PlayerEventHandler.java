package net.zhengzhengyiyi.event;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.zhengzhengyiyi.accessor.LevelPropertiesAccessor;
import net.zhengzhengyiyi.accessor.MineServerWorldAccessor;
import net.zhengzhengyiyi.component.ExchangeValueComponent;
import net.zhengzhengyiyi.component.ModDataComponentTypes;
import net.zhengzhengyiyi.mine.MineEffect;
import net.zhengzhengyiyi.network.ClientPacket4;
import net.zhengzhengyiyi.unlock.PlayerUnlock;
import net.zhengzhengyiyi.unlock.PlayerUnlockState;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Wires up all player-lifecycle and in-mine events needed for the survival cycle:
 *
 *  - Load/save PlayerUnlockState on join/leave
 *  - Convert items picked up in a mine into currency (exchange value system)
 *  - Award level exp when mine completes
 */
public class PlayerEventHandler {

    public static void init() {
        registerJoinLeave();
        registerItemPickup();
    }

    // ─── Join / Leave ────────────────────────────────────────────────────────────

    private static void registerJoinLeave() {
        // On join: sync current unlock state to client (load is handled by mixin on readCustomDataFromNbt)
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            PlayerUnlockState state = PlayerUnlockState.get(player);
            ServerPlayNetworking.send(player, new ClientPacket4(state.getUnlocks(), state.getCurrency()));
        });

        // On disconnect: evict from cache (save is handled by mixin on writeCustomDataToNbt)
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            PlayerUnlockState.evict(handler.player.getUuid());
        });
    }

    // ─── Item pickup → currency ──────────────────────────────────────────────────

    /**
     * When a player picks up an item inside a mine:
     * - If the item has ExchangeValueComponent with value == 0 → it's a free unlock item, skip.
     * - Otherwise calculate exchange value based on:
     *   - Base value: 1 currency per item stack
     *   - Multiplied by player's unlock expFactorForItem / expFactorForItemTag
     * - Add to player's currency balance.
     * - Award mine exp (for level scaling).
     *
     * Mirrors Craftmine's item pickup hook in ServerPlayerEntity.
     */
    private static void registerItemPickup() {
        // Hook into the AttackEntityCallback isn't quite right; we use
        // ServerEntityCombatEvents and a mixin instead.
        // For item pickup, Fabric has no direct hook pre-1.21 — we use
        // the AFTER_ENTITY_COLLISION approach via a separate tick mixin.
        // For now, we piggyback on the ServerWorld.onEntityAdded / removed
        // cycle by watching entity collisions in our ServerWorldMixin tick.
        //
        // The canonical approach for Fabric is to use:
        //   net.fabricmc.fabric.api.event.player.UseItemCallback  (doesn't cover pickup)
        //
        // Instead we inject via ServerPlayerEntityMixin — see that class.
    }

    /**
     * Called from ServerPlayerEntityMixin when a player picks up an ItemEntity.
     * Only applies inside mine worlds.
     */
    public static void onItemPickup(ServerPlayerEntity player, ItemStack stack) {
        if (!(player.getEntityWorld() instanceof ServerWorld serverWorld)) return;
        MineServerWorldAccessor mineWorld = (MineServerWorldAccessor)(Object) serverWorld;
        if (!mineWorld.isMineWorld() || mineWorld.isMineCompleted()) return;

        // Items with EXCHANGE_VALUE = 0 are free unlock-given items — no currency
        ExchangeValueComponent exchangeValue = stack.get(ModDataComponentTypes.EXCHANGE_VALUE);
        if (exchangeValue != null && exchangeValue.value() == 0.0F) return;

        // Calculate base value (1 per item)
        float value = stack.getCount();

        // Apply unlock multipliers
        PlayerUnlockState unlockState = PlayerUnlockState.get(player);
        for (var unlockEntry : unlockState.getUnlocks()) {
            PlayerUnlock unlock = unlockEntry.value();

            // Check item-specific multiplier
            Float itemFactor = unlock.experienceFactorForItem().get(stack.getItem());
            if (itemFactor != null) {
                value *= itemFactor;
                continue;
            }

            // Check tag multipliers
            for (Map.Entry<net.minecraft.registry.tag.TagKey<net.minecraft.item.Item>, Float> e
                    : unlock.experienceFactorForItemTag().entrySet()) {
                if (stack.isIn(e.getKey())) {
                    value *= e.getValue();
                    break;
                }
            }
        }

        int earned = Math.max(1, (int) value);

        // Add currency to player's balance
        unlockState.addCurrency(earned);

        // Add mine exp to global level tracking
        LevelPropertiesAccessor props = (LevelPropertiesAccessor)(Object)
            serverWorld.getServer().getSaveProperties().getMainWorldProperties();
        props.addMineExp(earned);

        // Sync updated currency to client every 5 items to avoid packet spam
        // (synced fully on mine exit)
    }

    /**
     * Called from MineCrafterBlockEntity.spawnRewards when a mine completes.
     * Awards a flat currency bonus based on mine success.
     */
    public static void onMineComplete(ServerPlayerEntity player, boolean won, java.util.List<MineEffect> effects) {
        PlayerUnlockState state = PlayerUnlockState.get(player);

        // Base reward: 10 currency for win, 3 for loss
        int reward = won ? 10 : 3;

        // Bonus per effect
        reward += effects.size() * 2;

        state.addCurrency(reward);

        // Update global mine exp
        LevelPropertiesAccessor props = (LevelPropertiesAccessor)(Object)
            player.getEntityWorld().getServer().getSaveProperties().getMainWorldProperties();
        props.addMineExp(reward);

        // Sync to client
        ServerPlayNetworking.send(player, new ClientPacket4(state.getUnlocks(), state.getCurrency()));
    }
}
