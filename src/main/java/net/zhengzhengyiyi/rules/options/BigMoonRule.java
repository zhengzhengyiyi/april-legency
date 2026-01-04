package net.zhengzhengyiyi.rules.options;

import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.world.Vote;
import net.zhengzhengyiyi.vote.VoteValue;
import net.zhengzhengyiyi.vote.VoterAction;

public class BigMoonRule extends NumberVoteRule.IntegerRule {
    public static final int MAX_LEVEL = 3;

    public BigMoonRule() {
        super(0, ConstantIntProvider.create(1));
    }

    public double getMoonScale() {
        return (double)this.currentValue / 3.0;
    }

    public boolean isMoonEnlarged() {
        return this.getMoonScale() != 0.0;
    }

    @Override
    protected Text getOptionDescription(Integer number) {
        int currentLevel = Math.min(this.currentValue, 3);
        int nextLevel = Math.min(number, 3);
        
        return nextLevel > currentLevel 
            ? Text.translatable("rule.moon." + nextLevel) 
            : Text.translatable("rule.moon." + currentLevel);
    }

    @Override
    public Stream<VoteValue> generateOptions(MinecraftServer server, Random random, int limit) {
        if (this.currentValue >= MAX_LEVEL) {
            return Stream.empty();
        }
//        
        return Stream.of(this.createIncrementalOption(1));
//    	return null;
    }
    
    protected VoteValue createIncrementalOption(int delta) {
        return new VoteValue() {
            @Override
            public void apply(VoterAction action) {
                BigMoonRule.this.currentValue = this.getNextValue(action);
            }

            private int getNextValue(VoterAction action) {
                return action == VoterAction.APPROVE ? BigMoonRule.this.currentValue + delta : BigMoonRule.this.currentValue - delta;
            }

			@Override
			public Vote getType() {
				return BigMoonRule.this;
			}

			@Override
			public Text getDescription(VoterAction action) {
				return null;
			}
        };
    }
}
