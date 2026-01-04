package net.zhengzhengyiyi.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public final record class_8484(float speed) implements Packet<ServerPlayPacketListener> {

    public class_8484(PacketByteBuf buf) {
        this(buf.readFloat());
    }

    public void write(PacketByteBuf buf) {
        buf.writeFloat(this.speed);
    }

    @Override
    public void apply(ServerPlayPacketListener listener) {
        ((VoteClientPlayPacketListener) listener).method_50045(this);
    }

	@Override
	public PacketType<? extends Packet<ServerPlayPacketListener>> getPacketType() {
		return null;
	}
}
