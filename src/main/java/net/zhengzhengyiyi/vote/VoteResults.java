package net.zhengzhengyiyi.vote;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.text.Text;

/**
 * The results of a finished vote.
 * <p>
 * Official Name: bgl
 * Intermediary Name: net.minecraft.class_8370
 */
public record VoteResults(UUID id, VoteDefinition vote, VoteStatistics results) {
	public VoteResults {
	}

    /**
     * Comparator for sorting voters by their weight or ID.
     */
//    private static final Comparator<Map.Entry<UUID, VoterAction>> VOTER_COMPARATOR = 
//        Map.Entry.<UUID, VoterAction>comparingByValue(Comparator.comparingInt(VoterAction::getWeight).reversed())
//        .thenComparing(Map.Entry.comparingByKey());
	public static final Comparator<Map.Entry<UUID, VoterAction>> VOTER_COMPARATOR = 
		    Map.Entry.<UUID, VoterAction>comparingByValue(Comparator.naturalOrder())
		    .thenComparing(Map.Entry.comparingByKey());

    /**
     * Comparator for sorting vote options by their index.
     */
    private static final Comparator<VoteStatistics.OptionResult> OPTION_INDEX_COMPARATOR = 
        Comparator.comparingInt(option -> option.id().index());

    /**
     * Processes and finishes the vote, applying the winning effects.
     *
     * @param effectConsumer Consumer for the winning effects.
     * @param feedbackConsumer Consumer for sending feedback messages.
     * @param config The configuration for processing results.
     */
    @SuppressWarnings("static-access")
	public void finish(Consumer<VoteDefinition.Effect> effectConsumer, Consumer<Text> feedbackConsumer, ResultConfig config) {
        feedbackConsumer.accept(Text.translatable("vote.finished", this.vote.NOTHING_RULE_TEXT.getString()));
        
        List<VoteStatistics.OptionResult> allOptions = this.getAllOptions();
        this.printDetails(feedbackConsumer, config, allOptions);
        
        List<VoteStatistics.OptionResult> winners = this.determineWinners(feedbackConsumer, config, allOptions);
        
        if (winners.isEmpty()) {
            feedbackConsumer.accept(Text.translatable("vote.no_option"));
            return;
        }

        this.applyWinners(effectConsumer, feedbackConsumer, winners);
    }

    /**
     * Collects all possible options, including those with zero votes.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private List<VoteStatistics.OptionResult> getAllOptions() {
        Set<VoteOptionId> remaining = new HashSet<>(this.vote.options().keySet());
        List<VoteStatistics.OptionResult> list = new ArrayList(this.results.options().entrySet().stream()
                .peek(entry -> remaining.remove(entry.getKey()))
                .collect(Collectors.toCollection(ArrayList::new)));
        
//        remaining.stream()
//                .map(id -> new VoteStatistics.OptionResult(id, VoteStatistics.VoteCount.EMPTY))
//                .forEach(list::add);
        return list;
    }

    /**
     * Determines which options won the vote based on quorum and tie-breaking rules.
     */
    private List<VoteStatistics.OptionResult> determineWinners(Consumer<Text> feedback, ResultConfig config, List<VoteStatistics.OptionResult> options) {
        List<VoteStatistics.OptionResult> winners;
        if (this.checkQuorum(feedback, config)) {
            winners = this.filterAndTieBreak(feedback, options, config);
        } else {
            winners = List.of();
        }

        if (winners.isEmpty() && this.shouldFallbackToRandom(config)) {
            feedback.accept(Text.translatable("vote.no_option.random"));
            ObjectArrayList<VoteStatistics.OptionResult> shuffled = options.stream()
                    .sorted(OPTION_INDEX_COMPARATOR)
                    .collect(Collectors.toCollection(ObjectArrayList::new));
//            Util.shuffle(shuffled, config.randomSource());
            winners = shuffled.stream().limit(config.maxWinners()).toList();
        }
        return winners;
    }

