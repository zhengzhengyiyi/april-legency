package net.zhengzhengyiyi.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.unlock.PlayerUnlock;

import java.util.List;

/**
 * Mirrors Craftmine ClientPacket4 — S2C packet that syncs the player's
 * current unlock list and currency balance to the client.
 * Sent after a purchase or on login.
 */
public record ClientPacket4(List<RegistryEntry<PlayerUnlock>> unlocks, int currency) implements CustomPayload {

    public static final CustomPayload.Id<ClientPacket4> PAYLOAD_ID =
        new CustomPayload.Id<>(Identifier.of("zhengzhengyiyi", "unlock_sync"));

    public static final PacketCodec<RegistryByteBuf, ClientPacket4> CODEC = PacketCodec.tuple(
        PacketCodecs.codec(PlayerUnlock.CODEC).collect(PacketCodecs.toList()), ClientPacket4::unlocks,
        PacketCodecs.VAR_INT, ClientPacket4::currency,
        ClientPacket4::new
    );

    @Override
    public Id<ClientPacket4> getId() {
        return PAYLOAD_ID;
    }
}
