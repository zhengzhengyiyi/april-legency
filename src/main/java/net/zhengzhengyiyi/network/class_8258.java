package net.zhengzhengyiyi.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.vote.VoteOptionId;

public final record class_8258(int transactionId, VoteOptionId optionId) implements CustomPayload {
    public static final Identifier PACKET_ID = Identifier.of("aprils_legacy", "vote_cast");
    public static final CustomPayload.Id<class_8258> PAYLOAD_ID = new CustomPayload.Id<>(PACKET_ID);
//    public static final PacketType<class_8258> TYPE = new PacketType<>(NetworkSide.SERVERBOUND, PACKET_ID);

    public class_8258(PacketByteBuf buf) {
        this(buf.readVarInt(), buf.decodeAsJson(VoteOptionId.CODEC));
    }

    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.transactionId);
        buf.encodeAsJson(VoteOptionId.CODEC, this.optionId);
    }

    public void apply(ServerPlayPacketListener listener) {
        ((VoteClientPlayPacketListener) listener).method_50043(this);
    }
//    public PacketType<class_8258> getPacketType() {
//        return TYPE;
//    }

    @Override
    public Id<class_8258> getId() {
        return PAYLOAD_ID;
    }
}