    /**
     * Checks if the total number of voters meets the required quorum.
     */
    private boolean checkQuorum(Consumer<Text> feedback, ResultConfig config) {
        int minRequired = config.requiresAnyVote() ? 1 : 0;
        int quorum = Math.max(Math.round(config.quorumPercentage() * config.maxPossibleVoters()), minRequired);
        int totalVoters = this.results.totalVotes().getVoterCount();
        
        if (totalVoters < quorum) {
            feedback.accept(Text.translatable("vote.quorum.not_reached", quorum));
            return false;
        }
        feedback.accept(Text.translatable("vote.quorum.passed", totalVoters, quorum));
        return true;
    }

    /**
     * Filters options by minimum vote count and resolves ties.
     */
    private List<VoteStatistics.OptionResult> filterAndTieBreak(Consumer<Text> feedback, List<VoteStatistics.OptionResult> options, ResultConfig config) {
        Map<Object, Collection<net.zhengzhengyiyi.vote.VoteStatistics.OptionResult>> groupedByVotes = options.stream()
                .collect(Collectors.groupingBy(opt -> opt.summary().getScore(), Collectors.toCollection(ArrayList::new)));
        
        List<VoteStatistics.OptionResult> winners = new ArrayList<>();
        boolean tieReported = false;
        int minVotes = this.getMinVotesRequired(feedback, config);
        
//        Comparator<Integer> scoreSort = config.reverseOrder() ? Comparator.reverseOrder() : Comparator.naturalOrder();
//        List<Map.Entry<Integer, List<VoteStatistics.OptionResult>>> sortedGroups = groupedByVotes.entrySet().stream()
//                .filter(entry -> (int)entry.getKey() >= minVotes)
//                .sorted(Map.Entry.comparingByKey(scoreSort))
//                .toList();
        
//        Map<Integer, List<VoteStatistics.OptionResult>> sortedGroups = options.stream()
//                .collect(Collectors.groupingBy(
//                    opt -> opt.summary().getScore(), 
//                    Collectors.toCollection(ArrayList::new)
//        );
        
        List<Entry<Object, Collection<net.zhengzhengyiyi.vote.VoteStatistics.OptionResult>>> sortedGroups = groupedByVotes.entrySet().stream()
                .filter(entry -> (int)entry.getKey() >= minVotes)
//                .sorted(Map.Entry.<Integer, List<VoteStatistics.OptionResult>>comparingByKey(scoreSort))
                .collect(Collectors.toList());

        for (Entry<Object, Collection<net.zhengzhengyiyi.vote.VoteStatistics.OptionResult>> group : sortedGroups) {
            List<VoteStatistics.OptionResult> tiedOptions = (List<net.zhengzhengyiyi.vote.VoteStatistics.OptionResult>) group.getValue();
            if (config.skipEmpty() && (int)group.getKey() == 0) continue;

            if (tiedOptions.size() > 1) {
                TieBreaker strategy = config.tieBreaker();
                if (!tieReported) {
                    tieReported = true;
                    feedback.accept(Text.translatable("vote.tie", strategy.asString()));
                }
                switch (strategy) {
                    case FIRST -> tiedOptions.stream().min(OPTION_INDEX_COMPARATOR).ifPresent(winners::add);
                    case LAST -> tiedOptions.stream().max(OPTION_INDEX_COMPARATOR).ifPresent(winners::add);
//                    case RANDOM -> Util.getRandom(tiedOptions, config.randomSource()).ifPresent(winners::add);
                    case ALL -> winners.addAll(tiedOptions.stream().sorted(OPTION_INDEX_COMPARATOR).toList());
                    case NONE -> { winners.clear(); return winners; }
                    default -> {}
                }
            } else {
                winners.addAll(tiedOptions);
            }
            if (winners.size() >= config.maxWinners()) break;
        }
        return winners;
    }

