package net.zhengzhengyiyi.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public final record class_8484(float speed) implements CustomPayload {
    public static final Identifier PACKET_ID = Identifier.of("aprils_legacy", "vote_speed_update");
    public static final CustomPayload.Id<class_8484> PAYLOAD_ID = new CustomPayload.Id<>(PACKET_ID);
//    public static final PacketType<class_8484> TYPE = new PacketType<>(NetworkSide.SERVERBOUND, PACKET_ID);

    public class_8484(PacketByteBuf buf) {
        this(buf.readFloat());
    }

    public void write(PacketByteBuf buf) {
        buf.writeFloat(this.speed);
    }

    public void apply(ServerPlayPacketListener listener) {
        ((VoteClientPlayPacketListener) listener).method_50045(this);
    }

//    @Override
//    public PacketType<class_8484> getPacketType() {
//        return TYPE;
//    }

    @Override
    public Id<class_8484> getId() {
        return PAYLOAD_ID;
    }
}