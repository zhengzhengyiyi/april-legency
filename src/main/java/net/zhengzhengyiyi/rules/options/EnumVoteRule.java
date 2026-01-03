package net.zhengzhengyiyi.rules.options;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.world.Vote;
import net.zhengzhengyiyi.vote.VoteValue;
import net.zhengzhengyiyi.vote.VoterAction;
import net.zhengzhengyiyi.world.Vote;

public abstract class EnumVoteRule<T> implements Vote {
    private final List<T> possibleValues;
    final T defaultValue;
    T currentValue;
    private final Codec<VoteValue> optionCodec;

    public EnumVoteRule(T[] values, T defaultValue, Codec<T> codec) {
        this(Arrays.asList(values), defaultValue, codec);
    }

    public EnumVoteRule(List<T> values, T defaultValue, Codec<T> codec) {
        this.possibleValues = values.stream().filter(v -> !defaultValue.equals(v)).toList();
        this.defaultValue = defaultValue;
        this.currentValue = defaultValue;
        this.optionCodec = codec.xmap(
            val -> new EnumVoteRule.Option(val),
            option -> (T) ((EnumVoteRule<?>.Option) option).value
        );
    }

    public T getValue() {
        return this.currentValue;
    }

    @Override
    public Stream getActiveOptions() {
        return !Objects.equals(this.currentValue, this.defaultValue) 
            ? Stream.of(new EnumVoteRule.Option(this.currentValue)) 
            : Stream.empty();
    }

    @Override
    public Stream generateOptions(MinecraftServer server, Random random, int limit) {
        ObjectArrayList<T> list = new ObjectArrayList<>(this.possibleValues);
        Util.shuffle(list, random);
        return list.stream()
            .filter(v -> !Objects.equals(v, this.defaultValue))
            .limit(limit)
            .map(v -> new EnumVoteRule.Option(v));
    }

    protected abstract Text getOptionDescription(T value);

    @Override
    public Codec getOptionCodec() {
        return this.optionCodec;
    }

    class Option implements VoteValue {
        final T value;
        private final Text description;

        Option(T value) {
            this.value = value;
            this.description = EnumVoteRule.this.getOptionDescription(value);
        }

        @Override
        public void apply(VoterAction action) {
            EnumVoteRule.this.currentValue = switch (action) {
                case APPROVE -> this.value;
                case REPEAL -> EnumVoteRule.this.defaultValue;
			default -> null;
            };
        }

		 

		@Override
		public Text getDescription(VoterAction action) {
			return null;
		}
    }
}
