package net.zhengzhengyiyi.rules.options;

// TODO: fix this class

import com.mojang.serialization.Codec;
import java.util.List;
//import java.util.Map;
import java.util.Optional;
//import java.util.stream.Collectors;
import net.minecraft.server.MinecraftServer;
//import net.minecraft.text.Text;
//import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRules;
//import net.zhengzhengyiyi.vote.VoteRuleType;
import net.zhengzhengyiyi.vote.VoteValue;
import net.zhengzhengyiyi.vote.VoterAction;
//import net.zhengzhengyiyi.world.Vote;

public class BinaryGameRuleVote extends VoteEffect.Weighted {
    private final List<GameRule<?>> gameRules = List.of(
        GameRules.FIRE_DAMAGE,
        GameRules.DO_MOB_GRIEFING,
        GameRules.KEEP_INVENTORY,
        GameRules.DO_MOB_SPAWNING,
        GameRules.DO_MOB_LOOT,
        GameRules.DO_TILE_DROPS,
        GameRules.DO_MOB_LOOT,
        GameRules.NATURAL_HEALTH_REGENERATION,
//        GameRules.DO_DAYLIGHT_CYCLE,
        GameRules.SHOW_DEATH_MESSAGES,
        GameRules.REDUCED_DEBUG_INFO,
        GameRules.ELYTRA_MOVEMENT_CHECK,
        GameRules.ADVANCE_WEATHER,
        GameRules.LIMITED_CRAFTING,
        GameRules.ANNOUNCE_ADVANCEMENTS,
        GameRules.DISABLE_RAIDS,
//        GameRules.DO_INSOMNIA,
        GameRules.DO_IMMEDIATE_RESPAWN,
        GameRules.DROWNING_DAMAGE,
        GameRules.FALL_DAMAGE,
        GameRules.FIRE_DAMAGE,
        GameRules.FREEZE_DAMAGE,
        GameRules.SPAWN_PATROLS,
        GameRules.SPAWN_WANDERING_TRADERS,
        GameRules.SPAWN_WARDENS,
        GameRules.FORGIVE_DEAD_PLAYERS,
        GameRules.UNIVERSAL_ANGER,
        GameRules.BLOCK_EXPLOSION_DROP_DECAY,
        GameRules.MOB_EXPLOSION_DROP_DECAY,
        GameRules.TNT_EXPLOSION_DROP_DECAY,
        GameRules.WATER_SOURCE_CONVERSION,
        GameRules.LAVA_SOURCE_CONVERSION,
        GameRules.GLOBAL_SOUND_EVENTS,
        GameRules.SPREAD_VINES
    );
//    private final Map<String, GameRules.Key<GameRules.BooleanRule>> nameToKey = this.gameRules
//        .stream()
//        .collect(Collectors.toMap(GameRules.Key::getName, key -> key));
//    private final Codec<BinaryGameRuleVote.Option> optionCodec = Codec.STRING
//        .comapFlatMap(name -> {
//            GameRules.Key<GameRules.BooleanRule> key = this.nameToKey.get(name);
//            return key == null ? com.mojang.serialization.DataResult.error(() -> "Unknown game rule: " + name) : com.mojang.serialization.DataResult.success(key);
//        }, GameRules.Key::getName)
//        .xmap(BinaryGameRuleVote.Option::new, opt -> opt.ruleKey);
//
//    @Override
//    public Optional<VoteValue> selectRandomOption(MinecraftServer server, Random random) {
//        return Util.getRandomOrEmpty(this.gameRules, random).map(BinaryGameRuleVote.Option::new);
//    }
//
//    @Override
//    public Codec getOptionCodec() {
//        return Vote.createOptionCodec(this.optionCodec);
//    }
//
//    class Option extends VoteEffect.Option {
//        final GameRules.Key<GameRules.BooleanRule> ruleKey;
//        private final Text description;
//
//        Option(GameRules.Key<GameRules.BooleanRule> key) {
//            this.ruleKey = key;
//            this.description = Text.translatable("rule.flip_binary_gamerule", Text.translatable(key.getTranslationKey()));
//        }
//
//        @Override
//        public void run(MinecraftServer server) {
//            GameRule<Boolean> rule = server.getDefaultGameMode().get(this.ruleKey);
//            rule.set(!rule.getDefaultValue(), server);
//        }
//
//		@Override
//		public VoteRuleType<?> getType() {
//			return null;
//		}
//
//		@Override
//		public Text getDescription(VoterAction action) {
//			return null;
//		}
//
//		@Override
//		protected Text getDescriptionText() {
//			return null;
//		}
//    }

	@Override
	public Codec<VoterAction> getOptionCodec() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Optional<VoteValue> selectRandomOption(MinecraftServer server, Random random) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}
}