    /**
     * Calculates the minimum number of votes required for an option to win.
     */
    private int getMinVotesRequired(Consumer<Text> feedback, ResultConfig config) {
        int min = Math.round(config.minVotePercentage() * this.results.totalVotes().getScore());
        if (min > 0) {
            feedback.accept(Text.translatable("vote.vote_count.minimum", min));
        }
        return min;
    }

    /**
     * Applies the winning effects and sends success messages.
     */
    private void applyWinners(Consumer<VoteDefinition.Effect> effectConsumer, Consumer<Text> feedback, List<VoteStatistics.OptionResult> winners) {
        List<WinnerEntry> entries = new ArrayList<>();
        for (VoteStatistics.OptionResult winner : winners) {
            VoteDefinition.Option definition = this.vote.options().get(winner.id());
            if (definition != null) entries.add(new WinnerEntry(winner.id(), definition));
        }

        if (entries.stream().allMatch(e -> e.definition().effects().isEmpty())) {
            feedback.accept(Text.translatable("vote.no_change"));
            return;
        }

        for (WinnerEntry entry : entries) {
            if (entry.definition().effects().isEmpty()) {
                feedback.accept(Text.translatable("vote.option_won.no_effect", entry.id().index() + 1, entry.definition().displayName()));
            } else {
                feedback.accept(Text.translatable("vote.option_won", entry.id().index() + 1, entry.definition().displayName()));
                for (VoteDefinition.Effect effect : entry.definition().effects()) {
                    feedback.accept(effect.getDescription());
                    effectConsumer.accept(effect);
                }
            }
        }
    }

    /**
     * Checks if the system should pick a random winner if no votes were cast.
     */
    private boolean shouldFallbackToRandom(ResultConfig config) {
        return config.allowRandomFallback() && (!config.requiresAnyVote() || this.results.totalVotes().getVoterCount() > 0);
    }

    private void printDetails(Consumer<Text> feedback, ResultConfig config, List<VoteStatistics.OptionResult> options) {
        // Implementation for printing detailed counts per option
    }

    /**
     * Represents a single winner and its definition.
     */
    private record WinnerEntry(VoteOptionId id, VoteDefinition.Option definition) {}

    /**
     * Configuration for vote result processing.
     * <p>
     * Official Name: bgl$b
     */
    public record ResultConfig(
        net.minecraft.util.math.random.Random randomSource, boolean requiresAnyVote, int maxPossibleVoters,
        float quorumPercentage, float minVotePercentage, boolean showTotal,
        boolean showVoters, boolean allowRandomFallback, boolean reverseOrder,
        boolean skipEmpty, int maxWinners, TieBreaker tieBreaker
    ) {}
    
    public record OptionResult(VoteOptionId id, ChoiceSummary summary) {

		public OptionResult {
		}
    }

    /**
     * A processed summary of votes, typically used to calculate scores and voter counts.
     * <p>
     * Official Name: bgs$b
     */
    public static class ChoiceSummary {
        private final VoteOptionStatistics stats;

        /**
         * Wraps raw option statistics into a summary for easier result processing.
         * @param stats The raw statistics (bgo) to summarize.
         */
        public ChoiceSummary(VoteOptionStatistics stats) {
            this.stats = stats;
        }

        /**
         * Gets the total number of unique players who participated.
         * @return The voter count from the underlying stats.
         */
        public int getVoterCount() {
            return this.stats.getVoterCount();
        }
        
        public static final Comparator<ChoiceSummary> COMPARATOR = 
                Comparator.comparingInt(ChoiceSummary::getScore).reversed();

        /**
         * Gets the calculated total score (sum of weights).
         * @return The combined weight of all votes in this summary.
         */
        public int getScore() {
            return this.stats.getScore();
        }

        /**
         * Accesses the raw statistics map if needed.
         */
        public VoteOptionStatistics getRawStats() {
            return this.stats;
        }
    }
}
