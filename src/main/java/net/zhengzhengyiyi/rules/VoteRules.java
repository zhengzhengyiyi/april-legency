package net.zhengzhengyiyi.rules;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntry.Reference;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.ClampedNormalFloatProvider;
import net.minecraft.util.math.intprovider.ClampedIntProvider;
import net.minecraft.util.math.intprovider.ClampedNormalIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.rules.options.*;
import net.zhengzhengyiyi.vote.VoteRegistries;
import net.zhengzhengyiyi.world.Vote;
import net.zhengzhengyiyi.world.Vote;
import net.zhengzhengyiyi.world.WorldShape;
import net.minecraft.util.collection.WeightedList;

public class VoteRules {
	private static final Logger LOGGER = LoggerFactory.getLogger(VoteRules.class);
//	private static final Pool<RegistryEntry.Reference<Vote>> POOL_BUILDER = new DataPool.Builder<>();
	private static final WeightedList<Reference<Vote>> POOL_BUILDER = new WeightedList<RegistryEntry.Reference<Vote>>();
	public static final int WEIGHT_VERY_RARE = 5;
	public static final int WEIGHT_RARE = 2;
	public static final int WEIGHT_UNCOMMON = 7;
	public static final int WEIGHT_COMMON = 1000;
	public static final int WEIGHT_ALWAYS = 1000;
	public static final int WEIGHT_OFTEN = 500;
	public static final int WEIGHT_SOMETIMES = 125;

	public static final StaticVoteRule TEST_RULE = register("test_rule_please_ignore", 7, new StaticVoteRule(Text.literal("TEST RULE PLEASE IGNORE")));
	public static final StaticVoteRule PASS_WITHOUT_VOTERS = register("vote_result_pass_without_voters", 125, new StaticVoteRule(Text.translatable("rule.vote_result_pass_without_voters")));
	public static final StaticVoteRule PASS_WITHOUT_VOTES = register("vote_result_pass_without_votes", 125, new StaticVoteRule(Text.translatable("rule.vote_result_pass_without_votes")));
	public static final StaticVoteRule SHOW_TALLY = register("vote_result_show_tally", 500, new StaticVoteRule(Text.translatable("rule.vote_result_show_options")));
	public static final StaticVoteRule SHOW_VOTERS = register("vote_result_show_voters", 500, new StaticVoteRule(Text.translatable("rule.vote_result_show_voters")));
	public static final StaticVoteRule RANDOM_IF_FAIL = register("vote_result_pick_random_if_vote_fails", 125, new StaticVoteRule(Text.translatable("rule.vote_result_pick_random_if_vote_fails")));
	public static final StaticVoteRule REVERSE_COUNTS = register("vote_result_reverse_counts", 125, new StaticVoteRule(Text.translatable("rule.vote_result_reverse_counts")));

	public static final NumberVoteRule.IntegerRule MAX_RESULTS = register("vote_max_results", 1000, new NumberVoteRule.IntegerRule(1, UniformIntProvider.create(1, 5)) {
		protected Text getOptionDescription(Integer val) {
			return Text.translatable("rule.vote_max_results", val);
		}
	});

	public static final NumberVoteRule.IntegerRule CHANCE_PER_TICK = register("new_vote_chance_per_tick", 500, new NumberVoteRule.IntegerRule(200, UniformIntProvider.create(1, 2000)) {
		protected Text getOptionDescription(Integer val) {
			return Text.translatable("rule.new_vote_chance_per_tick", val);
		}
	});

	public static final VoteOptionCountRule APPROVE_OPTION_COUNT = register("new_vote_approve_option_count", 500, new VoteOptionCountRule(UniformIntProvider.create(1, 5), UniformIntProvider.create(0, 4), UniformIntProvider.create(2, 4)) {
		@Override
		protected Text getOptionDescription(UniformIntProvider provider) {
			return Text.translatable("rule.new_vote_approve_option_count", provider.toString());
		}
	});

	public static final VoteOptionCountRule REPEAL_OPTION_COUNT = register("new_vote_repeal_option_count", 500, new VoteOptionCountRule(UniformIntProvider.create(1, 5), UniformIntProvider.create(0, 4), UniformIntProvider.create(2, 4)) {
		@Override
		protected Text getOptionDescription(UniformIntProvider provider) {
			return Text.translatable("rule.new_vote_repeal_option_count", provider.toString());
		}
	});

	public static final VoteOptionCountRule VOTE_DURATION = register("new_vote_duration_minutes", 1000, new VoteOptionCountRule(UniformIntProvider.create(1, 20), UniformIntProvider.create(0, 10), UniformIntProvider.create(8, 16)) {
		@Override
		protected Text getOptionDescription(UniformIntProvider provider) {
			return Text.translatable("rule.new_vote_duration_minutes", provider.toString());
		}
	});

	public static final NumberVoteRule.IntegerRule EXTRA_EFFECT_CHANCE = register("new_vote_extra_effect_chance", 1000, new NumberVoteRule.IntegerRule(30, UniformIntProvider.create(0, 80)) {
		protected Text getOptionDescription(Integer val) {
			return Text.translatable("rule.new_vote_extra_effect_chance", val);
		}
	});

