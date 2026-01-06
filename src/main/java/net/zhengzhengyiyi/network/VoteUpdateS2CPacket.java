package net.zhengzhengyiyi.network;

import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.vote.VoteDefinition;
import net.zhengzhengyiyi.vote.VoteOptionId;

public record VoteUpdateS2CPacket(
    boolean clear,
    Map<UUID, VoteDefinition> votes, 
    Map<VoteOptionId, VoterData> voters
) implements CustomPayload {
	public static final Identifier PACKET_ID = Identifier.of("aprils_legacy", "vote_update");
    public static final CustomPayload.Id<VoteUpdateS2CPacket> PAYLOAD_ID = new CustomPayload.Id<>(PACKET_ID);
//    public static final PacketType<VoteUpdateS2CPacket> TYPE = new PacketType<>(NetworkSide.CLIENTBOUND, PACKET_ID);
    
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

    public void apply(ClientPlayPacketListener listener) {
        ((VoteClientPlayPacketListener)(Object)listener).onVoteUpdate(this);
    }

//	@Override
//	public PacketType<? extends Packet<ClientPlayPacketListener>> getPacketType() {
//		return TYPE;
//	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return PAYLOAD_ID;
	}
}