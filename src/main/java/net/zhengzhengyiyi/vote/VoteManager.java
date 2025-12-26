package net.zhengzhengyiyi.vote;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.server.MinecraftServer;
//import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class VoteManager {
    private static final long TICKS_PER_DAY = 24000L;
    
//    private final ServerWorld world;
    private final Map<UUID, VoteDefinition> activeVotes = new LinkedHashMap<>();
    final VoteTracker tracker = new VoteTracker();
    private int totalProposalsCount;

    public VoteManager(MinecraftServer server) {
//        this.world = server.getSpawnWorld();
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

    public VoteState save() {
//        return VoteState.create(new HashMap(this.activeVotes), this.tracker.export(), this.totalProposalsCount);
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

    public void tick(long currentTime, MinecraftServer server, VoteDefinition.Context context, Consumer<VoteResults> onFinish, BiConsumer<UUID, VoteDefinition> onNewProposal) {
        Random random = context.random();
        boolean canPropose = this.shouldProposeRandomly(server, context.pos(), random) && context.isProposeEnabled();
//        boolean isRevokeMode = context.isRevokeMode();
        boolean isRevokeMode = true;
        int currentOptionCount = 0;
        
        VoterAction targetAction = isRevokeMode ? VoterAction.REVOKE : VoterAction.APPLY;
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

            if (canPropose && definition.hasAction(targetAction)) {
                currentOptionCount++;
            }
        }

        if (canPropose) {
            int limit = isRevokeMode ? context.maxOptions() : context.maxOptions();
            if (currentOptionCount < limit) {
                UUID newId = UUID.randomUUID();
                Optional<VoteDefinition> proposal = isRevokeMode ? 
                    VoteDefinition.proposeRevoke(newId, server, context) : 
                    VoteDefinition.proposeApply(newId, server, context);
                
                proposal.ifPresent(v -> {
                    this.addVote(newId, v);
                    onNewProposal.accept(newId, v);
                });
            }
        }
    }

    private boolean shouldProposeRandomly(MinecraftServer server, BlockPos pos, Random random) {
        long worldTime = server.getOverworld().getTime();
        float dayRatio = (float)(worldTime % TICKS_PER_DAY) / TICKS_PER_DAY;
        
        float chanceBase = (float)this.activeVotes.size() - (dayRatio * 7.0F);
        if (chanceBase < 0.0F) return true;

        return random.nextFloat() < Math.pow(0.1D, (double)chanceBase);
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
