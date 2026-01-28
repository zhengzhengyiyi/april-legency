package net.zhengzhengyiyi.generator.generation;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BlueMazeChunkGenerator extends ChunkGenerator {
    private static final BlockState BLUE = Blocks.BLUE_CONCRETE.getDefaultState();
    private static final BlockState LIGHT_BLUE = Blocks.LIGHT_BLUE_CONCRETE.getDefaultState();
    private static final BlockState AIR = Blocks.AIR.getDefaultState();

    private static final BlockState[] PATTERN_A = createPattern(true);
    private static final BlockState[] PATTERN_B = createPattern(false);

    public static final MapCodec<BlueMazeChunkGenerator> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(instance -> 
        instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.biomeSource)
        ).apply(instance, BlueMazeChunkGenerator::new)
    );

    public BlueMazeChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    private static BlockState[] createPattern(boolean typeA) {
        BlockState[] states = new BlockState[64];
        for (int i = 0; i < 64; i++) {
            states[i] = (i % 7 == 0 || i / 8 == 2) ? (typeA ? LIGHT_BLUE : AIR) : (typeA ? AIR : LIGHT_BLUE);
        }
        return states;
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Blender blender, NoiseConfig noiseConfig,
			StructureAccessor structureAccessor, Chunk chunk) {
    	ChunkPos chunkPos = chunk.getPos();
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 16; y++) {
                    chunk.setBlockState(mutable.set(x, y, z), BLUE, 1);
                }
            }
        }

        Random random = Random.create(chunkPos.x * 31L + chunkPos.z);
        generatePattern(chunk, 0, 0, random.nextBoolean() ? PATTERN_A : PATTERN_B);
        generatePattern(chunk, 0, 8, random.nextBoolean() ? PATTERN_A : PATTERN_B);
        generatePattern(chunk, 8, 0, random.nextBoolean() ? PATTERN_A : PATTERN_B);
        generatePattern(chunk, 8, 8, random.nextBoolean() ? PATTERN_A : PATTERN_B);
        
        return CompletableFuture.completedFuture(chunk);
    }

    public void generatePattern(Chunk chunk, int offsetX, int offsetZ, BlockState[] states) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int x = 0; x < 8; x++) {
            for (int z = 0; z < 8; z++) {
                BlockState state = states[x * 8 + z];
                for (int y = 16; y < 32; y++) {
                    chunk.setBlockState(mutable.set(x + offsetX, y, z + offsetZ), state, 1);
                }
            }
        }
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk) {
    }

    @Override
    public void populateEntities(ChunkRegion region) {
    }

    @Override
    public int getWorldHeight() {
        return 384;
    }

    @Override
    public int getSeaLevel() {
        return 63;
    }

    @Override
    public int getMinimumY() {
        return -64;
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return 32;
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        BlockState[] states = new BlockState[32];
        java.util.Arrays.fill(states, BLUE);
        return new VerticalBlockSample(0, states);
    }

    @Override
    public void appendDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
    }

    @Override
    public int getSpawnHeight(HeightLimitView world) {
        return 32;
    }
}
