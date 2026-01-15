package net.zhengzhengyiyi.rules.options;

import java.util.Objects;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.rules.RegistryEntryVoteRule;
import net.zhengzhengyiyi.vote.VoteValue;
import net.zhengzhengyiyi.world.Vote;

public class ReplaceBlockRule extends RegistryEntryVoteRule<Block> {
    protected final Block defaultBlock;
    private final String id;

	public ReplaceBlockRule(String id, Block block) {
        super(RegistryKeys.BLOCK, block.getRegistryEntry().registryKey());
        this.defaultBlock = block;
        this.id = id;
    }

    public Block getTargetBlock() {
        return this.getCurrentKey() == this.getDefaultKey()
            ? this.defaultBlock
            : (Block)Objects.requireNonNullElse(Registries.BLOCK.get(this.getCurrentKey()), this.defaultBlock);
    }

	@Override
	public Stream<VoteValue> generateOptions(MinecraftServer server, Random random, int limit) {
		return null;
	}

	@Override
	protected Text getOptionDescription(RegistryKey<Block> key) {
		return Text.translatable(id);
	}
}