	public static final NumberVoteRule.IntegerRule EXTRA_EFFECT_MAX_COUNT = register("new_vote_extra_effect_max_count", 1000, new NumberVoteRule.IntegerRule(1, UniformIntProvider.create(0, 5)) {
		protected Text getOptionDescription(Integer val) {
			return Text.translatable("rule.new_vote_extra_effect_max_count", val);
		}
	});

	public static final NumberVoteRule.IntegerRule REPEAL_VOTE_CHANCE = register("new_vote_repeal_vote_chance", 500, new NumberVoteRule.IntegerRule(50, UniformIntProvider.create(20, 80)) {
		protected Text getOptionDescription(Integer val) {
			return Text.translatable("rule.new_vote_repeal_vote_chance", val);
		}
	});

	public static final StaticVoteRule DISABLE_OPT_OUT = register("new_vote_disable_opt_out", 125, new StaticVoteRule(Text.translatable("rule.new_vote_disable_opt_out")));

	public static final NumberVoteRule.IntegerRule MAX_APPROVE_COUNT = register("new_vote_max_approve_vote_count", 500, new NumberVoteRule.IntegerRule(5, UniformIntProvider.create(1, 10)) {
		protected Text getOptionDescription(Integer val) {
			return Text.translatable("rule.new_vote_max_approve_vote_count", val);
		}
	});

	public static final NumberVoteRule.IntegerRule MAX_REPEAL_COUNT = register("new_vote_max_repeal_vote_count", 500, new NumberVoteRule.IntegerRule(2, UniformIntProvider.create(1, 10)) {
		protected Text getOptionDescription(Integer val) {
			return Text.translatable("rule.new_vote_max_repeal_vote_count", val);
		}
	});

	public static final VoteCostRule VOTE_COST = register("new_vote_cost", 500, new VoteCostRule());
	public static final StaticVoteRule INVISIBLE_ARMOR = register("invisible_armor", 500, new StaticVoteRule(Text.translatable("rule.invisible_armor")));

	public static final EnumVoteRule<WorldShape> WORLD_SHAPE = register("world_shape", 1000, new EnumVoteRule<>(WorldShape.values(), WorldShape.DEFAULT, WorldShape.CODEC) {
		protected Text getOptionDescription(WorldShape val) {
			return Text.translatable("rule.change_world_shape");
		}
	});

	public static final StaticVoteRule DISABLE_ITEM_TOOLTIPS = register("disable_item_tooltips", 500, new StaticVoteRule(Text.translatable("rule.disable_item_tooltips")));

	public static final NumberVoteRule.IntegerRule QUORUM_PERCENT = register("quorum_percent", 500, new NumberVoteRule.IntegerRule(0, createClampedPercentProvider(20)) {
		protected Text getOptionDescription(Integer val) {
			return Text.translatable("rule.quorum_percent", val);
		}
	});

	public static final NumberVoteRule.IntegerRule VOTES_TO_WIN_PERCENT = register("votes_to_win_percent", 125, new NumberVoteRule.IntegerRule(0, createClampedPercentProvider(20)) {
		protected Text getOptionDescription(Integer val) {
			return Text.translatable("rule.votes_to_win_percent", val);
		}
	});

	public static final StaticVoteRule OTHER_PORTAL = register("other_portal", 500, new StaticVoteRule(Text.translatable("rule.other_portal")));
	public static final StaticVoteRule ANONYMIZE_SKINS = register("anonymize_skins", 500, new StaticVoteRule(Text.translatable("rule.anonymize_skins")));
	public static final SpecialRecipeRule SPECIAL_RECIPE = register("special_recipe", 1000, new SpecialRecipeRule());
//	public static final FootprintsRule FOOTPRINTS = register("footprints", 500, new FootprintsRule());

	public static final EnumVoteRule<TieStrategy> TIE_STRATEGY = register("tie_strategy", 500, new EnumVoteRule<>(TieStrategy.values(), TieStrategy.PICK_RANDOM, TieStrategy.CODEC) {
		protected Text getOptionDescription(TieStrategy val) {
			return val.getDescription();
		}
	});

	public static final StaticVoteRule SILENT_VOTE = register("silent_vote", 125, new StaticVoteRule(Text.translatable("rule.silent_vote")));
//	public static final ReplaceItemModelRule REPLACE_ITEM_MODEL = register("replace_item_model", 1000, new ReplaceItemModelRule());
//	public static final ReplaceBlockModelRule REPLACE_BLOCK_MODEL = register("replace_block_model", 1000, new ReplaceBlockModelRule());

//	public static final EnumVoteRule<AutoJumpAlternative> AUTO_JUMP_ALTERNATIVES = register("auto_jump_alternatives", 500, new EnumVoteRule<>(AutoJumpAlternative.values(), AutoJumpAlternative.OFF, AutoJumpAlternative.CODEC) {
//		protected Text getOptionDescription(AutoJumpAlternative val) {
//			return val.getDisplayName();
//		}
//	});

	public static final StaticVoteRule UNCONTROLLABLE_LAVA = register("uncontrolable_lave", 125, new StaticVoteRule(Text.translatable("rule.uncontrolable_lave")));
	public static final StaticVoteRule MINECART_WHEELS = register("wheels_on_minecarts", 500, new StaticVoteRule(Text.translatable("rule.wheels_on_minecarts")));

