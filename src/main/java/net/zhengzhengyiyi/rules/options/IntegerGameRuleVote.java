package net.zhengzhengyiyi.rules.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.Pair;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.intprovider.ClampedIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRules;
import net.zhengzhengyiyi.vote.VoteValue;
import net.zhengzhengyiyi.vote.VoterAction;
import net.zhengzhengyiyi.world.Vote;

public class IntegerGameRuleVote extends VoteEffect {

//	@Override
//	public Codec<VoteValue> getOptionCodec() {
//		return super.getOptionCodec();
//	}

//	@Override
//	public Stream<VoteValue> generateOptions(MinecraftServer server, Random random, int limit) {
//		return null;
//	}
    private final List<Pair<GameRule<Integer>, IntProvider>> gameRules = List.of(
        Pair.of(GameRules.MAX_SNOW_ACCUMULATION_HEIGHT, UniformIntProvider.create(0, 8)),
        Pair.of(GameRules.PLAYERS_SLEEPING_PERCENTAGE, ClampedIntProvider.create(UniformIntProvider.create(-20, 120), 0, 100)),
        Pair.of(GameRules.MAX_ENTITY_CRAMMING, UniformIntProvider.create(0, 100)),
        Pair.of(GameRules.RESPAWN_RADIUS, UniformIntProvider.create(1, 100)),
        Pair.of(GameRules.RANDOM_TICK_SPEED, UniformIntProvider.create(0, 20))
    );

    private final Map<String, GameRule<Integer>> nameToKey = this.gameRules
        .stream()
        .collect(Collectors.toMap(pair -> pair.first().getTranslationKey(), Pair::first));

    private final Codec<GameRule<Integer>> ruleKeyCodec = Codec.STRING.comapFlatMap(
        name -> {
            GameRule<Integer> key = this.nameToKey.get(name);
            return key == null ? com.mojang.serialization.DataResult.error(() -> "Unknown int game rule: " + name) : com.mojang.serialization.DataResult.success(key);
        }, 
        GameRule::getTranslationKey
    );

    private final Codec<IntegerGameRuleVote.Option> optionCodec = RecordCodecBuilder.create(
        instance -> instance.group(
                this.ruleKeyCodec.fieldOf("game_rule_id").forGetter(opt -> opt.ruleKey), 
                Codec.INT.fieldOf("value").forGetter(opt -> opt.value)
            )
            .apply(instance, IntegerGameRuleVote.Option::new)
    );

    @Override
    public Stream<VoteValue> generateOptions(MinecraftServer server, Random random, int limit) {
        return Util.getRandomOrEmpty(this.gameRules, random).stream().flatMap(pair -> {
            GameRule<Integer> key = pair.first();
            int currentValue = (int)server.getRegistryManager().getOrThrow(RegistryKeys.GAME_RULE).get(key.getId()).getDefaultValue();
            IntProvider valueProvider = pair.second();
            
            return Stream.generate(() -> valueProvider.get(random))
                .filter(newValue -> newValue != currentValue)
                .limit(limit)
                .map(newValue -> new IntegerGameRuleVote.Option(key, newValue));
        });
    }

    @SuppressWarnings("unchecked")
	@Override
    public Codec<VoteValue> getOptionCodec() {
        return (Codec<VoteValue>)Vote.createCodec(this.optionCodec);
    }

    class Option extends VoteEffect.Option {
        final GameRule<Integer> ruleKey;
        final int value;
        private final Text description;

        Option(GameRule<Integer> key, int value) {
            this.ruleKey = key;
            this.value = value;
            this.description = Text.translatable("rule.change_integer_gamerule", Text.translatable(key.getTranslationKey()), value);
        }

        @Override
        protected Text getDescriptionText() {
            return this.description;
        }

		@Override
		public Text getDescription(VoterAction action) {
			return description;
		}

        @Override
        public void run(MinecraftServer server) {
        	var dispatcher = server.getCommandManager().getDispatcher();
        	String command = "gamerule " + ruleKey.toString() + " " + value;

        	var parseResults = dispatcher.parse(command, server.getCommandSource());

        	server.getCommandManager().execute(parseResults, command);
//            server.getGameRules().get(this.ruleKey).set(this.value, server);
        }
    }
}
