package net.zhengzhengyiyi.rules.options;

// TODO: Implement this class

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.vote.VoterAction;

public class IntegerGameRuleVote extends VoteEffect {

	@Override
	public Codec<VoterAction> getOptionCodec() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<VoterAction> generateOptions(MinecraftServer server, Random random, int limit) {
		// TODO Auto-generated method stub
		return null;
	}
//    private final List<Pair<GameRules.Key<GameRules.IntRule>, IntProvider>> gameRules = List.of(
//        Pair.of(GameRules.SNOW_ACCUMULATION_HEIGHT, UniformIntProvider.create(0, 8)),
//        Pair.of(GameRules.PLAYERS_SLEEPING_PERCENTAGE, ClampedIntProvider.create(UniformIntProvider.create(-20, 120), 0, 100)),
//        Pair.of(GameRules.MAX_ENTITY_CRAMMING, UniformIntProvider.create(0, 100)),
//        Pair.of(GameRules.SPAWN_RADIUS, UniformIntProvider.create(1, 100)),
//        Pair.of(GameRules.RANDOM_TICK_SPEED, UniformIntProvider.create(0, 20))
//    );
//
//    private final Map<String, GameRules.Key<GameRules.IntRule>> nameToKey = this.gameRules
//        .stream()
//        .collect(Collectors.toMap(pair -> pair.getFirst().getName(), Pair::getFirst));
//
//    private final Codec<GameRules.Key<GameRules.IntRule>> ruleKeyCodec = Codec.STRING.comapFlatMap(
//        name -> {
//            GameRules.Key<GameRules.IntRule> key = this.nameToKey.get(name);
//            return key == null ? com.mojang.serialization.DataResult.error(() -> "Unknown int game rule: " + name) : com.mojang.serialization.DataResult.success(key);
//        }, 
//        GameRules.Key::getName
//    );
//
//    private final Codec<IntegerGameRuleVote.Option> optionCodec = RecordCodecBuilder.create(
//        instance -> instance.group(
//                this.ruleKeyCodec.fieldOf("game_rule_id").forGetter(opt -> opt.ruleKey), 
//                Codec.INT.fieldOf("value").forGetter(opt -> opt.value)
//            )
//            .apply(instance, IntegerGameRuleVote.Option::new)
//    );
//
//    @Override
//    public Stream<VoteValue> generateOptions(MinecraftServer server, Random random, int limit) {
//        return Util.getRandomOrEmpty(this.gameRules, random).stream().flatMap(pair -> {
//            GameRules.Key<GameRules.IntRule> key = pair.getFirst();
//            int currentValue = server.getGameRules().get(key).get();
//            IntProvider valueProvider = pair.getSecond();
//            
//            return Stream.generate(() -> valueProvider.get(random))
//                .filter(newValue -> newValue != currentValue)
//                .limit(limit)
//                .map(newValue -> new IntegerGameRuleVote.Option(key, newValue));
//        });
//    }
//
//    @Override
//    public Codec<VoteValue> getOptionCodec() {
//        return Vote.createOptionCodec(this.optionCodec);
//    }
//
//    class Option extends VoteEffect.Option {
//        final GameRules.Key<GameRules.IntRule> ruleKey;
//        final int value;
//        private final Text description;
//
//        Option(GameRules.Key<GameRules.IntRule> key, int value) {
//            this.ruleKey = key;
//            this.value = value;
//            this.description = Text.translatable("rule.change_integer_gamerule", Text.translatable(key.getTranslationKey()), value);
//        }
//
//        @Override
//        protected Text getDescriptionText() {
//            return this.description;
//        }
//
//        @Override
//        public void run(MinecraftServer server) {
//            server.getGameRules().get(this.ruleKey).set(this.value, server);
//        }
//    }
}