	public static final NumberVoteRule.IntegerRule LAVA_SPREAD_DELAY = register("lava_spread_tick_delay", 500, new NumberVoteRule.IntegerRule(30, UniformIntProvider.create(1, 9)) {
		protected Text getOptionDescription(Integer val) {
			return Text.translatable("rule.lava_spread_tick_delay", val);
		}
	});

	public static final StaticVoteRule MIDAS_TOUCH = register("midas_touch", 125, new StaticVoteRule(Text.translatable("rule.midas_touch")));
	public static final ReplaceBlockRule COBBLESTONE_GEN_REPLACE = register("cobblestone_gen_replace", 1000, new ReplaceBlockRule("rule.lava_water_replace", Blocks.COBBLESTONE));
	public static final ReplaceBlockRule STONE_GEN_REPLACE = register("stone_gen_replace", 1000, new ReplaceBlockRule("rule.lava_water_replace", Blocks.STONE));
	public static final ReplaceBlockRule OBSIDIAN_GEN_REPLACE = register("obsidian_gen_replace", 125, new ReplaceBlockRule("rule.lava_water_replace", Blocks.OBSIDIAN));
	public static final ReplaceBlockRule BASALT_GEN_REPLACE = register("basalt_gen_replace", 125, new ReplaceBlockRule("rule.lava_blue_ice_replace", Blocks.BASALT));
	public static final StaticVoteRule ROWING_UP_HILL = register("rowing_up_that_hill", 500, new StaticVoteRule(Text.translatable("rule.rowing_up_that_hill")));
	public static final StaticVoteRule POT_GEMS = register("pot_gems", 500, new StaticVoteRule(Text.translatable("rule.pot_gems")));
	public static final StaticVoteRule DISABLE_SHIELD = register("disable_shield", 500, new StaticVoteRule(Text.translatable("rule.disable_shield")));

	public static final EnumVoteRule<WeatherState> RAIN = register("rain", 1000, new EnumVoteRule<>(WeatherState.values(), WeatherState.DEFAULT, WeatherState.CODEC) {
		protected Text getOptionDescription(WeatherState val) {
			return val.getRainDescription();
		}
	});

	public static final EnumVoteRule<WeatherState> THUNDER = register("thunder", 1000, new EnumVoteRule<>(WeatherState.values(), WeatherState.DEFAULT, WeatherState.CODEC) {
		protected Text getOptionDescription(WeatherState val) {
			return val.getThunderDescription();
		}
	});

	public static final NumberVoteRule.FloatRule GLOBAL_PITCH = register("global_pitch", 1000, new NumberVoteRule.FloatRule(1.0F, ClampedNormalFloatProvider.create(1.5F, 0.6F, 0.3F, 3.0F)) {
		protected Text getOptionDescription(Float val) {
			return Text.translatable("rule.global_pitch", Math.round(val * 100.0F));
		}
	});

//	public static final PermaEffectRule PERMA_EFFECT = register("perma_effect", 1000, new PermaEffectRule());

	public static final NumberVoteRule.FloatRule ITEM_USE_SPEED = register("item_use_speed", 1000, new NumberVoteRule.FloatRule(1.0F, ClampedNormalFloatProvider.create(1.0F, 0.4F, 0.1F, 8.0F)) {
		protected Text getOptionDescription(Float val) {
			return Text.translatable("rule.item_use_speed", Math.round(val * 100.0F));
		}
	});

	public static final NumberVoteRule.FloatRule ATTACK_KNOCKBACK = register("attack_knockback", 1000, new NumberVoteRule.FloatRule(1.0F, ClampedNormalFloatProvider.create(1.0F, 0.4F, 0.1F, 8.0F)) {
		protected Text getOptionDescription(Float val) {
			return Text.translatable("rule.attack_knockback", Math.round(val * 100.0F));
		}
	});

	public static final StaticVoteRule INFINITE_CAKES = register("infinite_cakes", 500, new StaticVoteRule(Text.translatable("rule.infinite_cakes")));
	public static final StaticVoteRule GOD_OF_LIGHTNING = register("god_of_lightning", 125, new StaticVoteRule(Text.translatable("rule.god_of_lightning")));
	public static final StaticVoteRule MORROWIND_MOVEMENT = register("morrowind_power_player_movement", 125, new StaticVoteRule(Text.translatable("rule.morrowind_power_player_movement")));
	public static final StaticVoteRule EVIL_EYE = register("evil_eye", 125, new StaticVoteRule(Text.translatable("rule.evil_eye")));
	public static final StaticVoteRule BIG_HEADS = register("big_head_mode", 1000, new StaticVoteRule(Text.translatable("rule.big_heads")));
	public static final StaticVoteRule FLOATING_HEADS = register("floating_head_mode", 1000, new StaticVoteRule(Text.translatable("rule.floating_heads")));
	public static final StaticVoteRule TRANSPARENT_PLAYERS = register("transparent_players", 500, new StaticVoteRule(Text.translatable("rule.transparent_players")));

