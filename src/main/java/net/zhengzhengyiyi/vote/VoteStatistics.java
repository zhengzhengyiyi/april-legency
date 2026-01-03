package net.zhengzhengyiyi.vote;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A persistent snapshot of all votes, mapping option IDs to their respective statistics.
 * <p>
 * Official Name: bgn
 * Intermediary Name: net.minecraft.class_8374
 */
public record VoteStatistics(Map<VoteOptionId, VoteOptionStatistics> options) {
	public VoteResults.ChoiceSummary totalVotes() {
	    Map<UUID, VoteChoice> totalVoterMap = new HashMap<>();
	    
	    for (VoteOptionStatistics optionStats : this.options.values()) {
	        optionStats.voters().forEach((uuid, choice) -> {
	            totalVoterMap.compute(uuid, (id, existing) -> 
	                VoteChoice.accumulate(existing, choice.displayName(), choice.voteCount())
	            );
	        });
	    }
	    
	    return new VoteResults.ChoiceSummary(new VoteOptionStatistics(totalVoterMap));
	}

    /**
     * Codec for serializing all voting statistics.
     * Corresponds to bgn.a in the bytecode.
     */
    public static final Codec<VoteStatistics> CODEC = RecordCodecBuilder.create(instance -> 
        instance.group(
            Codec.unboundedMap(VoteOptionId.STRING_CODEC, VoteOptionStatistics.CODEC)
                .fieldOf("options")
                .forGetter(VoteStatistics::options)
        ).apply(instance, VoteStatistics::new)
    );

    /**
     * Compiles the raw statistics into a finalized VoteResults object.
     * * @param proposalId The UUID of the specific vote.
     * @param definition The definition/metadata of the vote (bgj).
     * @return A matched VoteResults instance.
     */
    public VoteResults compileResults(UUID proposalId, VoteDefinition definition) {
        Map<UUID, VoteChoice> totalVotesByPlayer = new HashMap<>();
        List<VoteResults.OptionResult> resultsList = new ArrayList<>();

        for (Map.Entry<VoteOptionId, VoteOptionStatistics> entry : this.options.entrySet()) {
            VoteOptionId optionId = entry.getKey();
            VoteOptionStatistics optionStats = entry.getValue();

            resultsList.add(new VoteResults.OptionResult(optionId, new VoteResults.ChoiceSummary(optionStats)));

            optionStats.voters().forEach((playerUuid, choice) -> 
                totalVotesByPlayer.compute(playerUuid, (id, existing) -> 
                    VoteChoice.accumulate(existing, choice.displayName(), choice.voteCount())
                )
            );
        }
        VoteOptionId summaryId = new VoteOptionId(proposalId, 1); 
        Map<VoteOptionId, VoteOptionStatistics> summaryMap = Map.of(
            summaryId, new VoteOptionStatistics(totalVotesByPlayer)
        );
        VoteStatistics finalStats = new VoteStatistics(summaryMap);

        return new VoteResults(
            proposalId, 
            definition, 
            finalStats
        );
    }
    
    /**
     * Represents the summarized result for a specific voting option.
     * <p>
     * This record ties a unique option ID to its calculated summary, 
     * including total scores and voter counts.
     * <p>
     * Official Name: bgs$a
     */
    public record OptionResult(VoteOptionId id, VoteResults.ChoiceSummary summary) {

        /**
         * A comparator to sort option results.
         * It uses the score/count from the ChoiceSummary for comparison.
         * Corresponds to 'static final Comparator<a> a' in bytecode.
         */
        @SuppressWarnings("rawtypes")
		public static final Comparator COMPARATOR = Comparator.comparing(
            OptionResult::summary,
            VoteResults.ChoiceSummary.COMPARATOR
        );

        /**
         * Returns the unique identifier for this voting option.
         * @return The {@link VoteOptionId}.
         */
        public VoteOptionId id() {
            return this.id;
        }

        /**
         * Returns the summary of votes for this option.
         * @return The {@link VoteResults.ChoiceSummary}.
         */
        public VoteResults.ChoiceSummary summary() {
            return this.summary;
        }
    }
}