package net.zhengzhengyiyi.network;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.rules.VoteRules;
import net.zhengzhengyiyi.vote.VoteDefinition;
import net.zhengzhengyiyi.vote.VoteMetadata;
import net.zhengzhengyiyi.vote.VoteOptionId;

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
    	try {
	        if (buf instanceof RegistryByteBuf registryByteBuf) {
	            return buf.decode(registryByteBuf.getRegistryManager().getOps(NbtOps.INSTANCE), VoteDefinition.CODEC);
	        }
	        return buf.decode(NbtOps.INSTANCE, VoteDefinition.CODEC);
        } catch (Exception e) {
        	
        }
    	return new VoteDefinition(
    		    new VoteMetadata(VoteDefinition.NOTHING_RULE_TEXT, 0, 1L, List.of()),
    		    (Map<VoteOptionId, VoteDefinition.Option>) VoteRules.TEST_RULE.getActiveOptions()
    		        .collect(Collectors.toMap(
    		            value -> new VoteOptionId(UUID.randomUUID(), 0),
    		            value -> new VoteDefinition.Option(Text.of("test"), List.of())
    		        ))
    		);
    }

    @SuppressWarnings("deprecation")
    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.id);
        try {
        if (buf instanceof RegistryByteBuf registryByteBuf) {
            buf.encode(registryByteBuf.getRegistryManager().getOps(NbtOps.INSTANCE), VoteDefinition.CODEC, this.voteData);
        } else {
            buf.encode(NbtOps.INSTANCE, VoteDefinition.CODEC, this.voteData);
        }} catch (Exception e){}
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