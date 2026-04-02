package net.zhengzhengyiyi.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionOptions;
import net.zhengzhengyiyi.accessor.MineServerWorldAccessor;
import org.jetbrains.annotations.Nullable;

public class MiningPortalBlock extends BlockWithEntity {
   public static final MapCodec<MiningPortalBlock> CODEC = createCodec(MiningPortalBlock::new);
   private static final VoxelShape SHAPE = Block.createCubeShape(12.0);

   @Override
   public MapCodec<MiningPortalBlock> getCodec() {
      return CODEC;
   }

   protected MiningPortalBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   @Override
   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new TravellingBlockEntity(pos, state);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
      return validateTicker(type, ModBlocks.TRAVELLING_BLOCK_ENTITY,
         world.isClient() ? TravellingBlockEntity::clientTick : TravellingBlockEntity::serverTick);
   }

   @Override
   public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
      if (world.getBlockEntity(pos) instanceof TravellingBlockEntity entity) {
         int count = entity.getVisibleFaceCount() * 2;
         for (int i = 0; i < count; i++) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();
            double vx = (random.nextDouble() - 0.5) * 0.5;
            double vy = (random.nextDouble() - 0.5) * 0.5;
            double vz = (random.nextDouble() - 0.5) * 0.5;
            int side = random.nextInt(2) * 2 - 1;
            if (random.nextBoolean()) {
               z = pos.getZ() + 0.5 + 0.25 * side;
               vz = random.nextFloat() * 2.0F * side;
            } else {
               x = pos.getX() + 0.5 + 0.25 * side;
               vx = random.nextFloat() * 2.0F * side;
            }
            world.addParticleClient(ParticleTypes.PORTAL, x, y, z, vx, vy, vz);
         }
      }
   }

   @Override
   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if (player instanceof ServerPlayerEntity serverPlayer && world instanceof ServerWorld serverWorld
            && world.getBlockEntity(pos) instanceof TravellingBlockEntity entity) {
         MineServerWorldAccessor mineWorld = (MineServerWorldAccessor)(Object)serverWorld;
         if (entity.isRevisit() && !mineWorld.isMineWorld()) {
            serverWorld.breakBlock(pos, true, null);
         } else if (mineWorld.isMineWorld()) {
            serverWorld.breakBlock(pos, true, null);
         }
      }
      return ActionResult.SUCCESS;
   }

   @Override
   protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
      return ItemStack.EMPTY;
   }

   @Override
   protected boolean canBucketPlace(BlockState state, Fluid fluid) {
      return false;
   }

   @Override
   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPE;
   }

   @Override
   protected boolean isTransparent(BlockState state) {
      return true;
   }

   @Override
   protected BlockRenderType getRenderType(BlockState state) {
      return BlockRenderType.MODEL;
   }

   public static void createPortal(World world, BlockPos pos, RegistryKey<DimensionOptions> dimensionKey, boolean revisit) {
      world.breakBlock(pos, true, null);
      if (world.setBlockState(pos, ModBlocks.MINING_PORTAL.getDefaultState(), 2)
            && world.getBlockEntity(pos) instanceof TravellingBlockEntity entity) {
         entity.setDimensionKey(dimensionKey);
         entity.setRevisit(revisit);
      }
      world.syncGlobalEvent(1038, pos, 0);
   }
}
