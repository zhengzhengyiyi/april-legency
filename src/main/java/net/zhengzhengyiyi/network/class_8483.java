//package net.zhengzhengyiyi.network;
//
//import java.util.UUID;
//import net.minecraft.nbt.NbtOps;
//import net.minecraft.network.PacketByteBuf;
//import net.minecraft.network.listener.ClientPlayPacketListener;
//import net.minecraft.network.packet.CustomPayload;
//import net.minecraft.util.Identifier;
//import net.zhengzhengyiyi.vote.VoteDefinition;
//
//public final record class_8483(UUID id, VoteDefinition voteData) implements CustomPayload {
//    public static final Identifier PACKET_ID = Identifier.of("aprils_legacy", "vote_start");
//    public static final CustomPayload.Id<class_8483> PAYLOAD_ID = new CustomPayload.Id<>(PACKET_ID);
//    
////    public static final PacketType<class_8483> TYPE = new PacketType<>(NetworkSide.CLIENTBOUND, PACKET_ID);
//
//    public class_8483 {}
//
//    @SuppressWarnings("deprecation")
//	public class_8483(PacketByteBuf packetByteBuf) {
//        this(packetByteBuf.readUuid(), packetByteBuf.decode(NbtOps.INSTANCE, VoteDefinition.CODEC));
//    }
//
//    @SuppressWarnings("deprecation")
//	public void write(PacketByteBuf buf) {
//        buf.writeUuid(this.id);
//        buf.encode(NbtOps.INSTANCE, VoteDefinition.CODEC, this.voteData);
//    }
//
//    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
//        ((VoteClientPlayPacketListener) clientPlayPacketListener).onVoteStart(this);
//    }
//
//    @Override
//    public Id<class_8483> getId() {
//        return PAYLOAD_ID;
//    }
//}

package net.zhengzhengyiyi.network;

import java.util.UUID;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.vote.VoteDefinition;

public final record class_8483(UUID id, VoteDefinition voteData) implements CustomPayload {
    public static final Identifier PACKET_ID = Identifier.of("aprils_legacy", "vote_start");
    public static final CustomPayload.Id<class_8483> PAYLOAD_ID = new CustomPayload.Id<>(PACKET_ID);

    public class_8483(UUID id, VoteDefinition voteData) {
        this.id = id;
        this.voteData = voteData;
    }

    public class_8483(PacketByteBuf packetByteBuf) {
        this(packetByteBuf.readUuid(), decodeVoteDefinition(packetByteBuf));
    }

    @SuppressWarnings("deprecation")
	private static VoteDefinition decodeVoteDefinition(PacketByteBuf buf) {
        if (buf instanceof RegistryByteBuf registryByteBuf) {
            return buf.decode(registryByteBuf.getRegistryManager().getOps(NbtOps.INSTANCE), VoteDefinition.CODEC);
        }
        return buf.decode(NbtOps.INSTANCE, VoteDefinition.CODEC);
    }

    @SuppressWarnings("deprecation")
    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.id);
        if (buf instanceof RegistryByteBuf registryByteBuf) {
            buf.encode(registryByteBuf.getRegistryManager().getOps(NbtOps.INSTANCE), VoteDefinition.CODEC, this.voteData);
        } else {
            buf.encode(NbtOps.INSTANCE, VoteDefinition.CODEC, this.voteData);
        }
    }

    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        if (clientPlayPacketListener instanceof VoteClientPlayPacketListener listener) {
            listener.onVoteStart(this);
        }
    }

    @Override
    public Id<class_8483> getId() {
        return PAYLOAD_ID;
    }
}