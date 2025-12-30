package net.zhengzhengyiyi.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.vote.VoterAction;
import net.zhengzhengyiyi.vote.VoteRegistries;
import net.zhengzhengyiyi.world.Vote;
import net.zhengzhengyiyi.vote.VoteValue;

import java.util.List;

/**
 * S2C Packet used to sync active vote rules/effects to the client.
 * <p>
 * Official Name: xi
 * Intermediary Name: net.minecraft.class_8361
 */
public record VoteRuleSyncS2CPacket(
    boolean clearExisting, 
    VoterAction action, 
    List<VoteValue> values
) implements CustomPayload {
	public static final Identifier identifier = Identifier.of("minecraft", "vote_rule_sync");
	
	public static final CustomPayload.Id<VoteRuleSyncS2CPacket> ID = new CustomPayload.Id<VoteRuleSyncS2CPacket>(identifier);

    /**
     */
    public VoteRuleSyncS2CPacket {
    }
    
    public static final PacketCodec<RegistryByteBuf, VoteRuleSyncS2CPacket> CODEC = PacketCodec.unit(
        new VoteRuleSyncS2CPacket(true, VoterAction.APPROVE, List.of())
    );

    /**
     * Write method to PacketByteBuf (sf).
     */
//    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBoolean(this.clearExisting);
        buf.writeEnumConstant(this.action);
        buf.writeCollection(this.values, VoteRuleSyncS2CPacket::writeVoteValue);
    }

    /**
     * Internal helper to read a dispatched VoteValue.
     */
    public static VoteValue readVoteValue(PacketByteBuf buf) {
        // ja.ao refers to Registries.VOTE_RULE_TYPE
        Vote type = (Vote) buf.readRegistryKey(VoteRegistries.VOTE_RULE_TYPE_KEY);
        // rc.a refers to NbtOps.INSTANCE or a specific Packet Codec context
        return buf.decodeAsJson(type.getOptionCodec());
    }

    /**
     * Internal helper to write a dispatched VoteValue.
     */
    private static void writeVoteValue(PacketByteBuf buf, VoteValue value) {
//        buf.writeRegistryValue(((VoteRegistries)Registries).VOTE_RULE_TYPE, value.getType());
//    	buf.writeVarInt(VoteRegistries.VOTE_RULE_TYPE.getRawId(value.getType()));
    	buf.writeVarInt(VoteRegistries.VOTE_RULE_TYPE.getRawId(value.getType()));

        buf.encodeAsJson(value.getType().getOptionCodec(), value);
    }

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