	public static final EnumVoteRule<CapesState> CAPES = register("caep", 1000, new EnumVoteRule<>(CapesState.values(), CapesState.NONE, CapesState.CODEC) {
		protected Text getOptionDescription(CapesState val) {
			return val.getDisplayName();
		}
	});

	public static final StaticVoteRule MINI_PLAYERS = register("minime", 500, new StaticVoteRule(Text.translatable("rule.mini_players")));
	public static final StaticVoteRule MILK_EVERY_MOB = register("milk_every_mob", 500, new StaticVoteRule(Text.translatable("rule.milk_every_mob")));
	public static final StaticVoteRule FRENCH_MODE = register("french_mode", 500, new StaticVoteRule(Text.translatable("rule.french_mode")));
	public static final StaticVoteRule MBE = register("mbe", 500, new StaticVoteRule(Text.translatable("rule.mbe")));
	public static final StaticVoteRule STICKY = register("sticky", 500, new StaticVoteRule(Text.translatable("rule.sticky")));
	public static final StaticVoteRule BUTTONS_ON_THINGS = register("buttons_on_things", 1000, new StaticVoteRule(Text.translatable("rule.buttons_on_things")));

	public static final NumberVoteRule.IntegerRule PUSH_LIMIT = register("push_limit", 1000, new NumberVoteRule.IntegerRule(12, UniformIntProvider.create(0, 23)) {
		protected Text getOptionDescription(Integer val) {
			return Text.translatable("rule.push_limit", val);
		}
	});

	public static final StaticVoteRule FIRE_SPONGE = register("fire_sponge", 1000, new StaticVoteRule(Text.translatable("rule.fire_sponge")));
	public static final StaticVoteRule PERSISTENT_PARROTS = register("persistent_parrots", 1000, new StaticVoteRule(Text.translatable("rule.persistent_parrots")));
	public static final ThreadLocal<Boolean> INTERACTION_UPDATES = ThreadLocal.withInitial(() -> true);
	public static final StaticVoteRule LESS_INTERACTION_UPDATES = register("less_interaction_updates", 125, new StaticVoteRule(Text.translatable("rule.less_interaction_updates")));
	public static final StaticVoteRule DEAD_BUSH_RENEWABILITY = register("dead_bush_renewability", 1000, new StaticVoteRule(Text.translatable("rule.dead_bush_renewability")));
	public static final StaticVoteRule FOG_OFF = register("fog_off", 500, new StaticVoteRule(Text.translatable("rule.fog_off")));
	public static final StaticVoteRule FIX_QC = register("fix_qc", 500, new StaticVoteRule(Text.translatable("rule.fix_qc")));
	public static final StaticVoteRule FAST_HOPPERS = register("fast_hoppers", 500, new StaticVoteRule(Text.translatable("rule.fast_hoppers")));
	public static final StaticVoteRule LESS_GRAVITY = register("less_gravity", 125, new StaticVoteRule(Text.translatable("rule.less_gravity")));
	public static final StaticVoteRule BOUNCY_CASTLE = register("bouncy_castle", 1000, new StaticVoteRule(Text.translatable("rule.bouncy_castle")));
	public static final StaticVoteRule AIR_BLOCKS = register("air_blocks", 1000, new StaticVoteRule(Text.translatable("rule.air_blocks")));
	public static final StaticVoteRule DRINK_AIR = register("drink_air", 1000, new StaticVoteRule(Text.translatable("rule.drink_air")));

	public static final ReplaceItemRule BOTTLE_OF_VOID_REPLACE = register("replace_items_with_bottle_of_void", 500, new ReplaceItemRule((reg, rand) -> Optional.of(Items.GLASS_BOTTLE)));
	public static final BigMoonRule BIG_MOON = register("big_moon", 500, new BigMoonRule());
	public static final StaticVoteRule OBFUSCATE_NAMES = register("obfuscate_player_names", 500, new StaticVoteRule(Text.translatable("rule.obfuscate_player_names")));
	public static final StaticVoteRule BETA_ENTITY_IDS = register("beta_entity_ids", 500, new StaticVoteRule(Text.translatable("rule.beta_entity_ids")));
	public static final TheJokeRule THE_JOKE = register("the_joke", 500, new TheJokeRule());

//	public static final EnumVoteRule<NameVisibility> NORMAL_NAME_VISIBILITY = register("normal_name_visibility", 1000, new EnumVoteRule<>(NameVisibility.values(), NameVisibility.SEE_THROUGH, NameVisibility.CODEC) {
//		protected Text getOptionDescription(NameVisibility val) {
//			return Text.translatable("rule.normal_name_visibility", val.getDisplayName());
//		}
//	});
//
//	public static final EnumVoteRule<NameVisibility> SNEAKING_NAME_VISIBILITY = register("sneaking_name_visibility", 1000, new EnumVoteRule<>(NameVisibility.values(), NameVisibility.NORMAL, NameVisibility.CODEC) {
//		protected Text getOptionDescription(NameVisibility val) {
//			return Text.translatable("rule.sneaking_name_visibility", val.getDisplayName());
//		}
//	});

