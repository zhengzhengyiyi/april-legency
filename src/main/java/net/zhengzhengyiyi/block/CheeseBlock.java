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
   public static final int field_44222 = 8;
   public static final int field_44223 = 255;
   public static final IntProperty field_44224 = IntProperty.of("slices", 1, 255);
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
   public static final VoxelShape[] field_44226 = Util.make(new VoxelShape[256], voxelShapes -> {
      for (int i = 0; i < voxelShapes.length; i++) {
         VoxelShape voxelShape = VoxelShapes.empty();

         for (int j = 0; j < 8; j++) {
            if (method_50856(i, j)) {
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
      this.setDefaultState(this.stateManager.getDefaultState().with(field_44224, 255));
   }

   private static boolean method_50856(int i, int j) {
      return (i & method_50859(j)) != 0;
   }

   private static int method_50859(int i) {
      return 1 << i;
   }

   private static int method_50860(int i, int j) {
      return i & ~method_50859(j);
   }

   private static boolean method_50862(BlockState blockState) {
      return blockState.get(field_44224) == 255;
   }

   @Override
   public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if (!player.getStackInHand(player.getActiveHand()).isEmpty()) {
         return ActionResult.FAIL;
      } else {
         Vec3d vec3d = hit.getPos().subtract(pos.getX(), pos.getY(), pos.getZ());
         int i = method_50857(state, vec3d);
         if (i == -1) {
            return ActionResult.FAIL;
         } else {
            int j = method_50860(state.get(field_44224), i);
            if (j != 0) {
               world.setBlockState(pos, state.with(field_44224, j));
            } else {
               world.removeBlock(pos, false);
               world.emitGameEvent(player, GameEvent.BLOCK_DESTROY, pos);
            }

            if (!world.isClient()) {
               world.syncWorldEvent(2010, pos, i);
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

   private static int method_50857(BlockState blockState, Vec3d vec3d) {
      int i = blockState.get(field_44224);
      double d = Double.MAX_VALUE;
      int j = -1;

      for (int k = 0; k < CUBE.length; k++) {
         if (method_50856(i, k)) {
            VoxelShape voxelShape = CUBE[k];
            Optional<Vec3d> optional = voxelShape.getClosestPointTo(vec3d);
            if (optional.isPresent()) {
               double e = optional.get().squaredDistanceTo(vec3d);
               if (e < d) {
                  d = e;
                  j = k;
               }
            }
         }
      }

      return j;
   }

   @Override
   public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return field_44226[state.get(field_44224)];
   }

   @Override
   public boolean hasSidedTransparency(BlockState state) {
      return true;
   }

   @Override
   public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
      return method_50862(state) ? 0.2F : 1.0F;
   }

   @Override
   protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
      builder.add(field_44224);
   }
}
