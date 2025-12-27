package net.zhengzhengyiyi.rules.options;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.vote.VoteRuleType;
import net.zhengzhengyiyi.vote.VoteValue;
import net.zhengzhengyiyi.vote.VoterAction;
import net.zhengzhengyiyi.world.Vote;

public abstract class VoteOptionCountRule implements Vote {
    private final IntProvider minProvider;
    private final IntProvider rangeProvider;
    final UniformIntProvider defaultRange;
    UniformIntProvider currentRange;
    private final Codec<VoteValue> optionCodec;

    protected VoteOptionCountRule(IntProvider minProvider, IntProvider rangeProvider, UniformIntProvider defaultRange) {
        this.defaultRange = defaultRange;
        this.currentRange = defaultRange;
        this.minProvider = minProvider;
        this.rangeProvider = rangeProvider;
        this.optionCodec = Vote.createCodec((Codec)UniformIntProvider.CODEC.xmap(range -> new VoteOptionCountRule.Option(range), opt -> opt.range)
        );
    }

    public IntProvider getCurrentRange() {
        return this.currentRange;
    }

    @Override
    public Codec getOptionCodec() {
        return this.optionCodec;
    }

    @Override
    public Stream getActiveOptions() {
        return this.defaultRange.equals(this.currentRange) ? Stream.empty() : Stream.of(new VoteOptionCountRule.Option(this.currentRange));
    }

    protected abstract Text getOptionDescription(UniformIntProvider range);

    protected static Text formatRange(UniformIntProvider range, IntFunction<String> formatter) {
        return Text.literal("[" + formatter.apply(range.getMin()) + "-" + formatter.apply(range.getMax()) + "]");
    }

    protected static Text formatRange(UniformIntProvider range) {
        return formatRange(range, String::valueOf);
    }

    @Override
    public Stream generateOptions(MinecraftServer server, Random random, int limit) {
        return Stream.generate(() -> {
            int min = this.minProvider.get(random);
            int extra = Math.max(0, this.rangeProvider.get(random));
            int max = min + extra;
            return UniformIntProvider.create(min, max);
        }).limit(limit).map(range -> new VoteOptionCountRule.Option(range));
    }

    class Option implements VoteValue {
        private final Text description;
        final UniformIntProvider range;

        Option(UniformIntProvider range) {
            this.range = range;
            this.description = VoteOptionCountRule.this.getOptionDescription(range);
        }

        @Override
        public void apply(VoterAction action) {
            VoteOptionCountRule.this.currentRange = switch (action) {
                case APPROVE -> this.range;
                case REPEAL -> VoteOptionCountRule.this.defaultRange;
			default -> null;
            };
        }

		@Override
		public VoteRuleType<?> getType() {
			return (VoteRuleType<?>) VoteOptionCountRule.this;
		}

		@Override
		public Text getDescription(VoterAction action) {
			return this.description;
		}
    }
}
