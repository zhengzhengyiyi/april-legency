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
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
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
         
         // Debug logging
         System.out.println("[MiningPortal] Player clicked portal at " + pos);
         System.out.println("[MiningPortal] Dimension key: " + entity.getDimensionKey());
         
         MineServerWorldAccessor mineWorld = (MineServerWorldAccessor)(Object)serverWorld;
         boolean isMine = mineWorld.isMineWorld();
         System.out.println("[MiningPortal] Is mine world: " + isMine);
         
         if (isMine) {
            // inside mine — teleport back to overworld
            System.out.println("[MiningPortal] Teleporting back to overworld");
            ServerWorld overworld = serverWorld.getServer().getOverworld();
            Vec3d spawnPos = overworld.getSpawnPoint().getPos().toCenterPos();
            serverPlayer.teleportTo(new TeleportTarget(overworld, spawnPos, Vec3d.ZERO, serverPlayer.getYaw(), serverPlayer.getPitch(), TeleportTarget.NO_OP));
         } else {
            // in overworld — teleport into the mine dimension
            RegistryKey<World> targetKey = entity.getDimensionKey();
            System.out.println("[MiningPortal] Target dimension: " + targetKey);
            
            ServerWorld targetWorld = serverWorld.getServer().getWorld(targetKey);
            System.out.println("[MiningPortal] Target world exists: " + (targetWorld != null));
            
            if (targetWorld != null) {
               Vec3d spawnPos = targetWorld.getSpawnPoint().getPos().toCenterPos();
               System.out.println("[MiningPortal] Teleporting to: " + spawnPos);
               serverPlayer.teleportTo(new TeleportTarget(targetWorld, spawnPos, Vec3d.ZERO, serverPlayer.getYaw(), serverPlayer.getPitch(), TeleportTarget.ADD_PORTAL_CHUNK_TICKET));
            } else {
               System.out.println("[MiningPortal] ERROR: Target world is null!");
               player.sendMessage(Text.literal("§cError: Mine dimension not found! Try crafting the mine again."), false);
            }
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
      System.out.println("[MiningPortal] Creating portal at " + pos);
      System.out.println("[MiningPortal] Dimension key (DimensionOptions): " + dimensionKey);
      
      world.breakBlock(pos, true, null);
      if (world.setBlockState(pos, ModBlocks.MINING_PORTAL.getDefaultState(), 2)
            && world.getBlockEntity(pos) instanceof TravellingBlockEntity entity) {
         entity.setDimensionKey(dimensionKey);
         entity.setRevisit(revisit);
         entity.markDirty();
         
         System.out.println("[MiningPortal] Portal created successfully");
         System.out.println("[MiningPortal] Stored dimension key: " + entity.getDimensionKey());
      } else {
         System.out.println("[MiningPortal] ERROR: Failed to create portal block entity!");
      }
      world.syncGlobalEvent(1038, pos, 0);
   }
}
