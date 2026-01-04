package net.zhengzhengyiyi.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.zhengzhengyiyi.vote.VoteRegistries;
import net.zhengzhengyiyi.vote.VoteValue;
import net.zhengzhengyiyi.vote.VoterAction;
import net.zhengzhengyiyi.world.Vote;

import java.util.List;

public record VoteRuleSyncS2CPacket(
    boolean resetAll, 
    VoterAction action, 
    List<VoteValue> rules
) implements Packet<ClientPlayPacketListener> {

    public VoteRuleSyncS2CPacket(PacketByteBuf buf) {
        this(
            buf.readBoolean(), 
            buf.readEnumConstant(VoterAction.class), 
            buf.readList(VoteRuleSyncS2CPacket::readVoteValue)
        );
    }

    public void write(PacketByteBuf buf) {
        buf.writeBoolean(this.resetAll);
        buf.writeEnumConstant(this.action);
        buf.writeCollection(this.rules, VoteRuleSyncS2CPacket::writeVoteValue);
    }
    
    public static VoteValue readVoteValue(PacketByteBuf buf) {
        // ja.ao refers to Registries.VOTE_RULE_TYPE
        Vote type = (Vote) buf.readRegistryKey(VoteRegistries.VOTE_RULE_TYPE_KEY);
        // rc.a refers to NbtOps.INSTANCE or a specific Packet Codec context
        return buf.decodeAsJson(type.getOptionCodec());
    }

    private static void writeVoteValue(PacketByteBuf buf, VoteValue value) {
	//      buf.writeRegistryValue(((VoteRegistries)Registries).VOTE_RULE_TYPE, value.getType());
	//  	buf.writeVarInt(VoteRegistries.VOTE_RULE_TYPE.getRawId(value.getType()));
	  	buf.writeVarInt(VoteRegistries.VOTE_RULE_TYPE.getRawId(value.getType()));
	
	      buf.encodeAsJson(value.getType().getOptionCodec(), value);
	  }

    @Override
    public void apply(ClientPlayPacketListener listener) {
        if (listener instanceof VoteClientPlayPacketListener voteListener) {
            voteListener.onVoteRuleSync(this);
        }
    }

	@Override
	public PacketType<? extends Packet<ClientPlayPacketListener>> getPacketType() {
		return null;
	}
}
