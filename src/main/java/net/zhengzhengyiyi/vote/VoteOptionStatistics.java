package net.zhengzhengyiyi.vote;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Uuids;

/**
 * Statistics for a single voting option, tracking each player's individual choice.
 * <p>
 * Official Name: bgo
 * Intermediary Name: net.minecraft.class_8375
 */
public record VoteOptionStatistics(Map<UUID, VoteChoice> voters) {

    /**
     * An empty statistics instance with no voters.
     */
    public static final VoteOptionStatistics EMPTY = new VoteOptionStatistics(Map.of());

    /**
     * Codec for serializing and deserializing voter statistics.
     */
    public static final Codec<VoteOptionStatistics> CODEC = RecordCodecBuilder.create(instance -> 
        instance.group(
            Codec.unboundedMap(Uuids.INT_STREAM_CODEC, VoteChoice.CODEC)
                .fieldOf("voters")
                .forGetter(VoteOptionStatistics::voters)
        ).apply(instance, VoteOptionStatistics::new)
    );

    /**
     * Reads a {@link VoteOptionStatistics} from a {@link PacketByteBuf}.
     */
    public static VoteOptionStatistics read(PacketByteBuf buf) {
        return new VoteOptionStatistics(buf.readMap((a)->PacketByteBuf.readUuid(a), VoteChoice::read));
    }

    /**
     * Writes a {@link VoteOptionStatistics} to a {@link PacketByteBuf}.
     */
    public static void write(PacketByteBuf buf, VoteOptionStatistics stats) {
        buf.writeMap(stats.voters, (a, b)->a.writeUuid(b), VoteChoice::write);
    }

    /**
     * Returns the total number of unique players who voted for this option.
     * @return The voter count.
     */
    public int getVoterCount() {
        return this.voters.size();
    }

    /**
     * Calculates the total score (weight) for this option.
     * @return The sum of all vote weights.
     */
    public int getScore() {
        return this.voters.values().stream().mapToInt(VoteChoice::voteCount).sum();
    }

    /**
     * Creates a new statistics instance for a single voter.
     * @param uuid The player's UUID.
     * @param choice The player's choice.
     * @return A new statistics instance.
     */
    public static VoteOptionStatistics of(UUID uuid, VoteChoice choice) {
        return new VoteOptionStatistics(Map.of(uuid, choice));
    }
}
