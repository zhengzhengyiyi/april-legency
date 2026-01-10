package net.zhengzhengyiyi.vote;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.Uuids;

/**
 * Represents the persistent state of the vote manager, used for saving and loading from disk.
 * <p>
 * Official Name: bgt
 * Intermediary Name: net.minecraft.class_8381
 */
public record VoteState(
    List<VoteValue> activeValues,
    Map<UUID, VoteDefinition> activeVotes,
    VoteStatistics statistics,
    int totalProposalsCount
) {
    /**
     * Codec for serializing and deserializing the entire vote system state.
     */
	public static final Codec<VoteState> CODEC = RecordCodecBuilder.create(instance -> 
	    instance.group(
	        VoteValue.CODEC.listOf().fieldOf("approved").forGetter(VoteState::activeValues),
	        Codec.unboundedMap(Uuids.STRING_CODEC, VoteDefinition.CODEC).fieldOf("pending").forGetter(VoteState::activeVotes),
	        VoteStatistics.CODEC.fieldOf("votes").forGetter(VoteState::statistics),
	        Codec.INT.fieldOf("total_proposal_count").forGetter(VoteState::totalProposalsCount)
	    ).apply(instance, VoteState::new)
	);
//    public static final Codec<VoteState> CODEC = RecordCodecBuilder.create(instance -> 
//        instance.group(
//            VoteValue.CODEC.listOf().fieldOf("approved").forGetter(VoteState::activeValues),
//            Codec.unboundedMap(Uuids.INT_STREAM_CODEC, VoteDefinition.CODEC).fieldOf("pending").forGetter(VoteState::activeVotes),
//            VoteStatistics.CODEC.fieldOf("votes").forGetter(VoteState::statistics),
//            Codec.INT.fieldOf("total_proposal_count").forGetter(VoteState::totalProposalsCount)
//        ).apply(instance, VoteState::new)
//    );

    /**
     * Creates an empty state with no active votes or changes.
     */
    public VoteState() {
        this(List.of(), Map.of(), new VoteStatistics(Map.of()), 0);
    }

    /**
     * Factory method to create a state from active world rules and tracker data.
     *
     * @param activeRules Stream of currently applied world rules.
     * @param activeVotes Map of ongoing votes.
     * @param statistics Persistent statistics tracker.
     * @param totalCount Total number of proposals created.
     * @return A new VoteState instance.
     */
    public static VoteState create(Stream<Object> activeRules, Map<UUID, VoteDefinition> activeVotes, VoteStatistics statistics, int totalCount) {
        // In the original bytecode, this filters and collects values from the world's registry
        List<VoteValue> values = activeRules
        	.filter(rule -> rule instanceof Provider)
            .flatMap(rule -> ((Provider) rule).getValues())
            .collect(Collectors.toList());

        return new VoteState(values, activeVotes, statistics, totalCount);
    }
}