	public static final StaticVoteRule ENTITY_COLLISIONS = register("entity_collisions", 500, new StaticVoteRule(Text.translatable("rule.entity_collisions")));
	public static final StaticVoteRule DAY_BEDS = register("day_beds", 500, new StaticVoteRule(Text.translatable("rule.day_beds")));
	public static final StaticVoteRule PICKAXE_BLOCK = register("pickaxe_block", 1000, new StaticVoteRule(Text.translatable("rule.pickaxe_block")));
	public static final StaticVoteRule PLACE_BLOCK = register("place_block", 1000, new StaticVoteRule(Text.translatable("rule.place_block")));
//	public static final ParentTrapRule PARENT_TRAP = register("parent_trap", 500, new ParentTrapRule());
	public static final StaticVoteRule GLOW_BEES = register("glow_bees", 1000, new StaticVoteRule(Text.translatable("rule.glow_bees")));

//	public static final EnumVoteRule<FlailingLevel> FLAILING = register("flailing_level", 1000, new EnumVoteRule<>(FlailingLevel.values(), FlailingLevel.NORMAL, FlailingLevel.CODEC) {
//		protected Text getOptionDescription(FlailingLevel val) {
//			return val.getDisplayName();
//		}
//	});
//
//	public static final EnumVoteRule<RecipeFlip> RECIPE_FLIP = register("recipe_flip", 1000, new EnumVoteRule<>(RecipeFlip.values(), RecipeFlip.BOTH, RecipeFlip.CODEC) {
//		protected Text getOptionDescription(RecipeFlip val) {
//			return val.getDisplayName();
//		}
//	});

	public static final EntityVoteRule AI_ATTACK = register("ai_attack", 500, new EntityVoteRule() {
//		protected Text getOptionDescription(EntityReference entry) {
//			return Text.translatable("rule.ai_attack", entry.displayName());
//		}

		@Override
		protected Text getElementDescription(EntityReference element) {
			return null;
		}
	});

//	public static final EntityVoteRule PRESIDENT = register("president", 1000, new EntityVoteRule() {
//		protected Text getOptionDescription(EntityReference entry) {
//			return Text.translatable("rule.president", entry.displayName());
//		}
//
//		@Override
//		protected boolean apply(EntityReference entry) {
//			this.getActiveEntries().forEach(this::deactivate);
//			return super.apply(entry);
//		}
//
//		@Override
//		protected Text getElementDescription(EntityReference element) {
//			return null;
//		}
//	});

	public static final CopySkinRule COPY_SKIN = register("copy_skin", 1000, new CopySkinRule());
//
//	public static final EnumVoteRule<ItemDespawnStrategy> ITEM_DESPAWN = register("item_despawn", 125, new EnumVoteRule<>(ItemDespawnStrategy.values(), ItemDespawnStrategy.DESPAWN_ALL, ItemDespawnStrategy.CODEC) {
//		protected Text getOptionDescription(ItemDespawnStrategy val) {
//			return val.getDisplayName();
//		}
//	});
//
//	public static final ItemDespawnTimeRule ITEM_DESPAWN_TIME = register("item_despawn_time", 1000, new ItemDespawnTimeRule());
//	public static final DayLengthRule DAY_LENGTH = register("day_length", 1000, new DayLengthRule());
	public static final StaticVoteRule BEDS_ON_BANNERS = register("beds_on_banners", 500, new StaticVoteRule(Text.translatable("rule.beds_on_banners")));

	public static final EnumVoteRule<FoodRestriction> FOOD_RESTRICTION = register("food_restriction", 1000, new EnumVoteRule<>(FoodRestriction.values(), FoodRestriction.ANY, FoodRestriction.CODEC) {
		protected Text getOptionDescription(FoodRestriction val) {
			return Text.translatable("rule.food_restriction." + val.asString());
		}
	});

//	public static final CodepointStyleRule CODEPOINT_STYLE = register("codepoint_style", 1000, new CodepointStyleRule());
//	public static final CodepointReplaceRule CODEPOINT_REPLACE = register("codepoint_replace", 1000, new CodepointReplaceRule());
//	public static final OptimizeRule OPTIMIZE = register("optimize", 500, new OptimizeRule());
//	public static final BinaryGameruleRule BINARY_GAMERULE = register("binary_gamerule_rule", 1000, new BinaryGameruleRule());
//	public static final IntegerGameruleRule INTEGER_GAMERULE = register("integer_gamerule_rule", 1000, new IntegerGameruleRule());

//	public static final RegistryEntryVoteRule<EntityType<?>> DINNERBONIZE = register("dinnerbonize", 1000, new RegistryEntryVoteRule<EntityType<?>>("entity", RegistryKeys.ENTITY_TYPE) {
//		@Override
//		protected Text getLabel(Text name) {
//			return Text.translatable("rule.dinnerbonize", name);
//		}
//	});
//
//	public static final RegistryEntryVoteRule<EntityType<?>> GRUMMIZE = register("grummize", 1000, new RegistryEntryVoteRule<EntityType<?>>("entity", RegistryKeys.ENTITY_TYPE) {
//		@Override
//		protected Text getLabel(Text name) {
//			return Text.translatable("rule.grummize", name);
//		}
//	});

//	public static final GiveItemRule GIVE_ITEM = register("give_item", 1000, new GiveItemRule());
//
//	public static final DyeColorVoteRule SHEEP_COLOR = register("default_sheep_color", 500, new DyeColorVoteRule(DyeColor.WHITE) {
//		protected Text getOptionDescription(DyeColor color) {
//			return Text.translatable("rule.default_sheep_color", Text.translatable("color.minecraft." + color.getName()));
//		}
//	});

