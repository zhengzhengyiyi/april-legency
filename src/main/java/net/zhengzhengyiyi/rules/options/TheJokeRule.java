package net.zhengzhengyiyi.rules.options;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.world.Vote;
import net.zhengzhengyiyi.vote.VoterAction;

public class TheJokeRule extends VoteEffect {
    static final MutableText DISPLAY_NAME = Text.translatable("rule.the_joke");
    
    private final VoteEffect.Option option = new VoteEffect.Option() {
        @Override
        protected Text getDescriptionText() {
            return TheJokeRule.DISPLAY_NAME;
        }

        @Override
        public void run(MinecraftServer server) {
            server.getPlayerManager().sendToAll(new WorldEventS2CPacket(1506, BlockPos.ORIGIN, 0, true));
        }

		 

		@Override
		public Text getDescription(VoterAction action) {
			return DISPLAY_NAME;
		}
    };

    @Override
    public Stream generateOptions(MinecraftServer server, Random random, int limit) {
        return limit > 0 ? Stream.of(this.option) : Stream.empty();
    }
}