package net.zhengzhengyiyi.feature;

import com.mojang.serialization.Codec;
import java.util.function.Consumer;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class CraterFeature extends Feature<CraterFeatureConfig> {
   public CraterFeature(Codec<CraterFeatureConfig> codec) {
      super(codec);
   }

   @Override
   public boolean generate(FeatureContext<CraterFeatureConfig> context) {
      StructureWorldAccess world = context.getWorld();
      BlockPos origin = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, context.getOrigin()).down();
      Random random = context.getRandom();
      CraterFeatureConfig config = context.getConfig();
      
      int r = config.radius().get(random);
      int d = config.depth().get(random);
      
      if (d > r) {
         return false;
      } else {
         int k = (d * d + r * r) / (2 * d);
         BlockPos centerPos = origin.up(k - d);
         BlockPos.Mutable mutable = origin.mutableCopy();
         
         Consumer<WorldAccess> excavationLogic = worldAccess -> {
            for (int yOffset = -d; yOffset <= k; yOffset++) {
               boolean hasModified = false;

               for (int xOffset = -k; xOffset <= k; xOffset++) {
                  for (int zOffset = -k; zOffset <= k; zOffset++) {
                     mutable.set(origin, xOffset, yOffset, zOffset);
                     if (mutable.getSquaredDistance(centerPos) < k * k && !worldAccess.getBlockState(mutable).isAir()) {
                        hasModified = true;
                        worldAccess.setBlockState(mutable, Blocks.AIR.getDefaultState(), 3);
                     }
                  }
               }

               if (!hasModified && yOffset > 0) {
                  break;
               }
            }
         };

         if (k < 15) {
            excavationLogic.accept(world);
         } else {
            ServerWorld serverWorld = world.toServerWorld();
            serverWorld.getServer().execute(() -> excavationLogic.accept(serverWorld));
         }

         return true;
      }
   }
}
