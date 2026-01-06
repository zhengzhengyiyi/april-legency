package net.zhengzhengyiyi.network;

import java.util.UUID;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.Identifier;

public final record class_8481(UUID id) implements Packet<ClientPlayPacketListener>, CustomPayload {
    
    public static final Identifier PACKET_ID = Identifier.of("aprils_legacy", "vote_end");
    public static final CustomPayload.Id<class_8481> PAYLOAD_ID = new CustomPayload.Id<>(PACKET_ID);
    public static final PacketType<class_8481> TYPE = new PacketType<>(NetworkSide.CLIENTBOUND, PACKET_ID);

    public class_8481(PacketByteBuf packetByteBuf) {
        this(packetByteBuf.readUuid());
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.id);
    }

    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        ((VoteClientPlayPacketListener)clientPlayPacketListener).method_51013(this);
    }

    @Override
    public PacketType<class_8481> getPacketType() {
        return TYPE;
    }

    @Override
    public Id<class_8481> getId() {
        return PAYLOAD_ID;
    }
}
