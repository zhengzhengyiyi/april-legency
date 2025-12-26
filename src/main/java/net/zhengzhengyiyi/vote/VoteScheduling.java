package net.zhengzhengyiyi.vote;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

/**
 * Defines the timing and cost metadata for a vote.
 * This class handles when a vote starts, how long it lasts, and its requirements.
 * <p>
 * Official Name: bgk
 * Intermediary Name: net.minecraft.class_8372
 */
public record VoteScheduling(
    Text displayName,
    long startTime,
    long duration,
    List<VoteCost> cost
) {
    /**
     * MapCodec for serializing vote scheduling data within a VoteDefinition.
     */
    public static final MapCodec<VoteScheduling> CODEC = RecordCodecBuilder.mapCodec(instance -> 
        instance.group(
            TextCodecs.CODEC.fieldOf("display_name").forGetter(VoteScheduling::displayName),
            Codec.LONG.fieldOf("start").forGetter(VoteScheduling::startTime),
            Codec.LONG.fieldOf("duration").forGetter(VoteScheduling::duration),
            VoteCost.CODEC.codec().listOf().fieldOf("cost").forGetter(VoteScheduling::cost)
        ).apply(instance, VoteScheduling::new)
    );

    /**
     * Calculates the game time when the vote is scheduled to end.
     * @return The end timestamp (start + duration).
     */
    public long getEndTime() {
        return this.startTime + this.duration;
    }
}
