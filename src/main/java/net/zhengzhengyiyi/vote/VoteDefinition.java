package net.zhengzhengyiyi.vote;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.rules.VoteRules;
import net.zhengzhengyiyi.world.Vote;
import net.zhengzhengyiyi.world.VoteRule;

/**
 * Defines the content and structure of a vote.
 * <p>
 * Official Name: bgp
 */
public record VoteDefinition(VoteMetadata metadata, Map<VoteOptionId, Option> options) {

    public static final Codec<VoteDefinition> CODEC = RecordCodecBuilder.create(instance -> 
        instance.group(
            VoteMetadata.CODEC.forGetter(VoteDefinition::metadata),
            Codec.unboundedMap(VoteOptionId.CODEC, Option.CODEC).fieldOf("options").forGetter(VoteDefinition::options)
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
    public static Optional<VoteDefinition> proposeApply(UUID id, MinecraftServer server, Context context) {
//        Random random = context.random();
        int count = context.getOptionCount();
        
        List<List<VoteValue>> proposals = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
//          VoteRule rule = VoteRules.getRandomRule(random);
        	Vote rule = VoteRules.getRandomRule(context.random()).value();
            List<VoteValue> values = new ArrayList<>();
//          values.add(new VoteValue(rule, rule.getRandomValue(random)));
            values.add(new VoteValue() {

				@Override
				public Vote getType() {
					return null;
				}

				@Override
				public void apply(VoterAction action) {
					
				}

				@Override
				public Text getDescription(VoterAction action) {
					return null;
				}
            	
            });
            proposals.add(values);
        }

        if (proposals.isEmpty()) return Optional.empty();

        if (context.allowNothingOption() || proposals.size() == 1) {
            proposals.add(List.of());
        }

        List<Option> optionList = proposals.stream().map(values -> {
            List<Effect> effects = values.stream().map(v -> new Effect(v, VoterAction.APPROVE)).toList();
            return new Option(createOptionText(effects), effects);
        }).toList();

        return Optional.of(finalizeProposal(id, server, context, optionList));
    }

    /**
     * Proposes to remove an existing active rule.
     * <p>
     * Corresponds to the 'b' method in bgp for generating revoke votes.
     */
    public static Optional<VoteDefinition> proposeRevoke(UUID id, MinecraftServer server, Context context) {
        return Optional.empty(); 
    }

    private static Text createOptionText(List<Effect> effects) {
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
        
        public int getDuration() { return durationTicks * 1200; }
        public int getOptionCount() { return maxOptions; }
        public boolean allowNothingOption() { return true; }
        public List<VoteCost> getCosts() { return costs; }
        public boolean isProposeEnabled() { return true; }
        public int maxApplyProposals() { return 3; }
        public int maxRevokeProposals() { return 1; }
    }
}