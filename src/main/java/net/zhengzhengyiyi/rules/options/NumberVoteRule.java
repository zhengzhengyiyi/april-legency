package net.zhengzhengyiyi.rules.options;

import com.mojang.serialization.Codec;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.world.Vote;
import net.zhengzhengyiyi.vote.VoteValue;
import net.zhengzhengyiyi.vote.VoterAction;

public abstract class NumberVoteRule<T extends Number> implements Vote {
    final T defaultValue;
    private final Function<Random, T> valueProvider;
    T currentValue;
    private final Codec<VoteValue> optionCodec;

    @SuppressWarnings("unchecked")
	public NumberVoteRule(T defaultValue, Function<Random, T> valueProvider, Codec<T> codec) {
        this.valueProvider = valueProvider;
        this.defaultValue = defaultValue;
        this.currentValue = defaultValue;
//        this.optionCodec = Codec.unit(defaultValue).xmap(v -> new NumberVoteRule.Option(defaultValue), VoteValue::getRule); 
        this.optionCodec = (Codec<VoteValue>)(Object)Codec.EMPTY.xmap(
       	    v -> new NumberVoteRule.Option(this.defaultValue), 
       	    option -> null
        ).codec();
    }

    public T getValue() {
        return this.currentValue;
    }

    @Override
    public Stream getActiveOptions() {
        return !Objects.equals(this.currentValue, this.defaultValue) ? Stream.of(new NumberVoteRule.Option(this.currentValue)) : Stream.empty();
    }

    @SuppressWarnings("unchecked")
	@Override
    public Stream generateOptions(MinecraftServer server, Random random, int limit) {
        return Stream.generate(() -> (T)this.valueProvider.apply(random))
            .filter(number -> !Objects.equals(number, this.defaultValue))
            .limit(limit)
            .distinct()
            .map(number -> new NumberVoteRule.Option(number));
    }

    protected abstract Text getOptionDescription(T number);

    @Override
    public Codec getOptionCodec() {
        return this.optionCodec;
    }

    public abstract static class FloatRule extends NumberVoteRule<Float> {
        public FloatRule(float defaultValue, FloatProvider provider) {
            super(defaultValue, provider::get, Codec.FLOAT);
        }
    }

    public abstract static class IntegerRule extends NumberVoteRule<Integer> {
        public IntegerRule(int defaultValue, IntProvider provider) {
            super(defaultValue, provider::get, Codec.INT);
        }
    }

    class Option implements VoteValue {
        final T value;
//        private final Text description;

        Option(T value) {
            this.value = value;
//            this.description = NumberVoteRule.this.getOptionDescription(value);
        }

        @Override
        public void apply(VoterAction action) {
            NumberVoteRule.this.currentValue = (T)(switch (action) {
                case APPROVE -> this.value;
                case REPEAL -> NumberVoteRule.this.defaultValue;
			default -> null;
            });
        }

		@Override
		public Vote getType() {
			return (Vote)NumberVoteRule.this;
		}

		@Override
		public Text getDescription(VoterAction action) {
			return Text.of("text");
		}
    }
}
