package net.zhengzhengyiyi.block;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Portal;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.BlockView;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.tick.ScheduledTickView;
import net.zhengzhengyiyi.InfiniteDimensionManager;
import net.zhengzhengyiyi.generator.DimensionHasher;

import org.jetbrains.annotations.Nullable;

public class DimensionPortalBlock extends Block implements Portal, AbstractDimensionBlock {
   public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;
   protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
   protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

   public DimensionPortalBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState(this.stateManager.getDefaultState().with(AXIS, Direction.Axis.X));
   }

   @Override
   public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
	   return state.get(AXIS) == Direction.Axis.Z ? Z_SHAPE : X_SHAPE;
   }

   @Override
   public BlockState getStateForNeighborUpdate(
			BlockState state,
			WorldView world,
			ScheduledTickView tickView,
			BlockPos pos,
			Direction direction,
			BlockPos neighborPos,
			BlockState neighborState,
			Random random) {
      Direction.Axis axis = direction.getAxis();
      Direction.Axis portalAxis = state.get(AXIS);
      boolean isHorizontalNeighbor = portalAxis != axis && axis.isHorizontal();
      return !isHorizontalNeighbor && !neighborState.isOf(this) && !new DimensionPortalBlock.AreaHelper((WorldAccess)world, pos, portalAxis, this).wasAlreadyValid()
         ? Blocks.AIR.getDefaultState()
         : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }
   
   public static boolean method_26480(WorldAccess iWorld, BlockPos blockPos, Block block) {
		DimensionPortalBlock.AreaHelper areaHelper = createAreaHelper(iWorld, blockPos, block);
		if (areaHelper != null) {
		areaHelper.createPortal();
		 	return true;
		} else {
			return false;
		}
	}
   
   @Nullable
   public static DimensionPortalBlock.AreaHelper createAreaHelper(WorldAccess iWorld, BlockPos blockPos, Block block) {
      DimensionPortalBlock.AreaHelper areaHelper = new DimensionPortalBlock.AreaHelper(iWorld, blockPos, Direction.Axis.X, block);
      if (areaHelper.isValid() && areaHelper.foundPortalBlocks == 0) {
         return areaHelper;
      } else {
         DimensionPortalBlock.AreaHelper areaHelper2 = new DimensionPortalBlock.AreaHelper(iWorld, blockPos, Direction.Axis.Z, block);
         return areaHelper2.isValid() && areaHelper2.foundPortalBlocks == 0 ? areaHelper2 : null;
      }
   }

   @Override
   public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
      if (entity instanceof ItemEntity itemEntity) {
         ItemStack stack = itemEntity.getStack();
         if (stack.isOf(Items.WRITTEN_BOOK) || stack.isOf(Items.WRITABLE_BOOK)) {
            WrittenBookContentComponent content = stack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
            if (content != null) {
               String bookText = content.pages().stream()
                  .map(page -> page.raw().getString())
                  .collect(Collectors.joining("\n"));

               if (!bookText.isEmpty()) {
                  int dimId = DimensionHasher.hash(bookText);
                  convertPortal(world, pos, state, dimId);
                  entity.discard();
               }
               return;
            }
         }
      }

      if (!entity.hasVehicle() && !entity.hasPassengers() && entity.canUsePortals(false)) {
         entity.tryUsePortal(this, pos);
      }
   }

   public void convertPortal(World world, BlockPos startPos, BlockState state, int dimId) {
      Set<BlockPos> visited = Sets.newHashSet();
      Queue<BlockPos> queue = Queues.newArrayDeque();
      Direction.Axis axis = state.get(AXIS);
      BlockState newPortalState = ModBlocks.NEITHER_PORTAL.getDefaultState().with(AXIS, axis);
      queue.add(startPos);
      while (!queue.isEmpty()) {
         BlockPos current = queue.poll();
         if (visited.add(current) && world.getBlockState(current).isOf(this)) {
            world.setBlockState(current, newPortalState, 18);
            BlockEntity be = world.getBlockEntity(current);
            if (be instanceof NeitherPortalEntity shiny) {
               shiny.setDimensionId(dimId);
               world.updateListeners(current, newPortalState, newPortalState, 3);
               if (!world.isClient())
            	   world.getServer().getPlayerManager().sendToAll(new BlockUpdateS2CPacket(startPos, state));
            }
            for (Direction dir : Direction.values()) {
               queue.add(current.offset(dir));
            }
         }
      }
   }

   @Override
   public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
      if (random.nextInt(100) == 0) {
//         world.playSound(pos, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5F, random.nextFloat() * 0.4F + 0.8F, false);
      }
      for (int i = 0; i < 4; i++) {
         double x = pos.getX() + random.nextDouble();
         double y = pos.getY() + random.nextDouble();
         double z = pos.getZ() + random.nextDouble();
         world.addParticleClient(this.getParticle(state, world, pos), x, y, z, 0, 0, 0);
      }
   }

   protected ParticleEffect getParticle(BlockState state, World world, BlockPos pos) {
      return ParticleTypes.PORTAL;
   }

   @Override
   public BlockState rotate(BlockState state, BlockRotation rotation) {
      if (rotation == BlockRotation.CLOCKWISE_90 || rotation == BlockRotation.COUNTERCLOCKWISE_90) {
         return state.with(AXIS, state.get(AXIS) == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
      }
      return state;
   }

   @Override
   protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
      builder.add(AXIS);
   }

   public static class AreaHelper {
      private final WorldAccess world;
      public final Direction.Axis axis;
      private final Direction negativeDir;
      private final Direction positiveDir;
      private int foundPortalBlocks;
      @Nullable
      private BlockPos lowerCorner;
      private int height;
      private int width;
      private final Block portalBlock;

      public AreaHelper(WorldAccess world, BlockPos pos, Direction.Axis axis, Block portalBlock) {
         this.world = world;
         this.axis = axis;
         this.portalBlock = portalBlock;
         this.positiveDir = axis == Direction.Axis.X ? Direction.EAST : Direction.NORTH;
         this.negativeDir = positiveDir.getOpposite();

         BlockPos current = pos;
         while (pos.getY() > current.getY() - 21 && pos.getY() > world.getBottomY() && validStateInsidePortal(world.getBlockState(pos.down()))) {
            pos = pos.down();
         }

         int dist = distanceToPortalEdge(pos, positiveDir) - 1;
         if (dist >= 0) {
            this.lowerCorner = pos.offset(positiveDir, dist);
            this.width = distanceToPortalEdge(lowerCorner, negativeDir);
            if (width < 2 || width > 21) {
               this.lowerCorner = null;
               this.width = 0;
            }
         }

         if (lowerCorner != null) this.height = findHeight();
      }

      protected int distanceToPortalEdge(BlockPos pos, Direction dir) {
         int i;
         for (i = 0; i < 22; i++) {
            BlockPos bp = pos.offset(dir, i);
            if (!validStateInsidePortal(world.getBlockState(bp)) || !world.getBlockState(bp.down()).isOf(Blocks.OBSIDIAN)) break;
         }
         return world.getBlockState(pos.offset(dir, i)).isOf(Blocks.OBSIDIAN) ? i : 0;
      }

      protected int findHeight() {
         for (this.height = 0; this.height < 21; this.height++) {
            for (int i = 0; i < this.width; i++) {
               BlockPos bp = this.lowerCorner.offset(this.negativeDir, i).up(this.height);
               BlockState bs = this.world.getBlockState(bp);
               if (!validStateInsidePortal(bs)) return height;
               if (bs.isOf(portalBlock)) this.foundPortalBlocks++;
               
               if (i == 0 && !world.getBlockState(bp.offset(positiveDir)).isOf(Blocks.OBSIDIAN)) return height;
               if (i == width - 1 && !world.getBlockState(bp.offset(negativeDir)).isOf(Blocks.OBSIDIAN)) return height;
            }
         }
         return 21;
      }

      protected boolean validStateInsidePortal(BlockState state) {
         return state.isAir() || state.isIn(BlockTags.FIRE) || state.isOf(portalBlock);
      }

      public boolean isValid() {
         return lowerCorner != null && width >= 2 && width <= 21 && height >= 3 && height <= 21;
      }

      public boolean wasAlreadyValid() {
         return isValid() && foundPortalBlocks >= width * height;
      }
      
      public void createPortal() {
          for (int i = 0; i < this.width; i++) {
             BlockPos blockPos = this.lowerCorner.offset(this.negativeDir, i);
             for (int j = 0; j < this.height; j++) {
            	 this.world.setBlockState(blockPos.up(j), ModBlocks.NEITHER_PORTAL.getDefaultState().with(AXIS, this.axis), 18);
             }
          }
       }
   }
   
   public BlockPos createExitPortal(ServerWorld targetWorld, BlockPos pos, Direction.Axis axis) {
	    WorldBorder worldBorder = targetWorld.getWorldBorder();
	    BlockPos clampedPos = worldBorder.clampFloored(pos.getX(), pos.getY(), pos.getZ());
	    
	    Optional<BlockLocating.Rectangle> existingPortal = targetWorld.getPortalForcer().getPortalPos(clampedPos, targetWorld.getRegistryKey() == World.NETHER, worldBorder)
	        .map(p -> {
	            BlockState state = targetWorld.getBlockState(p);
	            return BlockLocating.getLargestRectangle(p, state.get(Properties.HORIZONTAL_AXIS), 21, Direction.Axis.Y, 21, posx -> targetWorld.getBlockState(posx) == state);
	        });

	    if (existingPortal.isEmpty()) {
	        targetWorld.getPortalForcer().createPortal(clampedPos, axis);
	    }
	    
	    return clampedPos;
	}

   @Override
   public @org.jspecify.annotations.Nullable TeleportTarget createTeleportTarget(ServerWorld world, Entity entity, BlockPos pos) {
       BlockEntity blockEntity = world.getBlockEntity(pos);
       if (!(blockEntity instanceof NeitherPortalEntity neitherPortal)) {
           return null;
       }

       int dimId = neitherPortal.getDimensionId();
       ServerWorld targetWorld = InfiniteDimensionManager.getOrCreateInfiniteDimension(world.getServer(), dimId);

       if (targetWorld == null) {
           return null;
       }
       
       method_26480(targetWorld, pos, targetWorld.getBlockState(pos).getBlock());
       
       BlockPos clamped_pos = createExitPortal(targetWorld, pos, world.getRandom().nextBoolean() ? Axis.Z : Axis.X);

       return new TeleportTarget(
           targetWorld,
           new Vec3d(clamped_pos),
           entity.getVelocity(),
           entity.getYaw(),
           entity.getPitch(),
           TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET
       );
   }
}
