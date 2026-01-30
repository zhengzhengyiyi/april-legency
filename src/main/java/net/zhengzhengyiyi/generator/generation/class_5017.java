package net.zhengzhengyiyi.generator.generation;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.zhengzhengyiyi.block.ColorBlockLists;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class class_5017 extends ChunkGenerator {
    public static final MapCodec<class_5017> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.biomeSource)
            ).apply(instance, class_5017::new)
    );

    public class_5017(BiomeSource biomeSource) {
        super(biomeSource);
    }

    private static int method_26528(int i) {
        return i & 2147483647;
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int i = 0; i < 8; i++) {
            int j = method_26528(chunkPos.x) ^ i ^ method_26528(chunkPos.z);
            Block[] blocks = ColorBlockLists.ALL_COLORED_BLOCKS[j % ColorBlockLists.ALL_COLORED_BLOCKS.length];

            for (int k = 0; k < 16; k++) {
                for (int l = 0; l < 16; l++) {
                    for (int m = 0; m < 16; m++) {
                        int n = 16 * i + l;
                        int o = k ^ n ^ m;
                        chunk.setBlockState(mutable.set(k, n, m), blocks[o % blocks.length].getDefaultState(), 2);
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structures) {
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return 128;
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        return new VerticalBlockSample(world.getBottomY(), new BlockState[0]);
    }

    @Override
    public void appendDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
    }

    @Override
    public int getSpawnHeight(HeightLimitView world) {
        return 100;
    }

    @Override
    public int getMinimumY() {
        return 0;
    }

    @Override
    public int getWorldHeight() {
        return 256;
    }

	@Override
	public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess,
			StructureAccessor structureAccessor, Chunk chunk) {
	}

	@Override
	public void populateEntities(ChunkRegion region) {		
	}

	@Override
	public int getSeaLevel() {
		return 0;
	}
}