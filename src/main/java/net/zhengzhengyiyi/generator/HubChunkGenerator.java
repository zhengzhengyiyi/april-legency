package net.zhengzhengyiyi.generator;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.RandomSeed;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

/**
 * Mirrors craftmine's class_11083 — the "minecraft:hub" chunk generator.
 * Registered as "minecraft:hub" in ChunkGenerators.
 * Places hub/center_base, hub/center_hat, hub/corridor_base, hub/corridor_hat NBT structures.
 */
public class HubChunkGenerator extends ChunkGenerator {

    /** Mirrors class_11083.field_59021 — codec using minecraft:hub biome */
    public static final RegistryKey<Biome> HUB_BIOME_KEY =
        RegistryKey.of(RegistryKeys.BIOME, Identifier.ofVanilla("hub"));

    public static final MapCodec<HubChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            RegistryOps.getEntryCodec(HUB_BIOME_KEY)
        ).apply(instance, instance.stable(HubChunkGenerator::new))
    );

    /** Mirrors class_11083.field_59022 */
    public static final Identifier CENTER_BASE  = Identifier.ofVanilla("hub/center_base");
    /** Mirrors class_11083.field_59023 */
    public static final Identifier CENTER_HAT   = Identifier.ofVanilla("hub/center_hat");
    /** Mirrors class_11083.field_59024 */
    public static final Identifier CORRIDOR_BASE = Identifier.ofVanilla("hub/corridor_base");
    /** Mirrors class_11083.field_59025 */
    public static final Identifier CORRIDOR_HAT  = Identifier.ofVanilla("hub/corridor_hat");

    public HubChunkGenerator(RegistryEntry.Reference<Biome> biome) {
        super(new FixedBiomeSource(biome));
    }

    @Override
    protected MapCodec<HubChunkGenerator> getCodec() {
        return CODEC;
    }

    /** Mirrors class_11083.generateFeatures — places hub structures per chunk */
    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        ChunkPos chunkPos = chunk.getPos();
        StructureTemplateManager manager = world.toServerWorld().getStructureTemplateManager();

        if (chunkPos.z >= -1 && chunkPos.z <= 1) {
            HeightLimitView heightLimit = chunk.getHeightLimitView();
            BlockBox blockBox = new BlockBox(
                chunkPos.getStartX(), heightLimit.getBottomY(), chunkPos.getStartZ(),
                chunkPos.getEndX(), heightLimit.getTopYInclusive(), chunkPos.getEndZ()
            );

            if (chunkPos.x >= -1 && chunkPos.x <= 1) {
                BlockPos.Mutable mutable = new BlockPos.Mutable(-16, 0, -16);
                placeVertical(world, manager, blockBox, mutable, CENTER_BASE, CENTER_HAT);
            } else if (chunkPos.x < -1) {
                BlockPos.Mutable mutable = new BlockPos.Mutable(chunkPos.x * 16, 0, -16);
                placeVertical(world, manager, blockBox, mutable, CORRIDOR_BASE, CORRIDOR_HAT);
            }
        }
    }

    /** Mirrors class_11083.method_69799 */
    private void placeVertical(
        StructureWorldAccess world, StructureTemplateManager manager,
        BlockBox blockBox, BlockPos.Mutable mutable,
        Identifier baseId, Identifier hatId
    ) {
        // Build the processor lazily here so ModBlocks.SKY is guaranteed to be initialized
        StructureProcessor bedrockToSky = new RuleStructureProcessor(
            List.of(new StructureProcessorRule(
                new BlockMatchRuleTest(Blocks.BEDROCK),
                AlwaysTrueRuleTest.INSTANCE,
                net.zhengzhengyiyi.block.ModBlocks.SKY.getDefaultState()
            ))
        );
        for (int i = 0; i < world.getTopYInclusive() / 16; i++) {
            Identifier id = (i == 0) ? baseId : hatId;
            placeTemplate(world, mutable, blockBox, manager, id, bedrockToSky);
            mutable.move(Direction.UP, 16);
        }
    }

    /** Mirrors class_11083.method_69801 */
    private static void placeTemplate(
        StructureWorldAccess world, BlockPos pos, BlockBox blockBox,
        StructureTemplateManager manager, Identifier id, StructureProcessor processor
    ) {
        manager.getTemplate(id).ifPresent(template -> {
            ChunkRandom random = new ChunkRandom(new Xoroshiro128PlusPlusRandom(RandomSeed.getSeed()));
            random.setPopulationSeed(world.getSeed(), pos.getX(), pos.getZ());
            template.place(
                world, pos, pos,
                new StructurePlacementData().setBoundingBox(blockBox).addProcessor(processor),
                random, 0
            );
        });
    }

    @Override
    public void carve(ChunkRegion region, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk) {}

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {}

    @Override
    public void populateEntities(ChunkRegion region) {}

    @Override
    public int getWorldHeight() { return 0; }

    @Override
    public CompletableFuture<Chunk> populateNoise(Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getSeaLevel() { return 0; }

    @Override
    public int getMinimumY() { return 0; }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) { return 0; }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        return new VerticalBlockSample(0, new BlockState[0]);
    }

    @Override
    public void appendDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {}
}
