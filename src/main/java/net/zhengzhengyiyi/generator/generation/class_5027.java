package net.zhengzhengyiyi.generator.generation;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.text.Text;
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

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class class_5027 extends ChunkGenerator {
    public static final MapCodec<class_5027> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.biomeSource)
            ).apply(instance, class_5027::new)
    );

    public class_5027(BiomeSource biomeSource) {
        super(biomeSource);
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                chunk.setBlockState(mutable.set(i, 0, j), Blocks.WHITE_CONCRETE.getDefaultState(), 2);
            }
        }

        if (chunkPos.x == 0 && chunkPos.z == 0) {
            chunk.setBlockState(mutable.set(0, 1, 0), Blocks.DIRT.getDefaultState(), 2);
            
            BlockPos signPos = new BlockPos(0, 3, 0);
            chunk.setBlockState(signPos, Blocks.ACACIA_WALL_SIGN.getDefaultState(), 2);
            SignBlockEntity signBlockEntity = new SignBlockEntity(signPos, Blocks.ACACIA_WALL_SIGN.getDefaultState());
            SignText frontText = signBlockEntity.getFrontText();

	         frontText = frontText.withMessage(0, Text.empty());
	         frontText = frontText.withMessage(1, Text.literal("PATIENCE"));
	         frontText = frontText.withMessage(2, Text.empty());
	         frontText = frontText.withMessage(3, Text.empty());
	
	         signBlockEntity.setText(frontText, true);
            chunk.setBlockEntity(signBlockEntity);
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
        return 1;
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        return new VerticalBlockSample(world.getBottomY(), new net.minecraft.block.BlockState[0]);
    }

    @Override
    public void appendDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
    }

    @Override
    public int getSpawnHeight(HeightLimitView world) {
        return 30;
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
