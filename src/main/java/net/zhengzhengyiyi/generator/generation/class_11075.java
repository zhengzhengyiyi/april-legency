package net.zhengzhengyiyi.generator.generation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.entry.RegistryEntry;
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
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

/**
 * class_11075 - Grid-based Chunk Generator
 * Generates terrain in a grid pattern where blocks appear based on position modulo grid size.
 */
public class class_11075 extends ChunkGenerator {
    public static final MapCodec<class_11075> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(g -> g.biomeSource),
            ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(g -> g.settings),
            Codec.INT.fieldOf("grid_size").forGetter(g -> g.gridSize),
            Codec.INT.fieldOf("block_count").forGetter(g -> g.blockCount),
            Codec.INT.fieldOf("y_offset").forGetter(g -> g.yOffset),
            Codec.BOOL.fieldOf("noise_surface").forGetter(g -> g.noiseSurface)
        ).apply(instance, class_11075::new)
    );

    private final RegistryEntry<ChunkGeneratorSettings> settings;
    private final int gridSize;
    private final int blockCount;
    private final int yOffset;
    private final boolean noiseSurface;
    private final NoiseChunkGenerator delegate;

    public class_11075(
        BiomeSource biomeSource,
        RegistryEntry<ChunkGeneratorSettings> settings,
        int gridSize,
        int blockCount,
        int yOffset,
        boolean noiseSurface
    ) {
        super(biomeSource);
        this.settings = settings;
        this.gridSize = gridSize;
        this.blockCount = blockCount;
        this.yOffset = yOffset;
        this.noiseSurface = noiseSurface;
        this.delegate = new NoiseChunkGenerator(biomeSource, settings);
    }

    /** method_69758 - Returns chunk generator settings */
    public RegistryEntry<ChunkGeneratorSettings> method_69758() {
        return this.settings;
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
        if (this.noiseSurface) {
            this.delegate.buildSurface(region, structures, noiseConfig, chunk);
        }
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Blender blender, NoiseConfig noiseConfig, StructureAccessor structures, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int minY = chunk.getBottomY();
        int height = chunk.getHeight();
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int worldX = chunkPos.getStartX() + lx;
                int worldZ = chunkPos.getStartZ() + lz;
                for (int ly = 0; ly < height; ly++) {
                    int worldY = minY + ly;
                    if (method_69759(worldX, worldY, worldZ)) {
                        mutable.set(worldX, worldY, worldZ);
        chunk.setBlockState(mutable, Blocks.STONE.getDefaultState());
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(chunk);
    }

    /** method_69759 - Determines if position should have block based on grid pattern */
    public boolean method_69759(int x, int y, int z) {
        int gx = Math.floorMod(x, this.gridSize);
        int gz = Math.floorMod(z, this.gridSize);
        int gy = Math.floorMod(y - this.yOffset, this.gridSize);
        return gx < this.blockCount || gz < this.blockCount || gy < this.blockCount;
    }

    /** method_69763 - Creates chunk noise sampler (delegates to noise generator) */
    public NoiseChunkGenerator method_69763() {
        return this.delegate;
    }

    /** method_69771 - Creates vertical block sample with block at Y level */
    public static VerticalBlockSample method_69771(int minY, int height, int blockY, BlockState state) {
        BlockState[] states = new BlockState[height];
        for (int i = 0; i < height; i++) {
            states[i] = (minY + i == blockY) ? state : Blocks.AIR.getDefaultState();
        }
        return new VerticalBlockSample(minY, states);
    }

    /** method_69772 - Creates empty vertical block sample */
    public static VerticalBlockSample method_69772(int minY, int height) {
        BlockState[] states = new BlockState[height];
        java.util.Arrays.fill(states, Blocks.AIR.getDefaultState());
        return new VerticalBlockSample(minY, states);
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk) {}

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structures) {}

    @Override
    public void populateEntities(ChunkRegion region) {}

    @Override
    public int getMinimumY() {
        return this.settings.value().generationShapeConfig().minimumY();
    }

    @Override
    public int getWorldHeight() {
        return this.settings.value().generationShapeConfig().height();
    }

    @Override
    public int getSeaLevel() {
        return this.settings.value().seaLevel();
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return world.getTopYInclusive();
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        return method_69772(world.getBottomY(), world.getHeight());
    }

    @Override
    public void appendDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {}
}
