package net.zhengzhengyiyi.vote;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import java.util.stream.Stream;
import java.util.Optional;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.rules.VoteRules;
import net.zhengzhengyiyi.world.Vote;

/**
 * Defines the content and structure of a vote.
 */
public record VoteDefinition(VoteMetadata metadata, Map<VoteOptionId, Option> options) {
	public static final Codec<VoteDefinition> CODEC = RecordCodecBuilder.create(instance -> 
	    instance.group(
	        VoteMetadata.CODEC.forGetter(VoteDefinition::metadata),
	        Codec.<VoteOptionId, Option>unboundedMap(
	            Codec.STRING.comapFlatMap(
	                s -> {
	                    try {
	                        return DataResult.success(new VoteOptionId(UUID.fromString(s), 0));
	                    } catch (Exception e) {
	                        return DataResult.error(() -> "Invalid UUID string: " + s);
	                    }
	                },
	                id -> id.voteId().toString()
	            ), 
	            Option.CODEC
	        ).fieldOf("options").forGetter(VoteDefinition::options)
	    ).apply(instance, VoteDefinition::new)
	);

    public static final Text NOTHING_RULE_TEXT = Text.translatable("rule.nothing");

    /**
     * Represents an effect applied when an option wins.
     * <p>
     * Official Name: bgp$a
     */
    public record Effect(VoteValue change, VoterAction action) {
        public static final Codec<Effect> CODEC = RecordCodecBuilder.create(instance -> 
            instance.group(
                VoteValue.CODEC.fieldOf("change").forGetter(Effect::change),
                VoterAction.CODEC.fieldOf("action").forGetter(Effect::action)
            ).apply(instance, Effect::new)
        );

        /**
         * Applies this effect to the server.
         */
        public void apply(MinecraftServer server) {
            this.change.apply(this.action);
        }

        /**
         * Gets the description text for this effect.
         */
        public Text getDescription() {
            return this.change.getDescription(this.action);
        }
    }

    /**
     * Represents a selectable option in a vote.
     * <p>
     * Official Name: bgp$b
     */
    public record Option(Text displayName, List<Effect> effects) {
        public static final Codec<Option> CODEC = RecordCodecBuilder.create(instance -> 
            instance.group(
                TextCodecs.CODEC.fieldOf("display_name").forGetter(Option::displayName),
                Effect.CODEC.listOf().fieldOf("changes").forGetter(Option::effects)
            ).apply(instance, Option::new)
        );

        public boolean containsAction(VoterAction action) {
            return this.effects.stream().anyMatch(effect -> effect.action() == action);
        }
    }

    public boolean hasAction(VoterAction action) {
        return this.options.values().stream().anyMatch(option -> option.containsAction(action));
    }

    /**
     * Proposes a new set of rules to be applied to the game.
     * <p>
     * Corresponds to the 'a' method in bgp for generating new apply votes.
     */
    public static Optional<VoteDefinition> proposeApply(UUID id, MinecraftServer server, VoteDefinition.Context settings) {
        Set<Vote> activeRules = new HashSet<>();
        
        for (VoteDefinition definition : ((VoteServer) server).getVoteManager().activeVotes.values()) {
            for (VoteDefinition.Option option : definition.options().values()) {
                for (VoteDefinition.Effect effect : option.effects()) {
                    activeRules.add(effect.change().getType());
                }
            }
        }

        activeRules.addAll(((VoteServer) server).getVoteManager().method_50570());

        Optional<Vote> randomRule = method_50525(activeRules, settings)
                .findAny()
                .map(net.minecraft.registry.entry.RegistryEntry.Reference::value);

        if (randomRule.isEmpty()) {
            return Optional.empty();
        }

        Vote rule = randomRule.get();
        int optionCount = settings.maxOptions();
        List<List<VoteValue>> generatedValues = new ArrayList<>();
        
        rule.generateOptions(server, settings.random(), optionCount).forEach(val -> {
            generatedValues.add(new ArrayList<>(List.of(val)));
        });

        if (generatedValues.isEmpty()) {
            return Optional.empty();
        }

        List<Option> optionList = generatedValues.stream().map(valueList -> {
            List<Effect> effects = valueList.stream()
                    .map(val -> new Effect(val, VoterAction.APPROVE))
                    .toList();
            return new Option(createOptionText(effects), effects);
        }).toList();

        return Optional.of(finalizeProposal(id, server, settings, optionList));
    }
    
    public static Stream<RegistryEntry.Reference<Vote>> method_50525(Set<Vote> set, VoteDefinition.Context arg) {
        return Stream.generate(() -> VoteRules.getRandomRule(arg.random))
            .filter(opt -> opt.hasKeyAndValue())
            .limit(1000L)
            .filter(reference -> !set.contains(reference.value()));
    }

