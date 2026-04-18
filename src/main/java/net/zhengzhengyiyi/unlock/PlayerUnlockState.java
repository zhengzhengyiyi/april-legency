package net.zhengzhengyiyi.unlock;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.AprilsLegacy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks which PlayerUnlocks a player has purchased and their currency balance.
 * Stored in a server-side map keyed by player UUID, persisted via player NBT
 * using the "player_unlock_data" key in the player's custom NBT compound.
 *
 * Mirrors Craftmine's class_10959_fixed (field_58300 in ServerPlayerEntity).
 */
public class PlayerUnlockState {

    private static final String NBT_ROOT = "player_unlock_data";
    private static final String NBT_UNLOCKS = "unlocks";
    private static final String NBT_CURRENCY = "currency";

    /** Server-side cache: UUID → state. Populated on player join, cleared on leave. */
    public static final Map<UUID, PlayerUnlockState> CACHE = new HashMap<>();

    private final List<RegistryEntry<PlayerUnlock>> unlocks = new ArrayList<>();
    private int currency = 0;

    private PlayerUnlockState() {}

    /**
     * Factory: build a state from already-resolved data (used by mixin read path).
     */
    public static PlayerUnlockState fromData(UUID uuid, int currency,
            List<RegistryEntry<PlayerUnlock>> unlocks) {
        PlayerUnlockState state = new PlayerUnlockState();
        state.currency = currency;
        state.unlocks.addAll(unlocks);
        return state;
    }

    /**
     * Get the cached unlock state for a player.
     * If not cached, creates a new empty state.
     */
    public static PlayerUnlockState get(ServerPlayerEntity player) {
        return CACHE.computeIfAbsent(player.getUuid(), uuid -> new PlayerUnlockState());
    }

    /**
     * Load state from player NBT. Called on player join.
     */
    public static void load(ServerPlayerEntity player, NbtCompound playerNbt) {
        PlayerUnlockState state = new PlayerUnlockState();
        if (playerNbt.contains(NBT_ROOT)) {
            NbtCompound root = playerNbt.getCompoundOrEmpty(NBT_ROOT);
            state.readNbt(root);
        }
        CACHE.put(player.getUuid(), state);
    }

    /**
     * Save state to player NBT. Called on player save/leave.
     */
    public static void save(ServerPlayerEntity player, NbtCompound playerNbt) {
        PlayerUnlockState state = CACHE.get(player.getUuid());
        if (state != null) {
            NbtCompound root = new NbtCompound();
            state.writeNbt(root);
            playerNbt.put(NBT_ROOT, root);
        }
    }

    /**
     * Remove from cache on player disconnect.
     */
    public static void evict(UUID uuid) {
        CACHE.remove(uuid);
    }

    public boolean hasUnlock(RegistryEntry<PlayerUnlock> unlock) {
        return unlocks.stream().anyMatch(e -> e.value() == unlock.value());
    }

    public void addUnlock(RegistryEntry<PlayerUnlock> unlock) {
        if (!hasUnlock(unlock)) {
            unlocks.add(unlock);
        }
    }

    public List<RegistryEntry<PlayerUnlock>> getUnlocks() {
        return List.copyOf(unlocks);
    }

    public int getCurrency() {
        return currency;
    }

    public void addCurrency(int amount) {
        this.currency += amount;
    }

    public void removeCurrency(int amount) {
        this.currency = Math.max(0, this.currency - amount);
    }

    public void readNbt(NbtCompound nbt) {
        this.currency = nbt.getInt(NBT_CURRENCY, 0);
        this.unlocks.clear();

        NbtList list = nbt.getListOrEmpty(NBT_UNLOCKS);
        for (NbtElement element : list) {
            if (element instanceof NbtString nbtString) {
                Identifier id = Identifier.tryParse(nbtString.value());
                if (id != null) {
                    AprilsLegacy.PLAYER_UNLOCK.getOptionalValue(id).ifPresent(unlock -> {
                        RegistryEntry<PlayerUnlock> entry = AprilsLegacy.PLAYER_UNLOCK.getEntry(unlock);
                        if (entry != null) {
                            this.unlocks.add(entry);
                        }
                    });
                }
            }
        }
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putInt(NBT_CURRENCY, this.currency);

        NbtList list = new NbtList();
        for (RegistryEntry<PlayerUnlock> unlock : this.unlocks) {
            unlock.getKey().ifPresent(key ->
                list.add(NbtString.of(key.getValue().toString()))
            );
        }
        nbt.put(NBT_UNLOCKS, list);
    }

    /**
     * Apply all onMineEnter callbacks for this player's unlocks.
     * Called when the player enters a mine dimension.
     */
    public void applyMineEnter(ServerPlayerEntity player) {
        for (RegistryEntry<PlayerUnlock> unlock : unlocks) {
            unlock.value().onMineEnter().accept(player);
        }
    }
}
