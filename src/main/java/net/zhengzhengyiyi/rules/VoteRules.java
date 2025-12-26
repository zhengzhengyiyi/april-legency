package net.zhengzhengyiyi.rules;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.WeightedList;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.vote.VoteRegistries;
import net.zhengzhengyiyi.world.VoteRule;

import java.util.Collection;
import java.util.Optional;

/**
 * Registry class for all Vote Rules in the 23w13a "One Block at a Time" update.
 * Contains definitions for vote behavior, probabilities, and game-changing effects.
 */
public class VoteRules {
    private static final WeightedList<VoteRule> RULE_WEIGHTS = new WeightedList<>();

    // Internal constants for default weights
    public static final int WEIGHT_COMMON = 1000;
    public static final int WEIGHT_NORMAL = 500;
    public static final int WEIGHT_RARE = 125;

    // System Rules
//    public static final BooleanVoteRule TEST_RULE = register("test_rule_please_ignore", 7, new BooleanVoteRule(Text.literal("TEST RULE PLEASE IGNORE")));
//    public static final BooleanVoteRule PASS_WITHOUT_VOTERS = register("vote_result_pass_without_voters", WEIGHT_RARE, new BooleanVoteRule(Text.translatable("rule.vote_result_pass_without_voters")));
//    public static final BooleanVoteRule PASS_WITHOUT_VOTES = register("vote_result_pass_without_votes", WEIGHT_RARE, new BooleanVoteRule(Text.translatable("rule.vote_result_pass_without_votes")));
//    public static final BooleanVoteRule SHOW_TALLY = register("vote_result_show_tally", WEIGHT_NORMAL, new BooleanVoteRule(Text.translatable("rule.vote_result_show_options")));
//    
//    // Core Vote Configuration Rules
//    public static final IntegerVoteRule MAX_RESULTS = register("vote_max_results", WEIGHT_COMMON, new IntegerVoteRule(1, 1, 5, (val) -> Text.translatable("rule.vote_max_results", val)));
//    public static final IntegerVoteRule NEW_VOTE_CHANCE = register("new_vote_chance_per_tick", WEIGHT_NORMAL, new IntegerVoteRule(200, 1, 2000, (val) -> Text.translatable("rule.new_vote_chance_per_tick", val)));
//    public static final IntProviderVoteRule VOTE_DURATION = register("new_vote_duration_minutes", WEIGHT_COMMON, new IntProviderVoteRule(1, 20, 0, 10, 8, 16, (val) -> Text.translatable("rule.new_vote_duration_minutes", val)));
//    public static final VoteCostRule VOTE_COST = register("new_vote_cost", WEIGHT_NORMAL, new VoteCostRule());
//
//    // Gameplay Effect Rules (Examples)
//    public static final BooleanVoteRule INVISIBLE_ARMOR = register("invisible_armor", WEIGHT_NORMAL, new BooleanVoteRule(Text.translatable("rule.invisible_armor")));
//    public static final EnumVoteRule<WorldShape> WORLD_SHAPE = register("world_shape", WEIGHT_COMMON, new EnumVoteRule<>(WorldShape.values(), WorldShape.DEFAULT, WorldShape.ALTERNATIVE, (val) -> Text.translatable("rule.change_world_shape")));
//    public static final BooleanVoteRule MIDAS_TOUCH = register("midas_touch", WEIGHT_RARE, new BooleanVoteRule(Text.translatable("rule.midas_touch")));
//    
//    // Block Replacement Rules
//    public static final ReplaceBlockRule COBBLESTONE_REPLACE = register("cobblestone_gen_replace", WEIGHT_COMMON, new ReplaceBlockRule("rule.lava_water_replace", Blocks.COBBLESTONE));
//    public static final ReplaceBlockRule OBSIDIAN_REPLACE = register("obsidian_gen_replace", WEIGHT_RARE, new ReplaceBlockRule("rule.lava_water_replace", Blocks.OBSIDIAN));
//
//    // Player & Entity Rules
//    public static final BooleanVoteRule BIG_HEAD_MODE = register("big_head_mode", WEIGHT_COMMON, new BooleanVoteRule(Text.translatable("rule.big_heads")));
//    public static final BooleanVoteRule MINI_ME = register("minime", WEIGHT_NORMAL, new BooleanVoteRule(Text.translatable("rule.mini_players")));
//    public static final BooleanVoteRule GRAVITY_LESS = register("less_gravity", WEIGHT_RARE, new BooleanVoteRule(Text.translatable("rule.less_gravity")));
//    public static final BooleanVoteRule BEELOONS = register("beeloons", WEIGHT_COMMON, new BooleanVoteRule(Text.translatable("rule.beeloons")));

    /**
     * Registers a vote rule into the game registry and assigns a random selection weight.
     * * @param id The unique identifier for the rule.
     * @param weight The probability weight for random selection.
     * @param rule The rule instance to register.
     * @return The registered rule instance.
     */
//    private static <R extends VoteRule> R register(String id, int weight, R rule) {
//        // Register into the specialized Vote Rule registry (ja.ao in obfuscated)
//        Registry.register(VoteRegistries.VOTE_RULE_TYPE, Identifier.of(id), rule);
//        // Add to weighted list for random proposal generation
//        RULE_WEIGHTS.add(rule, weight);
//        return rule;
//    }

    /**
     * Picks a random vote rule based on the defined weights.
     * * @param random The random source.
     * @return A randomly selected vote rule.
     */
//    public static VoteRule getRandomRule(Random random) {
//        return RULE_WEIGHTS.getRandom(random).orElseThrow();
//    }
//
//    /**
//     * Specialized pick that ignores some constraints if necessary.
//     */
//    public static VoteRule getAlternativeRandomRule(Random random) {
//        return RULE_WEIGHTS.getRandom(random).orElse(TEST_RULE);
//    }
}
