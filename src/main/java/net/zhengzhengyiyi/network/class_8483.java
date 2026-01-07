package net.zhengzhengyiyi.network;

import java.util.UUID;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.vote.VoteDefinition;

public final record class_8483(UUID id, VoteDefinition voteData) implements CustomPayload {
    public static final Identifier PACKET_ID = Identifier.of("aprils_legacy", "vote_start");
    public static final CustomPayload.Id<class_8483> PAYLOAD_ID = new CustomPayload.Id<>(PACKET_ID);
//    public static final PacketType<class_8483> TYPE = new PacketType<>(NetworkSide.CLIENTBOUND, PACKET_ID);

    public class_8483 {}

    @SuppressWarnings("deprecation")
	public class_8483(PacketByteBuf packetByteBuf) {
        this(packetByteBuf.readUuid(), packetByteBuf.decode(NbtOps.INSTANCE, VoteDefinition.CODEC));
//    	this(packetByteBuf.readUuid(), packetByteBuf.decodeAsJson(VoteDefinition.CODEC));
    }

    @SuppressWarnings("deprecation")
	public void write(PacketByteBuf buf) {
        buf.writeUuid(this.id);
        buf.encode(NbtOps.INSTANCE, VoteDefinition.CODEC, this.voteData);
//        buf.encodeAsJson(VoteDefinition.CODEC, voteData());
    }

    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        ((VoteClientPlayPacketListener) clientPlayPacketListener).onVoteStart(this);
    }

//    @Override
//    public PacketType<class_8483> getPacketType() {
//        return TYPE;
//    }

    @Override
    public Id<class_8483> getId() {
        return PAYLOAD_ID;
    }
}
