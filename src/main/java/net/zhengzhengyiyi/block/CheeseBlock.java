package net.zhengzhengyiyi.block;

import java.util.Optional;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class CheeseBlock extends Block {
   public static final int CUBE_COUNT = 8;
   public static final int MAX_SLICES = 255;
   public static final IntProperty SLICES = IntProperty.of("slices", 1, 255);
   public static final VoxelShape[] CUBE = Util.make(new VoxelShape[8], voxelShapes -> {
      voxelShapes[0] = VoxelShapes.cuboid(0.0, 0.0, 0.0, 0.5, 0.5, 0.5);
      voxelShapes[1] = VoxelShapes.cuboid(0.5, 0.0, 0.0, 1.0, 0.5, 0.5);
      voxelShapes[2] = VoxelShapes.cuboid(0.0, 0.0, 0.5, 0.5, 0.5, 1.0);
      voxelShapes[3] = VoxelShapes.cuboid(0.5, 0.0, 0.5, 1.0, 0.5, 1.0);
      voxelShapes[4] = VoxelShapes.cuboid(0.0, 0.5, 0.0, 0.5, 1.0, 0.5);
      voxelShapes[5] = VoxelShapes.cuboid(0.5, 0.5, 0.0, 1.0, 1.0, 0.5);
      voxelShapes[6] = VoxelShapes.cuboid(0.0, 0.5, 0.5, 0.5, 1.0, 1.0);
      voxelShapes[7] = VoxelShapes.cuboid(0.5, 0.5, 0.5, 1.0, 1.0, 1.0);
   });
   public static final VoxelShape[] SHAPES_BY_SLICE = Util.make(new VoxelShape[256], voxelShapes -> {
      for (int i = 0; i < voxelShapes.length; i++) {
         VoxelShape voxelShape = VoxelShapes.empty();

         for (int j = 0; j < 8; j++) {
            if (hasCube(i, j)) {
               voxelShape = VoxelShapes.union(voxelShape, CUBE[j]);
            }
         }

         voxelShapes[i] = voxelShape.simplify();
      }
   });
   @SuppressWarnings("unused")
   private static final int INFITE = -1;

   protected CheeseBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState(this.stateManager.getDefaultState().with(SLICES, 255));
   }

   private static boolean hasCube(int slices, int cubeIndex) {
      return (slices & getCubeBit(cubeIndex)) != 0;
   }

   private static int getCubeBit(int cubeIndex) {
      return 1 << cubeIndex;
   }

   private static int removeCube(int slices, int cubeIndex) {
      return slices & ~getCubeBit(cubeIndex);
   }

   private static boolean isFull(BlockState blockState) {
      return blockState.get(SLICES) == 255;
   }

   @Override
   public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if (!player.getStackInHand(player.getActiveHand()).isEmpty()) {
         return ActionResult.FAIL;
      } else {
         Vec3d vec3d = hit.getPos().subtract(pos.getX(), pos.getY(), pos.getZ());
         int cubeIndex = getClosestCube(state, vec3d);
         if (cubeIndex == -1) {
            return ActionResult.FAIL;
         } else {
            int newSlices = removeCube(state.get(SLICES), cubeIndex);
            if (newSlices != 0) {
               world.setBlockState(pos, state.with(SLICES, newSlices));
            } else {
               world.removeBlock(pos, false);
               world.emitGameEvent(player, GameEvent.BLOCK_DESTROY, pos);
            }

            if (!world.isClient()) {
               world.syncWorldEvent(2010, pos, cubeIndex);
               player.getHungerManager().add(1, 0.1F);
               if (player.getAir() < player.getMaxAir()) {
                  player.setAir(player.getAir() + 10);
               }

               world.emitGameEvent(player, GameEvent.EAT, pos);
               player.getHungerManager().add(2, 0.1F);
            }

            return ActionResult.SUCCESS;
         }
      }
   }

   private static int getClosestCube(BlockState blockState, Vec3d vec3d) {
      int slices = blockState.get(SLICES);
      double minDistance = Double.MAX_VALUE;
      int closestCube = -1;

      for (int k = 0; k < CUBE.length; k++) {
         if (hasCube(slices, k)) {
            VoxelShape voxelShape = CUBE[k];
            Optional<Vec3d> optional = voxelShape.getClosestPointTo(vec3d);
            if (optional.isPresent()) {
               double distance = optional.get().squaredDistanceTo(vec3d);
               if (distance < minDistance) {
                  minDistance = distance;
                  closestCube = k;
               }
            }
         }
      }

      return closestCube;
   }

   @Override
   public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPES_BY_SLICE[state.get(SLICES)];
   }

   @Override
   public boolean hasSidedTransparency(BlockState state) {
      return true;
   }

   @Override
   public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
      return isFull(state) ? 0.2F : 1.0F;
   }

   @Override
   protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
      builder.add(SLICES);
   }
}
