package net.zhengzhengyiyi.generator.generation;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap.Type;
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

public class ColorGridChunkGenerator extends ChunkGenerator {
    public static final MapCodec<ColorGridChunkGenerator> CODEC = BiomeSource.CODEC.fieldOf("biome_source")
            .xmap(ColorGridChunkGenerator::new, generator -> generator.biomeSource);

    public ColorGridChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    private static int sanitizeCoord(int i) {
        return i & 2147483647;
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structures) {
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Blender blender, NoiseConfig noiseConfig, StructureAccessor structures, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int i = 0; i < 8; i++) {
            int j = sanitizeCoord(chunkPos.x) ^ i ^ sanitizeCoord(chunkPos.z);
            Block[] blocks = ColorBlockLists.ALL_COLORED_BLOCKS[j % ColorBlockLists.ALL_COLORED_BLOCKS.length];

            for (int k = 0; k < 16; k++) {
                for (int l = 0; l < 16; l++) {
                    for (int m = 0; m < 16; m++) {
                        int y = 16 * i + l;
                        int pattern = k ^ y ^ m;
                        chunk.setBlockState(mutable.set(k, y, m), blocks[pattern % blocks.length].getDefaultState(), 1);
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(chunk);
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
    public int getSeaLevel() {
        return 63;
    }

	@Override
	public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess,
			StructureAccessor structureAccessor, Chunk chunk) {
	}

	@Override
	public void populateEntities(ChunkRegion region) {
	}

	@Override
	public int getHeight(int x, int z, Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
		return 256;
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
		return null;
	}

	@Override
	public void appendDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
	}
}
