package net.zhengzhengyiyi.network;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.vote.VoteOptionId;

public final record class_8482(VoteOptionId id, VoterData voters) implements Packet<ClientPlayPacketListener>, CustomPayload {

    public static final Identifier PACKET_ID = Identifier.of("aprils_legacy", "vote_sync_voters");
    public static final CustomPayload.Id<class_8482> PAYLOAD_ID = new CustomPayload.Id<>(PACKET_ID);
    public static final PacketType<class_8482> TYPE = new PacketType<>(NetworkSide.CLIENTBOUND, PACKET_ID);

    public class_8482(PacketByteBuf packetByteBuf) {
        this(VoteOptionId.read(packetByteBuf), VoterData.read(packetByteBuf));
    }

    public void write(PacketByteBuf buf) {
        VoteOptionId.write(buf, this.id);
        voters.write(buf);
    }

    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        ((VoteClientPlayPacketListener) clientPlayPacketListener).method_51014(this);
    }

    @Override
    public PacketType<class_8482> getPacketType() {
        return TYPE;
    }

    @Override
    public Id<class_8482> getId() {
        return PAYLOAD_ID;
    }
}
