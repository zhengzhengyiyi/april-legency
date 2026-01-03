package net.zhengzhengyiyi.network;

import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.zhengzhengyiyi.vote.VoteDefinition;
import net.zhengzhengyiyi.vote.VoteOptionId;

public record VoteUpdateS2CPacket(
    boolean clear,
    Map<UUID, VoteDefinition> votes, 
    Map<VoteOptionId, VoterData> voters
) implements Packet<ClientPlayPacketListener> {
    @SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	public VoteUpdateS2CPacket(PacketByteBuf buf) {
        this(
            buf.readBoolean(),
            buf.readMap(
                b -> b.readUuid(),
                packetByteBuf -> packetByteBuf.decode((DynamicOps) NbtOps.INSTANCE, VoteDefinition.CODEC)
            ),
            buf.readMap(VoteOptionId::read, VoterData::read)
        );
    }

    @SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
	public void write(PacketByteBuf buf) {
        buf.writeBoolean(this.clear);
        
        buf.writeMap(
            this.votes, 
            (a, b) -> a.writeUuid(b),
            (packetByteBuf, value) -> packetByteBuf.encode((DynamicOps) NbtOps.INSTANCE, VoteDefinition.CODEC, value)
        );

//        buf.writeMap(this.voters, VoteOptionId::read, VoterData::write);
        buf.writeMap(
            this.voters, 
            (b, id) -> VoteOptionId.write(b, id),
            (b, data) -> data.write(b)
        );
    }

    @Override
    public void apply(ClientPlayPacketListener listener) {
        ((VoteClientPlayPacketListener)(Object)listener).onVoteUpdate(this);
    }

	@Override
	public PacketType<? extends Packet<ClientPlayPacketListener>> getPacketType() {
		return null;
	}
}