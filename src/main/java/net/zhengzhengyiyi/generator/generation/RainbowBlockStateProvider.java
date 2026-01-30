package net.zhengzhengyiyi.generator.generation;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

import java.util.List;

public class RainbowBlockStateProvider extends BlockStateProvider {
    public static final MapCodec<RainbowBlockStateProvider> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            BlockState.CODEC.listOf().fieldOf("states").forGetter(provider -> provider.states)
        ).apply(instance, RainbowBlockStateProvider::new)
    );

    private final List<BlockState> states;

    public RainbowBlockStateProvider(List<BlockState> states) {
        this.states = states;
    }

    @Override
    protected BlockStateProviderType<?> getType() {
        return BlockStateProviderType.SIMPLE_STATE_PROVIDER; 
    }

    @Override
    public BlockState get(Random random, BlockPos pos) {
        if (this.states.isEmpty()) {
            return net.minecraft.block.Blocks.AIR.getDefaultState();
        }
        int i = Math.abs(pos.getX() + pos.getY() + pos.getZ());
        return this.states.get(i % this.states.size());
    }
}