	public static final StaticVoteRule FLINTSPLODER = register("flintsploder", 500, new StaticVoteRule(Text.translatable("rule.flintsploder")));
	public static final StaticVoteRule FIX_PISTON = register("fix_piston", 125, new StaticVoteRule(Text.translatable("rule.fix_piston")));
	public static final StaticVoteRule PLAYER_HEAD_DROP = register("player_head_drop", 500, new StaticVoteRule(Text.translatable("rule.player_head_drop")));
	public static final StaticVoteRule CHARGED_CREEPERS = register("charged_creepers", 500, new StaticVoteRule(Text.translatable("rule.charged_creepers")));
//	public static final EggFreeRule EGG_FREE = register("egg_free", 1000, new EggFreeRule());
//	public static final VillagerGemRule VILLAGER_GEM = register("villager_gem", 1000, new VillagerGemRule());
	public static final StaticVoteRule UNSTABLE_TNT = register("unstable_tnt", 500, new StaticVoteRule(Text.translatable("rule.unstable_tnt")));
	public static final StaticVoteRule TNT_TENNIS = register("tnt_tennis", 500, new StaticVoteRule(Text.translatable("rule.tnt_tennis")));
	public static final StaticVoteRule UNDEAD_PLAYERS = register("undead_players", 125, new StaticVoteRule(Text.translatable("rule.undead_players")));
	public static final StaticVoteRule HAUNTED_WORLD = register("haunted_world", 500, new StaticVoteRule(Text.translatable("rule.haunted_world")));
//	public static final ExplosionPowerRule EXPLOSION_POWER = register("explosion_power", 500, new ExplosionPowerRule());
//	public static final ReplaceRegistryRule REPLACE_LOOT = register("replace_loot_drop", 1000, new ReplaceRegistryRule("rule.replace_loot_drop"));
//	public static final DoubleHalfRule LOOT_DOUBLE_HALF = register("loot_double_or_half", 1000, new DoubleHalfRule("rule.loot_double_or_half", -4, 4));
//	public static final ReplaceRegistryRule REPLACE_RECIPE = register("replace_recipe_output", 1000, new ReplaceRegistryRule("rule.replace_recipe_output"));
//	public static final DoubleHalfRule RECIPE_DOUBLE_HALF = register("recipe_double_or_half", 1000, new DoubleHalfRule("rule.recipe_double_or_half", -4, 4));
//	public static final DoubleHalfRule STACK_SIZE_DOUBLE_HALF = register("stack_size_double_or_half", 1000, new DoubleHalfRule("rule.stack_size_double_or_half", -6, 4));
//	public static final DamageModifierRule DAMAGE_MODIFIER = register("damage_modifier", 1000, new DamageModifierRule("rule.damage_modifier", -3, 10));
//	public static final InflammabilityRule INFLAMMABILITY = register("inflammability", 1000, new InflammabilityRule());
	public static final StaticVoteRule MINECART_LIES = register("minecart_lies", 500, new StaticVoteRule(Text.translatable("rule.minecart_lies")));
//	public static final Vote WIP_RULE = register("wipwipwi-_-pwipwip", 1, new WIPRule());
	public static final StaticVoteRule SWAP_SKY = register("swap_sky", 500, new StaticVoteRule(Text.translatable("rule.swap_skies")));

//	public static final RegistryEntryVoteRule<EntityType<?>> SPAWN_DISABLE = register("natural_spawn_disable", 1000, new RegistryEntryVoteRule<EntityType<?>>("entity", RegistryKeys.ENTITY_TYPE) {
//		@Override
//		protected Text getLabel(Text name) {
//			return Text.translatable("rule.natural_spawn_disable", name);
//		}
//	});

//	public static final SpawnReplacementRule SPAWN_REPLACEMENT = register("natural_spawn_replacement", 1000, new SpawnReplacementRule());
//	public static final SoundReplaceRule SOUND_REPLACE = register("sound_replace", 1000, new SoundReplaceRule());

	public static final EnumVoteRule<CollisionStrategy> MINECART_COLLISIONS = register("minecart_collisions", 125, new EnumVoteRule<>(CollisionStrategy.values(), CollisionStrategy.NONE, CollisionStrategy.CODEC) {
		protected Text getOptionDescription(CollisionStrategy val) {
			return Text.translatable("rule.minecart_collisions." + val.asString());
		}
	});

