package net.zhengzhengyiyi.vote;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Uuids;

import java.util.Comparator;
import java.util.UUID;

/**
 * Modern implementation of the vote casting packet.
 * <p>
 * Original Mappings:
 * <ul>
 * <li>Class: class_8373 (Official: bgm)</li>
 * <li>Field f (Official: f): voteId (UUID)</li>
 * <li>Field g (Official: g): optionIndex (int)</li>
 * <li>Method a(): voteId()</li>
 * <li>Method b(): optionIndex()</li>
 * </ul>
 */
public record VoteC2SPacket(UUID voteId, int optionIndex) {

    public static final Comparator<VoteC2SPacket> COMPARATOR = Comparator
            .comparing(VoteC2SPacket::voteId)
            .thenComparingInt(VoteC2SPacket::optionIndex);

    public static final PacketCodec<RegistryByteBuf, VoteC2SPacket> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, VoteC2SPacket::voteId,
            PacketCodecs.VAR_INT, VoteC2SPacket::optionIndex,
            VoteC2SPacket::new
    );

    public static final Codec<VoteC2SPacket> RECORD_CODEC = RecordCodecBuilder.create(instance -> 
        instance.group(
            Uuids.INT_STREAM_CODEC.fieldOf("uuid").forGetter(VoteC2SPacket::voteId),
            Codec.INT.fieldOf("index").forGetter(VoteC2SPacket::optionIndex)
        ).apply(instance, VoteC2SPacket::new)
    );

    public static final Codec<VoteC2SPacket> STRING_CODEC = Codec.STRING.comapFlatMap(s -> {
        int divider = s.indexOf(':');
        if (divider == -1) {
            return DataResult.error(() -> "Invalid format, expected <uuid>:<index>");
        }
        try {
            UUID uuid = UUID.fromString(s.substring(0, divider));
            int index = Integer.parseInt(s.substring(divider + 1));
            return DataResult.success(new VoteC2SPacket(uuid, index));
        } catch (Exception e) {
            return DataResult.error(() -> "Error parsing packet: " + e.getMessage());
        }
    }, packet -> packet.voteId + ":" + packet.optionIndex);

    /**
     * @return Original field f (UUID)
     */
    public UUID a() {
        return voteId();
    }

    /**
     * @return Original field g (int)
     */
    public int b() {
        return optionIndex();
    }

    @Override
    public String toString() {
        return this.voteId + ":" + this.optionIndex;
    }
}
