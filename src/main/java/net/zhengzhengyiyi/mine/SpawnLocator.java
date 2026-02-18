package net.zhengzhengyiyi.mine;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

public enum SpawnLocator implements StringIdentifiable {
   SURFACE("surface", world -> {
      BlockPos spawnPos = world.getSpawnPoint().getPos();
      int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, spawnPos);
      return spawnPos.withY(topY).toBottomCenterPos();
   }),
   CAVE("cave", world -> {
      BlockPos spawnPos = world.getSpawnPoint().getPos();
      int minY = world.getBottomY();
      int maxY = world.getTopYInclusive();
      BlockPos centerPos = spawnPos.withY((minY + maxY) / 2);
      BlockPos.Mutable mutable = centerPos.mutableCopy();

      while (mutable.getY() > minY && !isSafeSpawn(world, mutable)) {
         mutable.move(Direction.DOWN);
      }

      if (mutable.getY() > minY) {
         return mutable.toBottomCenterPos();
      } else {
         mutable.set(centerPos);

         while (mutable.getY() < maxY && !isSafeSpawn(world, mutable)) {
            mutable.move(Direction.UP);
         }

         return mutable.getY() < maxY ? mutable.toBottomCenterPos() : centerPos.toBottomCenterPos();
      }
   });

   public static final Codec<SpawnLocator> CODEC = StringIdentifiable.createCodec(SpawnLocator::values);
   private final String id;
   private final Function<ServerWorld, Vec3d> locator;

   private SpawnLocator(String id, Function<ServerWorld, Vec3d> locator) {
      this.id = id;
      this.locator = locator;
   }

   public Vec3d getSpawnPos(ServerWorld world) {
      return this.locator.apply(world);
   }

   private static boolean isSafeSpawn(ServerWorld world, BlockPos pos) {
      BlockPos floorPos = pos.down();
      return world.getBlockState(pos).getCollisionShape(world, pos).isEmpty()
         && world.getBlockState(floorPos).isSideSolidFullSquare(world, floorPos, Direction.UP);
   }

   @Override
   public String asString() {
      return this.id;
   }
}
