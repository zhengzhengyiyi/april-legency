package net.zhengzhengyiyi.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.zhengzhengyiyi.vote.VoteOptionId;

public final record class_8258(int transactionId, VoteOptionId optionId) implements Packet<ServerPlayPacketListener> {
    public class_8258(PacketByteBuf buf) {
        this(buf.readVarInt(), buf.decodeAsJson(VoteOptionId.CODEC));
    }

    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.transactionId);
//        buf.encode(VoteOptionId.CODEC, this.optionId);
        buf.encodeAsJson(VoteOptionId.CODEC, this.optionId);
    }

    @Override
    public void apply(ServerPlayPacketListener listener) {
//        ((VoteClientPlayPacketListener) listener).method_50043(null);
    	((VoteClientPlayPacketListener) listener).method_50043(this);
    }

	@Override
	public PacketType<? extends Packet<ServerPlayPacketListener>> getPacketType() {
		return null;
	}
}
