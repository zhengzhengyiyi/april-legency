package net.zhengzhengyiyi.rules.options;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.vote.VoteValue;
import net.zhengzhengyiyi.vote.VoterAction;
import net.zhengzhengyiyi.world.Vote;
import org.slf4j.Logger;
import java.util.stream.Stream;

public class HackerVoteRule implements Vote {
    private static final Logger LOGGER = LogUtils.getLogger();
    private boolean active = false;
    private final HackerOption option = new HackerOption();
    public final Codec<VoteValue> codec = RecordCodecBuilder.create(instance -> instance.point(this.option));

    private static final float[] MSG_1 = new float[]{2.32618E-39F, 1.7332302E25F, 7.578229E31F, 7.007856E22F, 4.730713E22F, 4.7414995E30F, 1.8012582E25F};
    private static final float[] MSG_2 = new float[]{1.498926E-39F, 4.631878E27F, 1.6974224E-19F, 7.0081926E28F, 1.7718017E28F};

    @Override
    public Stream<VoteValue> getActiveOptions() {
        return this.active ? Stream.of(this.option) : Stream.empty();
    }

    @Override
    public Stream<VoteValue> generateOptions(MinecraftServer server, Random random, int limit) {
        return !this.active && limit > 0 ? Stream.of(this.option) : Stream.empty();
    }

    static String decode(float[] fs) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        for (float f : fs) {
            out.writeFloat(f);
        }
        byte[] bs = out.toByteArray();
        ByteArrayDataInput in = ByteStreams.newDataInput(bs);
        try {
            return in.readUTF();
        } catch (Exception e) {
            return "???";
        }
    }

    private class HackerOption implements VoteValue {
        @Override
        public Vote getType() {
            return HackerVoteRule.this;
        }

        @Override
        public void apply(VoterAction action) {
            HackerVoteRule.this.active = (action == VoterAction.APPROVE);
        }

        @Override
        public void applyAndBroadcast(VoterAction action, MinecraftServer server) {
            if (!HackerVoteRule.this.active && action == VoterAction.APPROVE) {
                HackerVoteRule.this.active = true;
                LOGGER.error("LOOK AT YOU HACKER", new UnsupportedOperationException());
                Text kickMsg = Text.literal("Nice try");
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    player.networkHandler.disconnect(kickMsg);
                }
            }
        }

        @Override
        public Text getDescription(VoterAction action) {
            return switch (action) {
                case APPROVE -> HackerVoteRule.this.active 
                    ? Text.literal(decode(MSG_1)) 
                    : Text.literal(decode(MSG_2));
                case REPEAL -> Text.literal("You Can Not Redo.");
            };
        }
    }
}
