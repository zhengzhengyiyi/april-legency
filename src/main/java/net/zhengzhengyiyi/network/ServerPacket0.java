package net.zhengzhengyiyi.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.unlock.PlayerUnlock;
import net.zhengzhengyiyi.unlock.PlayerUnlockState;

/**
 * Mirrors Craftmine ServerPacket0 — C2S packet sent when a player purchases an unlock.
 * The server validates the purchase, deducts currency, and applies the unlock.
 */
public record ServerPacket0(RegistryEntry<PlayerUnlock> unlock) implements CustomPayload {

    public static final CustomPayload.Id<ServerPacket0> PAYLOAD_ID =
        new CustomPayload.Id<>(Identifier.of("zhengzhengyiyi", "buy_unlock"));

    public static final PacketCodec<RegistryByteBuf, ServerPacket0> CODEC = PacketCodec.tuple(
        PacketCodecs.codec(PlayerUnlock.CODEC), ServerPacket0::unlock, ServerPacket0::new
    );

    @Override
    public Id<ServerPacket0> getId() {
        return PAYLOAD_ID;
    }

    /**
     * Server-side handler: validate and apply the unlock purchase.
     */
    public static void handle(ServerPacket0 packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        RegistryEntry<PlayerUnlock> unlockEntry = packet.unlock();
        PlayerUnlock unlock = unlockEntry.value();

        // Run on server thread
        context.server().execute(() -> {
            PlayerUnlockState state = PlayerUnlockState.get(player);

            // Check player has enough currency
            int price = unlock.unlockPrice();
            if (state.getCurrency() < price) {
                return;
            }

            // Check not already unlocked
            if (state.hasUnlock(unlockEntry)) {
                return;
            }

            // Check parent is unlocked (if any)
            if (unlock.parent().isPresent() && !state.hasUnlock(unlock.parent().get())) {
                return;
            }

            // Deduct currency and apply unlock
            state.removeCurrency(price);
            state.addUnlock(unlockEntry);

            // Fire activation callback (gives items, applies attributes, etc.)
            unlock.activation().accept(player);

            // Play success sound
            player.getEntityWorld().playSound(null, player.getBlockPos(),
                SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 0.5F, 1.0F);

            // Sync unlock state back to client
            ServerPlayNetworking.send(player, new ClientPacket4(state.getUnlocks(), state.getCurrency()));
        });
    }
}
