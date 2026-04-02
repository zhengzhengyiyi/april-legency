package net.zhengzhengyiyi.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class FireflyBushBlock extends PlantBlock implements Fertilizable {
   private static final double FIREFLY_CHANCE = 0.7;
   private static final double FIREFLY_HORIZONTAL_RADIUS = 10.0;
   private static final double FIREFLY_VERTICAL_RADIUS = 5.0;
   private static final int LIGHT_LEVEL_THRESHOLD = 13;
   private static final int IDLE_SOUND_CHANCE = 30;
   public static final MapCodec<FireflyBushBlock> CODEC = createCodec(FireflyBushBlock::new);

   public FireflyBushBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   @Override
   protected MapCodec<? extends FireflyBushBlock> getCodec() {
      return CODEC;
   }

   @Override
   public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
      if (random.nextInt(IDLE_SOUND_CHANCE) == 0 && world.isNight() && world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) <= pos.getY()) {
         world.playSound(null, pos, SoundEvents.BLOCK_GRASS_STEP, SoundCategory.BLOCKS, 1.0F, 1.0F);
      }

      if (world.getLightLevel(pos) <= LIGHT_LEVEL_THRESHOLD && random.nextDouble() <= FIREFLY_CHANCE) {
         double x = pos.getX() + random.nextDouble() * FIREFLY_HORIZONTAL_RADIUS - FIREFLY_HORIZONTAL_RADIUS / 2.0;
         double y = pos.getY() + random.nextDouble() * FIREFLY_VERTICAL_RADIUS;
         double z = pos.getZ() + random.nextDouble() * FIREFLY_HORIZONTAL_RADIUS - FIREFLY_HORIZONTAL_RADIUS / 2.0;
         world.addParticleClient(ParticleTypes.GLOW, x, y, z, 0.0, 0.0, 0.0);
      }
   }

   @Override
   public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
      return true;
   }

   @Override
   public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
      return true;
   }

   @Override
   public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
      // Spread to nearby positions
      for (int i = 0; i < 4; i++) {
         BlockPos targetPos = pos.add(
            random.nextInt(3) - 1,
            random.nextInt(2) - random.nextInt(2),
            random.nextInt(3) - 1
         );
         
         if (world.isAir(targetPos) && this.canPlaceAt(state, world, targetPos)) {
            world.setBlockState(targetPos, this.getDefaultState());
         }
      }
   }
}