    /**
     * Proposes to remove an existing active rule.
     * <p>
     * Corresponds to the 'b' method in bgp for generating revoke votes.
     */
    public static Optional<VoteDefinition> proposeRevoke(UUID id, MinecraftServer server, Context context) {
        return Optional.empty(); 
    }

    public static Text createOptionText(List<Effect> effects) {
        return effects.stream()
            .map(Effect::getDescription)
            .reduce((first, second) -> Text.translatable("rule.connector", first, second))
            .orElse(NOTHING_RULE_TEXT);
    }

    private static VoteDefinition finalizeProposal(UUID id, MinecraftServer server, Context context, List<Option> optionList) {
        Map<VoteOptionId, Option> map = new LinkedHashMap<>();
        for (int i = 0; i < optionList.size(); i++) {
            map.put(new VoteOptionId(id, i), optionList.get(i));
        }
        
        int proposalIndex = ((VoteServer)server).getVoteManager().getProposalCount();
        Text title = Text.translatable("rule.proposal", proposalIndex + 1);
        long startTime = server.getOverworld().getTime();
        
        return new VoteDefinition(new VoteMetadata(title, startTime, (long)context.getDuration(), context.getCosts()), map);
    }

    /**
     * Contextual rules and parameters for vote generation.
     * <p>
     * Official Name: bgp$c
     */
    public record Context(BlockPos pos, float difficulty, List<VoteCost> costs, int durationTicks, int maxOptions, Random random, boolean isRevokeMode) {
//        public int getDuration() { return durationTicks * 1200; }
// TODO
    	public int getDuration() { return durationTicks * 600; }
        public int getOptionCount() { return maxOptions; }
        public boolean allowNothingOption() { return true; }
        public List<VoteCost> getCosts() { return costs; }
        public boolean isProposeEnabled() { return true; }
        public int maxApplyProposals() { return 3; }
        public int maxRevokeProposals() { return 1; }
    }
    
    
    public record VoteSettings(
    	    Random random,
    	    float newVoteChancePerTick,
    	    IntProvider optionsPerApproveVote,
    	    IntProvider optionsPerRepealVote,
    	    IntProvider durationMinutes,
    	    float extraOptionChance,
    	    int maxExtraOptions,
    	    List<VoteCost> voteCost,
    	    boolean alwaysAddOptOutVote,
    	    int maxApproveVoteCount,
    	    int maxRepealVoteCount,
    	    float repealVoteChance
    	) {
    	public static VoteSettings create(Random random) {
    		  return new VoteSettings(random,
    				  1.0F / VoteRules.CHANCE_PER_TICK
	    		      .getValue().intValue(),
	    		      VoteRules.APPROVE_OPTION_COUNT
	    		      .getCurrentRange(), VoteRules.REPEAL_OPTION_COUNT
	    		      .getCurrentRange(), VoteRules.VOTE_DURATION
	    		      .getCurrentRange(), VoteRules.EXTRA_EFFECT_CHANCE
	    		      .getValue().intValue() / 100.0F,
	    		      VoteRules.EXTRA_EFFECT_MAX_COUNT.getValue(),
//	    		      .getValue().intValue(), VoteRules.VOTE_COST.getCurrentCosts(),
	    		      VoteRules.VOTE_COST.getCurrentCosts().stream()
	    	            .map(VoteCost.Instance::cost)
	    	            .toList(),
	    		      !VoteRules.DISABLE_OPT_OUT.isActive(), 
	    		      VoteRules.MAX_APPROVE_COUNT
	    		      .getValue().intValue(), VoteRules.MAX_REPEAL_COUNT
	    		      .getValue().intValue(), VoteRules.REPEAL_VOTE_CHANCE
	    		      .getValue().intValue() / 100.0F);
    		}

    	    public int getDurationTicks() {
    	        return this.durationMinutes.get(this.random) * 1200;
    	    }

    	    public boolean shouldStartNewVote() {
    	        return this.random.nextFloat() < this.newVoteChancePerTick;
    	    }

    	    public boolean shouldRepealVote() {
    	        return this.random.nextFloat() < this.repealVoteChance;
    	    }

    	    public boolean shouldAddExtraOption() {
    	        return this.random.nextFloat() < this.extraOptionChance;
    	    }

    	    public int getApproveOptionCount() {
    	        return this.optionsPerApproveVote.get(this.random);
    	    }

    	    public int getRepealOptionCount() {
    	        return this.optionsPerRepealVote.get(this.random);
    	    }
    	}
}