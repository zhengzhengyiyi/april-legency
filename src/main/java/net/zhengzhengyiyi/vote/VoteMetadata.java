package net.zhengzhengyiyi.vote;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

/**
 * Metadata for a voting instance, defining timing, naming, and participation costs.
 * <p>
 * Official Name: bgk
 */
public final class VoteMetadata {
    private final Text displayName;
    private final long startTime;
    private final long duration;
    private final List<VoteCost> cost;

    /**
     * MapCodec for serializing and deserializing vote metadata.
     */
    @SuppressWarnings("unchecked")
	public static final MapCodec<VoteMetadata> CODEC = RecordCodecBuilder.mapCodec(instance -> 
        instance.group(
//            Text.Serializer.CODEC.fieldOf("display_name").forGetter(VoteMetadata::getDisplayName),
        	TextCodecs.CODEC.fieldOf("display_name").forGetter(VoteMetadata::getDisplayName),
            Codec.LONG.fieldOf("start").forGetter(VoteMetadata::getStartTime),
            Codec.LONG.fieldOf("duration").forGetter(VoteMetadata::getDuration),
            VoteCost.CODEC.codec().listOf().fieldOf("cost").forGetter(VoteMetadata::getCost)
        ).apply((Applicative)instance, VoteMetadata::new)
    );

    /**
     * Constructs a new VoteMetadata.
     *
     * @param displayName The name of the vote shown in the UI.
     * @param startTime   The tick at which the vote begins.
     * @param duration    The total number of ticks the vote lasts.
     * @param cost        A list of costs (XP/Items) required to cast a vote.
     */
    public VoteMetadata(Text displayName, long startTime, long duration, List<VoteCost> cost) {
        this.displayName = displayName;
        this.startTime = startTime;
        this.duration = duration;
        this.cost = cost;
    }

    /**
     * Gets the display name of the vote.
     */
    public Text getDisplayName() {
        return this.displayName;
    }

    /**
     * Gets the starting world tick of the vote.
     */
    public long getStartTime() {
        return this.startTime;
    }

    /**
     * Gets the duration of the vote in ticks.
     */
    public long getDuration() {
        return this.duration;
    }

    /**
     * Gets the list of costs required to participate in this vote.
     */
    public List<VoteCost> getCost() {
        return this.cost;
    }

    /**
     * Calculates the absolute ending tick of the vote.
     *
     * @return The tick when the vote should conclude (start + duration).
     */
    public long getEndTime() {
        return this.startTime + this.duration;
    }

    @Override
    public String toString() { return String.format("VoteMetadata[name=%s, end=%d]", displayName.getString(), getEndTime()); }
    
    @Override
    public int hashCode() { return java.util.Objects.hash(displayName, startTime, duration, cost); }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof VoteMetadata other)) return false;
        return startTime == other.startTime && duration == other.duration && 
               java.util.Objects.equals(displayName, other.displayName) && 
               java.util.Objects.equals(cost, other.cost);
    }
}