	public static final EnumVoteRule<CollisionStrategy> BOAT_COLLISIONS = register("boat_collisions", 125, new EnumVoteRule<>(CollisionStrategy.values(), CollisionStrategy.NONE, CollisionStrategy.CODEC) {
		protected Text getOptionDescription(CollisionStrategy val) {
			return Text.translatable("rule.boat_collisions." + val.asString());
		}
	});

//	public static final BiomeColorRule GRASS_COLOR = register("biome_grass_color", 1000, new BiomeColorRule("rule.biome_color.grass"));
//	public static final BiomeColorRule FOLIAGE_COLOR = register("biome_foliage_color", 1000, new BiomeColorRule("rule.biome_color.foliage"));
//	public static final BiomeColorRule SKY_COLOR = register("biome_sky_color", 1000, new BiomeColorRule("rule.biome_color.sky"));
//	public static final BiomeColorRule WATER_COLOR = register("biome_water_color", 1000, new BiomeColorRule("rule.biome_color.water"));
//	public static final BiomeColorRule FOG_COLOR = register("biome_fog_color", 1000, new BiomeColorRule("rule.biome_color.fog"));
//	public static final BiomeColorRule WATER_FOG_COLOR = register("biome_water_fog_color", 1000, new BiomeColorRule("rule.biome_color.water_fog"));
	public static final StaticVoteRule RUBIES = register("rubies", 500, new StaticVoteRule(Text.translatable("rule.rubies")));
//	public static final TransformScaleRule TRANSFORM_SCALE = register("transform_scale", 1000, new TransformScaleRule());
//	public static final TransformEntityRule TRANSFORM_ENTITY = register("transform_entity", 125, new TransformEntityRule());
	public static final StaticVoteRule ULTRA_REALISTIC = register("ultra_realistic_mode", 500, new StaticVoteRule(Text.translatable("rule.ultra_realistic_mode")));
	public static final StaticVoteRule REMOVE_PHANTOMS = register("remove_phantoms", 125, new StaticVoteRule(Text.translatable("rule.remove_phantoms")));
	public static final StaticVoteRule PHANTOM_PHANTOM = register("phantom_phantom", 500, new StaticVoteRule(Text.translatable("rule.phantom_phantom")));
	public static final ReplaceItemRule REPLACE_ITEMS = register("replace_items", 1000, new ReplaceItemRule((reg, rand) -> reg.getRandom(rand).map(RegistryEntry::value)));
	public static final StaticVoteRule DREAM_MODE = register("dream_mode", 500, new StaticVoteRule(Text.translatable("rule.dream_mode")));
	public static final StaticVoteRule INSTACHEESE = register("instacheese", 500, new StaticVoteRule(Text.translatable("rule.instacheese")));
	public static final StaticVoteRule UNIVERSAL_JEB = register("universal_jeb", 500, new StaticVoteRule(Text.translatable("rule.universal_jeb")));
	public static final StaticVoteRule WORLD_OF_GIANTS = register("world_of_giants", 125, new StaticVoteRule(Text.translatable("rule.world_of_giants")));
	public static final StaticVoteRule RAY_TRACING = register("ray_tracing", 125, new StaticVoteRule(Text.translatable("rule.ray_tracing")));

//	public static final DyeColorVoteRule COLORED_LIGHT = register("colored_light", 125, new DyeColorVoteRule(DyeColor.WHITE) {
//		protected Text getOptionDescription(DyeColor color) {
//			return Text.translatable("rule.colored_light", Text.translatable("color.minecraft." + color.getName()));
//		}
//	});

	public static final StaticVoteRule GLOWING_GLOW_SQUIDS = register("glowing_glow_squids", 500, new StaticVoteRule(Text.translatable("rule.glowing_glow_squids")));
	public static final StaticVoteRule BEDROCK_SHADOWS = register("bedrock_shadows", 1000, new StaticVoteRule(Text.translatable("rule.bedrock_shadows")));
	public static final StaticVoteRule ALWAYS_FLYING = register("always_flying", 125, new StaticVoteRule(Text.translatable("rule.always_flying")));
	public static final StaticVoteRule COPPER_SINK = register("copper_sink", 500, new StaticVoteRule(Text.translatable("rule.copper_sink")));
	public static final StaticVoteRule BED_PVP = register("bed_pvp", 125, new StaticVoteRule(Text.translatable("rule.bed_pvp")));
	public static final StaticVoteRule NBT_CRAFTING = register("nbt_crafting", 500, new StaticVoteRule(Text.translatable("rule.nbt_crafting")));
	public static final StaticVoteRule POTIONS_OF_BIG = register("potions_of_big", 1000, new StaticVoteRule(Text.translatable("rule.potions_of_big")));
	public static final StaticVoteRule POTIONS_OF_SMALL = register("potions_of_small", 1000, new StaticVoteRule(Text.translatable("rule.potions_of_small")));
	public static final StaticVoteRule KEEP_FRIENDS_CLOSE = register("keep_friends_close", 125, new StaticVoteRule(Text.translatable("rule.keep_friends_close")));
	public static final StaticVoteRule NO_FLOATING_TREES = register("prevent_floating_trees", 500, new StaticVoteRule(Text.translatable("rule.prevent_floating_trees")));
	public static final StaticVoteRule RANDOM_TNT_FUSE = register("random_tnt_fuse", 500, new StaticVoteRule(Text.translatable("rule.random_tnt_fuse")));
	public static final StaticVoteRule EXPLODING_PHANTOMS = register("exploding_phantoms", 125, new StaticVoteRule(Text.translatable("rule.exploding_phantoms")));
	public static final StaticVoteRule BUFF_FISHING = register("buff_fishing", 500, new StaticVoteRule(Text.translatable("rule.buff_fishing")));
	public static final StaticVoteRule ZOMBIE_APOCALYPSE = register("zombie_apocalypse", 125, new StaticVoteRule(Text.translatable("rule.zombie_apocalypse")));

