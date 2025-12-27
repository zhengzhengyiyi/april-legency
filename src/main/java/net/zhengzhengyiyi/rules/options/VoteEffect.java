package net.zhengzhengyiyi.rules.options;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.vote.VoteValue;
import net.zhengzhengyiyi.vote.VoterAction;
import net.zhengzhengyiyi.world.Vote;

public abstract class VoteEffect implements Vote {
    static final Text UNKNOWN_TEXT = Text.literal("???");

    @Override
    public Stream getActiveOptions() {
        return Stream.empty();
    }

    public abstract class Option implements VoteValue {
//        @Override
//        public Vote getRule() {
//            return VoteEffect.this;
//        }

        protected abstract Text getDescriptionText();

//        @Override
//        public Text getDisplayMessage(VoterAction action) {
//            return switch (action) {
//                case REPEAL -> VoteEffect.UNKNOWN_TEXT;
//                case APPROVE -> this.getDescriptionText();
//            };
//        }

        public abstract void run(MinecraftServer server);

//        @Override
//        public void applyWithServer(VoterAction action, MinecraftServer server) {
//            VoteValue.super.applyWithServer(action, server);
//            if (action == VoterAction.APPROVE) {
//                this.run(server);
//            }
//        }

        @Override
        public void apply(VoterAction action) {
        }
    }

    public abstract static class Mixed extends VoteEffect.Weighted {
        protected abstract Optional<VoteValue> selectPrimaryOption(MinecraftServer server, Random random);

        @Override
        public Stream<VoteValue> generateOptions(MinecraftServer server, Random random, int limit) {
            Optional<VoteValue> primary = this.selectPrimaryOption(server, random);
            Stream<VoteValue> weighted = Stream.generate(() -> this.selectRandomOption(server, random)).flatMap(Optional::stream);
            return random.nextBoolean() ? primary.stream().limit(limit) : weighted.limit(limit);
        }
    }

    public abstract static class Weighted extends VoteEffect {
        protected abstract Optional<VoteValue> selectRandomOption(MinecraftServer server, Random random);

        @Override
        public Stream generateOptions(MinecraftServer server, Random random, int limit) {
            return IntStream.range(0, limit * 3)
                .mapToObj(i -> this.selectRandomOption(server, random))
                .flatMap(Optional::stream)
                .limit(limit);
        }
    }
}
