package net.zhengzhengyiyi.network;

import java.util.UUID;
import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.vote.VoteDefinition;

public final record class_8483(UUID id, VoteDefinition voteData) implements Packet<ClientPlayPacketListener>, CustomPayload {
    public static final Identifier PACKET_ID = Identifier.of("aprils_legacy", "vote_start");
    public static final CustomPayload.Id<class_8483> PAYLOAD_ID = new CustomPayload.Id<>(PACKET_ID);
    public static final PacketType<class_8483> TYPE = new PacketType<>(NetworkSide.CLIENTBOUND, PACKET_ID);

    public class_8483 {}

    @SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
    public class_8483(PacketByteBuf packetByteBuf) {
        this(packetByteBuf.readUuid(), (VoteDefinition) packetByteBuf.decode((DynamicOps) NbtOps.INSTANCE, VoteDefinition.CODEC));
    }

    @SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.id);
        buf.encode((DynamicOps) NbtOps.INSTANCE, VoteDefinition.CODEC, this.voteData);
    }

    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        ((VoteClientPlayPacketListener) clientPlayPacketListener).method_51015(this);
    }

    @Override
    public PacketType<class_8483> getPacketType() {
        return TYPE;
    }

    @Override
    public Id<class_8483> getId() {
        return PAYLOAD_ID;
    }
}