	public static final NumberVoteRule.IntegerRule DUPE_OCCURRENCE = register("dupe_hack_occurrence_chance", 500, new NumberVoteRule.IntegerRule(0, ClampedNormalIntProvider.of(30.0F, 30.0F, 0, 500)) {
		protected Text getOptionDescription(Integer val) {
			return Text.translatable("rule.dupe_hack_occurrence_chance", val);
		}
	});

	public static final NumberVoteRule.IntegerRule DUPE_BREAK = register("dupe_hack_break_chance", 500, new NumberVoteRule.IntegerRule(30, ClampedNormalIntProvider.of(30.0F, 30.0F, 0, 100)) {
		protected Text getOptionDescription(Integer val) {
			return Text.translatable("rule.dupe_hack_break_chance", val);
		}
	});

	public static final NumberVoteRule.IntegerRule SPAWN_EGG_CHANCE = register("spawn_egg_chance", 500, new NumberVoteRule.IntegerRule(0, ClampedNormalIntProvider.of(10.0F, 30.0F, 0, 100)) {
		protected Text getOptionDescription(Integer val) {
			return Text.translatable("rule.spawn_egg_chance", val);
		}
	});

//	public static final EnumVoteRule<LightEngineOptimization> OPTIMIZE_LIGHT = register("optimize_light_engine", 125, new EnumVoteRule<>(LightEngineOptimization.values(), LightEngineOptimization.NONE, LightEngineOptimization.CODEC) {
//		protected Text getOptionDescription(LightEngineOptimization val) {
//			return val.getDisplayName();
//		}
//	});

//	public static final RegistryEntryVoteRule<EntityType<?>> RIDEABLE_ENTITIES = register("rideable_entities", 1000, new RegistryEntryVoteRule<EntityType<?>>("entity", RegistryKeys.ENTITY_TYPE) {
//		@Override
//		protected Text getLabel(Text name) {
//			return Text.translatable("rule.rideable_entities", name);
//		}
//	});

	public static final StaticVoteRule ENDERMEN_PICK_ANYTHING = register("endermen_pick_up_anything", 500, new StaticVoteRule(Text.translatable("rule.endermen_pick_up_anything")));
	public static final StaticVoteRule ENDERMEN_BLOCK_UPDATE = register("endermen_block_update", 500, new StaticVoteRule(Text.translatable("rule.endermen_block_update")));
	public static final StaticVoteRule VOTING_FIREWORKS = register("voting_fireworks", 500, new StaticVoteRule(Text.translatable("rule.voting_fireworks")));
	public static final StaticVoteRule SNITCH = register("snitch", 500, new StaticVoteRule(Text.translatable("rule.snitch")));
	public static final StaticVoteRule GRAPPLING_RODS = register("grappling_fishing_rods", 500, new StaticVoteRule(Text.translatable("rule.grappling_fishing_rods")));
	public static final StaticVoteRule BEELOONS = register("beeloons", 1000, new StaticVoteRule(Text.translatable("rule.beeloons")));
	public static final StaticVoteRule FISH_ANYTHING = register("fish_anything", 500, new StaticVoteRule(Text.translatable("rule.fish_anything")));
	public static final StaticVoteRule ONLY_MENDING = register("only_mending_trades", 500, new StaticVoteRule(Text.translatable("rule.only_mending_trades")));
	public static final StaticVoteRule TRAILS_AND_TAILS = register("trails_and_tails", 500, new StaticVoteRule(Text.translatable("rule.trails_and_tails")));

//	private static final DataPool<RegistryEntry.Reference<Vote>> FINAL_POOL = POOL_BUILDER.build();
	private static final WeightedList<Reference<Vote>> FINAL_POOL = new WeightedList<RegistryEntry.Reference<Vote>>();

	private static ClampedIntProvider createClampedPercentProvider(int padding) {
		return ClampedIntProvider.create(UniformIntProvider.create(-padding, 100 + padding), 0, 100);
	}

	private static <R extends Vote> R register(String name, int weight, R rule) {
		Reference<Vote> ref = Registry.registerReference(VoteRegistries.VOTE_RULE_TYPE, Identifier.of(name), rule);
		POOL_BUILDER.add((Reference<Vote>)(Object)ref, weight);
		return rule;
	}

	public static final Vote getDefaultRule(Registry<Vote> registry) {
		return TEST_RULE;
	}

	public static final RegistryEntry.Reference<Vote> getRandomRule(Random random) {
//		return FINAL_POOL.getDataOrEmpty(random).orElseThrow();
		FINAL_POOL.shuffle();
		return FINAL_POOL.stream()
		    .findFirst()
		    .orElseThrow();
	}

	public static final RegistryEntry.Reference<Vote> pickRule(Random random) {
//		return FINAL_POOL.getDataOrEmpty(random).orElseThrow();
		FINAL_POOL.shuffle();
		return FINAL_POOL.stream()
		    .findFirst()
		    .orElseThrow();
	}
	
	public static final void init() {
		LOGGER.info("Initializing vote rules");
	}
}
