package net.zhengzhengyiyi.network;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
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
) implements Packet<ClientPlayPacketListener> {
	public static final PacketType<VoteRuleSyncS2CPacket> TYPE = new PacketType(NetworkSide.CLIENTBOUND, Identifier.of("minecraft", "vote_rule_sync"));

    /**
     */
    public VoteRuleSyncS2CPacket {
    }

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
    private static VoteValue readVoteValue(PacketByteBuf buf) {
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
    public void apply(ClientPlayPacketListener listener) {
        // In your mixin or client-side handler, you should implement this:
        // ((VotePacketHandler)listener).onVoteRuleSync(this);
    }

	@Override
	public PacketType<? extends Packet<ClientPlayPacketListener>> getPacketType() {
		return TYPE;
	}
}
