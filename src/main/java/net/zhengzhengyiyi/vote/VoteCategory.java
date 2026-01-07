package net.zhengzhengyiyi.vote;

import java.util.List;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;

/**
 * Configuration context for generating vote proposals.
 * Defines probabilities, durations, and costs for a specific category of votes.
 * <p>
 * Official Name: bgp$c
 */
public record VoteCategory(
    Random random,
    float baseProbability,
    IntProvider minChanges,
    IntProvider maxChanges,
    IntProvider durationProvider,
    float feedbackProbability,
    int maxOptions,
    List<VoteCost.Instance> costs,
    boolean allowRollback,
    int postVoteDelay,
    int preVoteDelay,
    float fallbackProbability
) {

    /**
     * Factory method to create a category instance based on game rules or global settings.
     * Uses the 'bef' class (VoteSettings) to pull default values.
     */
//    public static VoteCategory create(Random random) {
//        return new VoteCategory(
//            random,
//            1.0F / (float)VoteRules.PROBABILITY_BASE.get().intValue(), 
//            VoteRules.MIN_CHANGES.get(), 
//            VoteRules.MAX_CHANGES.get(), 
//            VoteRules.DURATION.get(),
//            (float)VoteRules.FEEDBACK_CHANCE.get().intValue() / 100.0F, 
//            VoteRules.MAX_OPTIONS.get().intValue(), 
//            VoteRules.DEFAULT_COSTS.get(), 
//            !VoteRules.DISABLE_ROLLBACK.get(), 
//            VoteRules.POST_VOTE_TICK.get().intValue(), 
//            VoteRules.PRE_VOTE_TICK.get().intValue(), 
//            (float)VoteRules.FALLBACK_CHANCE.get().intValue() / 100.0F
//        );
//    }

    /**
     * Calculates the total duration of the vote in ticks.
     * @return Duration in ticks (usually multiplied by 1200 for minutes).
     */
    public int getDurationTicks() {
        return this.durationProvider.get(this.random) * 1200;
    }

    /**
     * Checks if a vote should trigger based on base probability.
     */
    public boolean shouldTrigger() {
        return this.random.nextFloat() < this.baseProbability;
    }

    /**
     * Checks if a fallback rule should be applied.
     */
    public boolean shouldFallback() {
        return this.random.nextFloat() < this.fallbackProbability;
    }

    /**
     * Checks if feedback effects should be triggered.
     */
    public boolean shouldTriggerFeedback() {
        return this.random.nextFloat() < this.feedbackProbability;
    }

    /**
     * Gets the number of rules to change for a "minor" update.
     */
    public int getMinChangeCount() {
        return this.minChanges.get(this.random);
    }

    /**
     * Gets the number of rules to change for a "major" update.
     */
    public int getMaxChangeCount() {
        return this.maxChanges.get(this.random);
    }
}
