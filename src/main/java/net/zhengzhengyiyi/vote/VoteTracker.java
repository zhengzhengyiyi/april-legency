package net.zhengzhengyiyi.vote;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.text.Text;
import net.minecraft.world.World;

/**
 * official bgv
 */
public class VoteTracker {
    private final Map<VoteOptionId, Map<UUID, VoteChoice>> votesByOption = new HashMap<>();

    public void record(VoteOptionId optionId, UUID playerUuid, String playerName, int weight) {
        Map<UUID, VoteChoice> optionVotes = this.votesByOption.computeIfAbsent(optionId, k -> new HashMap<>());
        optionVotes.compute(playerUuid, (uuid, existing) -> {
            int newWeight = (existing == null) ? weight : existing.voteCount() + weight;
            return new VoteChoice(Text.literal(playerName), newWeight);
        });
    }

    public int getVoteWeight(VoteOptionId optionId, UUID playerUuid) {
        Map<UUID, VoteChoice> optionVotes = this.votesByOption.get(optionId);
        if (optionVotes == null) return 0;
        VoteChoice choice = optionVotes.get(playerUuid);
        return (choice != null) ? choice.voteCount() : 0;
    }

    public int getTotalWeightForPlayer(Set<VoteOptionId> options, UUID playerUuid) {
        return options.stream().mapToInt(id -> getVoteWeight(id, playerUuid)).sum();
    }

    public void load(VoteStatistics stats) {
        this.votesByOption.clear();
        stats.options().forEach((id, stat) -> 
            this.votesByOption.put(id, new HashMap<>(stat.voters()))
        );
    }

    public VoteStatistics export() {
        Map<VoteOptionId, VoteOptionStatistics> compiled = this.votesByOption.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey, 
                e -> new VoteOptionStatistics(Map.copyOf(e.getValue()))
            ));
        return new VoteStatistics(compiled);
    }

    public VoteStatistics finishVote(UUID voteId) {
        Map<VoteOptionId, VoteOptionStatistics> results = new HashMap<>();
        this.votesByOption.keySet().removeIf(optionId -> {
            if (optionId.voteId().equals(voteId)) {
                Map<UUID, VoteChoice> voters = this.votesByOption.get(optionId);
                results.put(optionId, new VoteOptionStatistics(Map.copyOf(voters)));
                return true;
            }
            return false;
        });
        return new VoteStatistics(results);
    }

    public void processVote(UUID playerUuid, BiConsumer<VoteOptionId, VoteChoice> consumer) {
        this.votesByOption.forEach((id, voters) -> {
            VoteChoice choice = voters.get(playerUuid);
            if (choice != null) {
                consumer.accept(id, choice);
            }
        });
    }

    public interface VoteCost {
        boolean apply(World world, int amount, boolean simulate);

        Category getCategory();

        Text getDescription();

        enum Category {
            ONCE,
            EACH_VOTE
        }
    }
}
