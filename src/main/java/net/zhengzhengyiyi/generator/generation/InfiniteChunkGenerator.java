package net.zhengzhengyiyi.generator.generation;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;
import org.joml.Vector3f;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

public class InfiniteChunkGenerator extends ChunkGenerator {
    private final boolean vaperizes;
    private final boolean netherLike;
    private final float cloudHeight;
    private final Vector3f skyColor;
    private final Vector3f fogColor;
    private final Vector3f[] blockLightColors;
    private final Vector3f[] entityLightColors;
    private final ChunkGenerator baseGenerator;
    
    public boolean getNetherLike() {return this.netherLike;}
    public Vector3f getfogColor() {return this.fogColor;}
    public Vector3f[] getblockLightColors() {return this.blockLightColors;}
    public Vector3f[] getentityLightColors() {return this.entityLightColors;}

    public InfiniteChunkGenerator(BiomeSource biomeSource, ChunkGenerator baseGenerator, int seed) {
        super(biomeSource);
        this.baseGenerator = baseGenerator;
        ChunkRandom random = new ChunkRandom(new net.minecraft.util.math.random.CheckedRandom(seed));

        this.vaperizes = random.nextInt(5) == 0;
        this.netherLike = random.nextBoolean();
        this.cloudHeight = random.nextInt(255);
        this.skyColor = createRandomVector(random);
        this.fogColor = createRandomVector(random);
        this.blockLightColors = createNoiseColors(random);
        this.entityLightColors = createRandomColors(random);
    }

    private static Vector3f createRandomVector(ChunkRandom random) {
        return random.nextBoolean()
                ? new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat())
                : new Vector3f(1.0F, 1.0F, 1.0F);
    }

    private Vector3f[] createRandomColors(Random random) {
        int size = random.nextInt(6) + 2;
        Vector3f[] colors = new Vector3f[size];
        for (int i = 0; i < size; i++) {
            colors[i] = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
        }
        return colors;
    }

    private Vector3f[] createNoiseColors(ChunkRandom random) {
        Vector3f[] colors = new Vector3f[256];
        OctavePerlinNoiseSampler n1 = OctavePerlinNoiseSampler.create(random, IntStream.rangeClosed(-3, 0));
        OctavePerlinNoiseSampler n2 = OctavePerlinNoiseSampler.create(random, IntStream.rangeClosed(-2, 4));
        OctavePerlinNoiseSampler n3 = OctavePerlinNoiseSampler.create(random, IntStream.rangeClosed(-5, 0));
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                colors[i * 16 + j] = new Vector3f((float) n1.sample(i, j, 0.0), (float) n2.sample(i, j, 0.0), (float) n3.sample(i, j, 0.0));
            }
        }
        return colors;
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> getCodec() {
        return MapCodec.unit(this);
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess,
			StructureAccessor structureAccessor, Chunk chunk) {
        this.baseGenerator.carve(chunkRegion, seed, noiseConfig, biomeAccess, structureAccessor, chunk);
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
        this.baseGenerator.buildSurface(region, structures, noiseConfig, chunk);
    }

    @Override
    public void populateEntities(ChunkRegion region) {
        this.baseGenerator.populateEntities(region);
    }

    @Override
    public int getWorldHeight() {
        return baseGenerator.getWorldHeight();
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
//        return this.baseGenerator.populateNoise(blender, noiseConfig, structureAccessor, chunk);
    	return this.baseGenerator.populateNoise(blender, noiseConfig, structureAccessor, chunk);
    }

    @Override
    public int getSeaLevel() {
        return baseGenerator.getSeaLevel();
    }

    @Override
    public int getMinimumY() {
        return baseGenerator.getMinimumY();
    }

    @Override
    public int getHeight(int x, int z, Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return baseGenerator.getHeight(x, z, heightmap, world, noiseConfig);
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        return this.baseGenerator.getColumnSample(x, z, world, noiseConfig);
    }

    @Override
    public void appendDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
        this.baseGenerator.appendDebugHudText(text, noiseConfig, pos);
    }

    public float getCloudHeight() { return this.cloudHeight; }
    public Vector3f getSkyColor() { return this.skyColor; }
    public boolean doesWaterVaporize() { return this.vaperizes; }
}
