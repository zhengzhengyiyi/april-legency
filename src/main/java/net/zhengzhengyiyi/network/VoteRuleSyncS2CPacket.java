package net.zhengzhengyiyi.network;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.vote.VoteRegistries;
import net.zhengzhengyiyi.vote.VoteValue;
import net.zhengzhengyiyi.vote.VoterAction;
import net.zhengzhengyiyi.world.Vote;

import java.util.List;

public record VoteRuleSyncS2CPacket(
    boolean resetAll, 
    VoterAction action, 
    List<VoteValue> rules
) implements Packet<ClientPlayPacketListener>, CustomPayload {

    public static final Identifier PACKET_ID = Identifier.of("aprils_legacy", "vote_rule_sync");
    public static final CustomPayload.Id<VoteRuleSyncS2CPacket> PAYLOAD_ID = new CustomPayload.Id<>(PACKET_ID);
    public static final PacketType<VoteRuleSyncS2CPacket> TYPE = new PacketType<>(NetworkSide.CLIENTBOUND, PACKET_ID);

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
        Vote type = VoteRegistries.VOTE_RULE_TYPE.get(buf.readVarInt());
        return buf.decodeAsJson(type.getOptionCodec());
    }

    private static void writeVoteValue(PacketByteBuf buf, VoteValue value) {
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
    public PacketType<VoteRuleSyncS2CPacket> getPacketType() {
        return TYPE;
    }

    @Override
    public Id<VoteRuleSyncS2CPacket> getId() {
        return PAYLOAD_ID;
    }
}
