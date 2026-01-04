package net.zhengzhengyiyi.vote;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.network.VoterData;
import net.zhengzhengyiyi.world.Vote;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class VoteManager {
    private static final long TICKS_PER_DAY = 18000L;
    
    public final Map<UUID, VoteDefinition> activeVotes = new HashMap<>();
    final VoteTracker tracker = new VoteTracker();
    private int totalProposalsCount;

    public VoteManager(MinecraftServer server) {
    }

    public void load(VoteState state) {
        this.activeVotes.clear();
        this.activeVotes.putAll(state.activeVotes());
        this.totalProposalsCount = state.totalProposalsCount();
        this.tracker.load(state.statistics());
        for (VoteValue value : state.activeValues()) {
            value.apply(VoterAction.APPROVE);
        }
    }

    public void initializeRules() {
    }
    
    public RegistryEntry.Reference<Vote> getRandomRule(MinecraftServer server, Random random) {
        Registry<Vote> registry = server.getRegistryManager().getOrThrow(VoteRegistries.VOTE_RULE_TYPE_KEY);
        return registry.streamEntries()
                .skip(random.nextInt(registry.size()))
                .findFirst()
                .orElseThrow();
    }

    public VoteState save() {
        java.util.stream.Stream<Object> activeValues = this.activeVotes.values().stream()
             .flatMap(definition -> definition.options().values().stream())
             .flatMap(option -> option.effects().stream())
             .map(effect -> effect.change());

        return VoteState.create(
            activeValues,
            new HashMap<>(this.activeVotes),
            this.tracker.export(),
            this.totalProposalsCount
        );
    }
    
    public VoterData method_50566(VoteOptionId arg) {
    	return this.tracker.method_50590(arg, false);
    }

    public void tick(long currentTime, MinecraftServer server, VoteDefinition.Context context, Consumer<VoteResults> onFinish, BiConsumer<UUID, VoteDefinition> onNewProposal) {
        Random random = context.random();
        
        boolean attemptPropose = this.shouldProposeRandomly(server, context.pos(), random) && context.isProposeEnabled();
        
        boolean isRevokeMode = context.isRevokeMode();
        
        int currentCount = 0;
        Set<Object> usedRules = new HashSet<>();
        
        Iterator<Map.Entry<UUID, VoteDefinition>> iterator = this.activeVotes.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<UUID, VoteDefinition> entry = iterator.next();
            UUID id = entry.getKey();
            VoteDefinition definition = entry.getValue();
            if (currentTime >= definition.metadata().getStartTime() + definition.metadata().getDuration()) {
                iterator.remove();
                VoteStatistics stats = this.tracker.finishVote(id); 
                onFinish.accept(new VoteResults(id, definition, stats));
                continue;
            }

            if (attemptPropose) {
                collectUsedRules(usedRules, definition);

                if (isRevokeMode) {
                    if (definition.hasAction(VoterAction.REPEAL)) {
                        currentCount++;
                    }
                } else {
                    if (definition.hasAction(VoterAction.APPROVE)) {
                        currentCount++;
                    }
                }
            }
        }

        if (attemptPropose) {
            if (isRevokeMode) {
                int limit = context.maxRevokeProposals();
                if (currentCount < limit) {
                    UUID newId = UUID.randomUUID();
                    Optional<VoteDefinition> proposal = VoteDefinition.proposeRevoke(newId, server, context);
                    proposal.ifPresent(v -> {
                        this.addVote(newId, v);
                        onNewProposal.accept(newId, v);
                    });
                }
            } else {
                int limit = context.maxApplyProposals();
                if (currentCount < limit) {
                    UUID newId = UUID.randomUUID();
                    Optional<VoteDefinition> proposal = VoteDefinition.proposeApply(newId, server, context);
                    proposal.ifPresent(v -> {
                        this.addVote(newId, v);
                        onNewProposal.accept(newId, v);
                    });
                }
            }
        }
    }

    private void collectUsedRules(Set<Object> set, VoteDefinition definition) {
        if (definition.options() == null) return;
        for (VoteDefinition.Option option : definition.options().values()) {
            for (VoteDefinition.Effect effect : option.effects()) {
                set.add(effect.change()); 
            }
        }
    }

    private boolean shouldProposeRandomly(MinecraftServer server, BlockPos pos, Random random) {
        long worldTime = server.getOverworld().getTime();
        
        // float f = (float)l / 18000.0F;
        float ratio = (float)worldTime / (float)TICKS_PER_DAY;
        
        if (ratio > 1.0F) {
            return true;
        } else {
            float chanceBase = (float)this.activeVotes.size() - (ratio * 7.0F);
            if (chanceBase < 0.0F) {
                return true;
            } else {
                // Math.pow(0.1, chanceBase)
                return random.nextFloat() < (float)Math.pow(0.1D, (double)chanceBase);
            }
        }
    }

    public int getProposalCount() {
        return this.totalProposalsCount;
    }

    public void addVote(UUID id, VoteDefinition definition) {
        this.activeVotes.put(id, definition);
        this.totalProposalsCount++;
    }

    @Nullable
    public VoteResults forceFinish(UUID id) {
        VoteDefinition definition = this.activeVotes.remove(id);
        if (definition != null) {
            VoteStatistics stats = this.tracker.finishVote(id);
            return new VoteResults(id, definition, stats);
        }
        return null;
    }

    public void castVote(UUID voteId, BiConsumer<VoteOptionId, VoteChoice> consumer) {
        this.tracker.processVote(voteId, consumer);
    }

    @Nullable
    public OptionHandle getOptionHandle(VoteOptionId optionId) {
        VoteDefinition definition = this.activeVotes.get(optionId.voteId());
        if (definition == null) return null;
        return new OptionHandle(optionId, definition);
    }

    public enum VoteAvailability {
        ALLOWED, VOTED, DENIED;
    }

    public class OptionHandle {
        private final VoteOptionId optionId;
        private final VoteDefinition definition;

        public OptionHandle(VoteOptionId optionId, VoteDefinition definition) {
            this.optionId = optionId;
            this.definition = definition;
        }

        public VoteAvailability checkRequirements(PlayerEntity player) {
            if (!(player instanceof ServerPlayerEntity)) {
                return VoteAvailability.DENIED;
            }
//            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

            // Cost (List<class_8390.class_8391>)
            
            /*
            for (VoteCost cost : this.definition.metadata().costs()) {
                if (cost.isTicketLimit()) {
                     int used = VoteManager.this.tracker.getVoteCount(..., player.getUuid());
                     if (used >= cost.amount()) return VoteAvailability.VOTED; 
                } else if (!cost.canAfford(serverPlayer)) {
                     return VoteAvailability.DENIED;
                }
            }
            */

            return VoteAvailability.ALLOWED;
        }
        
        public void submit(ServerPlayerEntity player, int weight) {
            VoteManager.this.tracker.record(this.optionId, player.getUuid(), player.getName().getString(), weight);
        }

        public Text getVoteTitle() {
            return this.definition.metadata().getDisplayName();
        }

        public Text getOptionName() {
            if (this.definition.options() == null) return Text.empty();
            VoteDefinition.Option option = this.definition.options().get(this.optionId);
            return (option != null) ? option.displayName() : Text.empty();
        }
    }
}
