package net.zhengzhengyiyi.generator.generation;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class class_5097 extends ChunkGenerator {
    private static final Logger field_23560 = LogManager.getLogger();
    private static final byte[] field_23561 = new byte[]{
    		72,
    	      -71,
    	      33,
    	      -116,
    	      61,
    	      25,
    	      -105,
    	      61,
    	      69,
    	      -34,
    	      22,
    	      -96,
    	      83,
    	      -41,
    	      4,
    	      100,
    	      49,
    	      -120,
    	      -76,
    	      -32,
    	      -24,
    	      105,
    	      -103,
    	      57,
    	      -101,
    	      -101,
    	      114,
    	      39,
    	      45,
    	      -48,
    	      -58,
    	      106,
    	      -83,
    	      72,
    	      -120,
    	      -98,
    	      14,
    	      111,
    	      -73,
    	      38,
    	      -43,
    	      -29,
    	      -17,
    	      -64,
    	      -48,
    	      -21,
    	      -63,
    	      -14,
    	      7,
    	      -65,
    	      -115,
    	      61,
    	      -62,
    	      -121,
    	      108,
    	      -2,
    	      24,
    	      84,
    	      -62,
    	      117,
    	      115,
    	      52,
    	      -88,
    	      18,
    	      30,
    	      115,
    	      44,
    	      -113,
    	      -123,
    	      88,
    	      77,
    	      -122,
    	      15,
    	      -13,
    	      123,
    	      85,
    	      -118,
    	      -12,
    	      -30,
    	      7,
    	      104,
    	      -75,
    	      -84,
    	      -57,
    	      -124,
    	      -113,
    	      -38,
    	      84,
    	      52,
    	      6,
    	      -94,
    	      67,
    	      76,
    	      59,
    	      105,
    	      82,
    	      92,
    	      -65,
    	      -52,
    	      -26,
    	      -46,
    	      45,
    	      94,
    	      47,
    	      10,
    	      -14,
    	      -86,
    	      -12,
    	      1,
    	      111,
    	      -107,
    	      -119,
    	      -115,
    	      32,
    	      -60,
    	      -92,
    	      107,
    	      -2,
    	      73,
    	      109,
    	      -128,
    	      -107,
    	      -52,
    	      -7,
    	      -6,
    	      126,
    	      98,
    	      -71,
    	      -92,
    	      12,
    	      -41,
    	      83,
    	      -124,
    	      14,
    	      -51,
    	      -15,
    	      4,
    	      -3,
    	      -65,
    	      -36,
    	      99,
    	      63,
    	      119,
    	      64,
    	      46,
    	      21,
    	      10,
    	      30,
    	      -23,
    	      -10,
    	      -90,
    	      -36,
    	      -4,
    	      -106,
    	      -102,
    	      84,
    	      -17,
    	      58,
    	      59,
    	      -76,
    	      -103,
    	      -28,
    	      -95,
    	      4,
    	      112,
    	      18,
    	      3,
    	      -78,
    	      125,
    	      -79,
    	      11,
    	      120,
    	      -59,
    	      -64,
    	      -37,
    	      -47,
    	      19,
    	      -21,
    	      90,
    	      -9,
    	      -65,
    	      109,
    	      70,
    	      -83,
    	      -4,
    	      34,
    	      41,
    	      -109,
    	      27,
    	      -20,
    	      29,
    	      60,
    	      109,
    	      -117,
    	      74,
    	      -112,
    	      -58,
    	      76,
    	      96,
    	      9,
    	      -65,
    	      86,
    	      63,
    	      62,
    	      112,
    	      -88,
    	      96,
    	      -35,
    	      64,
    	      57,
    	      35,
    	      89,
    	      -24,
    	      -40,
    	      121,
    	      106,
    	      -102,
    	      -103,
    	      -24,
    	      -73,
    	      103,
    	      -110,
    	      56,
    	      97,
    	      -82,
    	      55,
    	      -53,
    	      -100,
    	      22,
    	      -68,
    	      104,
    	      8,
    	      98,
    	      -120,
    	      -65,
    	      -30,
    	      38,
    	      114,
    	      -59,
    	      30,
    	      66,
    	      -119,
    	      59,
    	      -93,
    	      107,
    	      -50,
    	      115,
    	      40,
    	      80,
    	      77,
    	      -61,
    	      -102,
    	      -62,
    	      -110,
    	      -80,
    	      -85,
    	      19,
    	      123,
    	      -120,
    	      70,
    	      -119,
    	      11,
    	      63,
    	      30,
    	      92,
    	      73,
    	      81,
    	      -19,
    	      -14,
    	      122,
    	      -103,
    	      -108,
    	      38,
    	      -116,
    	      -100,
    	      50,
    	      -121,
    	      -7,
    	      -125,
    	      61,
    	      -44,
    	      -38,
    	      -117,
    	      16,
    	      14,
    	      -101,
    	      79,
    	      -96,
    	      89,
    	      12,
    	      84,
    	      -36,
    	      42,
    	      -21,
    	      -109,
    	      -7,
    	      117,
    	      64,
    	      38,
    	      18,
    	      -97,
    	      -58,
    	      73,
    	      2,
    	      41,
    	      70,
    	      -85,
    	      75,
    	      6,
    	      123,
    	      76,
    	      -66,
    	      53,
    	      -41,
    	      25,
    	      -14,
    	      -104,
    	      -19,
    	      67,
    	      -28,
    	      -9,
    	      -111,
    	      59,
    	      -109,
    	      35,
    	      57,
    	      108,
    	      100,
    	      40,
    	      116,
    	      -106,
    	      -128,
    	      2,
    	      109,
    	      -75,
    	      3,
    	      19,
    	      87,
    	      -120,
    	      59,
    	      -20,
    	      -15,
    	      74,
    	      -40,
    	      106,
    	      -3,
    	      -122,
    	      19,
    	      -94,
    	      53,
    	      -103,
    	      -60,
    	      -36,
    	      2,
    	      52,
    	      31,
    	      63,
    	      17,
    	      -32,
    	      -61,
    	      -116,
    	      5,
    	      9,
    	      117,
    	      -72,
    	      -28,
    	      -125,
    	      99,
    	      -54,
    	      -126,
    	      96,
    	      21,
    	      29,
    	      38,
    	      35,
    	      90,
    	      -32,
    	      89,
    	      48,
    	      108,
    	      10,
    	      -52,
    	      -117,
    	      2,
    	      -74,
    	      -122,
    	      -21,
    	      119,
    	      126,
    	      -110,
    	      -115,
    	      57,
    	      -119,
    	      -53,
    	      43,
    	      -128,
    	      10,
    	      97,
    	      122,
    	      126,
    	      -111,
    	      103,
    	      113,
    	      90,
    	      101,
    	      44,
    	      9,
    	      5,
    	      102,
    	      88,
    	      -24,
    	      -108,
    	      -8,
    	      42,
    	      65,
    	      46
    };
    private static final byte[] field_23562 = new byte[]{-114, 123, -36, 36, 6, 2, 31, 116, -76, -125, -62, -61, -41, -121, 82, -106};
    
    private final String[] field_23564;

    public class_5097(BiomeSource biomeSource, String seedString) {
        super(biomeSource);
        this.field_23564 = method_26571(seedString);
    }

    private static String[] method_26571(String string) {
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec keySpec = new PBEKeySpec(string.toCharArray(), "pinch_of_salt".getBytes(StandardCharsets.UTF_8), 65536, 128);
            SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(field_23562);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] bs = cipher.doFinal(field_23561);
            return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bs)).toString().split("\n");
        } catch (Exception var8) {
            field_23560.warn("No.", var8);
            return new String[]{"Uh uh uh! You didn't say the magic word!"};
        }
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> getCodec() {
        return null;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int i = chunkPos.x * 2;
        int j = chunkPos.z * 2;
        method_26569(chunk, j, i, 0, 0);
        method_26569(chunk, j, i, 1, 0);
        method_26569(chunk, j, i, 0, 1);
        method_26569(chunk, j, i, 1, 1);
        return CompletableFuture.completedFuture(chunk);
    }

    private void method_26569(Chunk chunk, int i, int j, int k, int l) {
        int m = j + k;
        int n = i + l;
        if (n >= 0 && n < this.field_23564.length) {
            String string = this.field_23564[n];
            if (m >= 0 && m < string.length()) {
                char c = string.charAt(m);
                if (c != ' ') {
                    for (int y = 0; y < 16; y++) {
                        chunk.setBlockState(new BlockPos(xInChunk(k), 20 + y, zInChunk(l)), Blocks.GRASS_BLOCK.getDefaultState(), 2);
                    }
                }
            }
        }
    }

    private int xInChunk(int k) { return k * 8; }
    private int zInChunk(int l) { return l * 8; }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
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
        return 0;
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        BlockState[] states = new BlockState[world.getHeight()];
        Arrays.fill(states, Blocks.AIR.getDefaultState());
        return new VerticalBlockSample(world.getBottomY(), states);
    }

    @Override
    public void appendDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
    }

	@Override
	public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess,
			StructureAccessor structureAccessor, Chunk chunk) {
		
	}
}
