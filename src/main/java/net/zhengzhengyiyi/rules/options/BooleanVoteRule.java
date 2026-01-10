package net.zhengzhengyiyi.rules.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.vote.VoteValue;
import net.zhengzhengyiyi.vote.VoterAction;
import net.zhengzhengyiyi.world.Vote;

public class BooleanVoteRule implements Vote {
    final Text description;
    boolean currentValue;
    private final BooleanVoteRule.Option option = new BooleanVoteRule.Option();
    private final Codec<VoteValue> optionCodec = RecordCodecBuilder.create(instance -> instance.point(this.option));

    public BooleanVoteRule(Text text) {
        this.description = text;
    }

    public boolean getValue() {
        return this.currentValue;
    }

    @Override
    public Stream getActiveOptions() {
        return this.currentValue ? Stream.of(this.option) : Stream.empty();
    }

    @Override
    public Stream generateOptions(MinecraftServer server, Random random, int limit) {
        return !this.currentValue && limit > 0 ? Stream.of(this.option) : Stream.empty();
    }

    @Override
    public Codec getOptionCodec() {
        return this.optionCodec;
    }

    class Option implements VoteValue {
        @Override
        public void apply(VoterAction action) {
            BooleanVoteRule.this.currentValue = (action == VoterAction.APPROVE);
        }

//        @Override
//        public Text getDescription() {
//            return BooleanVoteRule.this.description;
//        }

		 

		@Override
		public Text getDescription(VoterAction action) {
			return Text.of("no description");
		}
    }
}
