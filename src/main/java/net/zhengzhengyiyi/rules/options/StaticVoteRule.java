package net.zhengzhengyiyi.rules.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.world.Vote;
import net.zhengzhengyiyi.vote.VoteValue;
import net.zhengzhengyiyi.vote.VoterAction;
import net.zhengzhengyiyi.world.Vote;

public class StaticVoteRule implements Vote {
    final Text description;
    boolean active;
    private final StaticVoteRule.Option option = new StaticVoteRule.Option();
    private final Codec<VoteValue> optionCodec = RecordCodecBuilder.create(instance -> instance.point(this.option));

    public StaticVoteRule(Text description) {
        this.description = description;
    }

    public boolean isActive() {
        return this.active;
    }

    @Override
    public Stream getActiveOptions() {
        return this.active ? Stream.of(this.option) : Stream.empty();
    }

    @Override
    public Stream generateOptions(MinecraftServer server, Random random, int limit) {
        return !this.active && limit > 0 ? Stream.of(this.option) : Stream.empty();
    }

    @Override
    public Codec getOptionCodec() {
        return this.optionCodec;
    }

    class Option implements VoteValue {
        @Override
        public void apply(VoterAction action) {
            StaticVoteRule.this.active = (action == VoterAction.APPROVE);
        }

		@Override
		public Vote getType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Text getDescription(VoterAction action) {
			// TODO Auto-generated method stub
			return null;
		}
    }
}
