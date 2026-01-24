package net.zhengzhengyiyi.block;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import net.zhengzhengyiyi.generator.DimensionHasher;

import org.jetbrains.annotations.Nullable;

public class DimensionPortalBlock extends Block implements Portal {
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
                  this.convertPortal(world, pos, state, dimId);
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

   private void convertPortal(World world, BlockPos startPos, BlockState state, int dimId) {
      Set<BlockPos> visited = Sets.newHashSet();
      Queue<BlockPos> queue = Queues.newArrayDeque();
      Direction.Axis axis = state.get(AXIS);
      BlockState newPortalState = Blocks.NETHER_PORTAL.getDefaultState().with(AXIS, axis);

      queue.add(startPos);
      while (!queue.isEmpty()) {
         BlockPos current = queue.poll();
         if (visited.add(current) && world.getBlockState(current).isOf(this)) {
            world.setBlockState(current, newPortalState, 18);
            BlockEntity be = world.getBlockEntity(current);
            if (be instanceof NeitherPortalEntity shiny) {
               shiny.setDimensionId(dimId);
            }
            for (Direction dir : Direction.values()) {
               queue.add(current.offset(dir));
            }
         }
      }
   }

   @Environment(EnvType.CLIENT)
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

   @Environment(EnvType.CLIENT)
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
   }

   @Override
   public @org.jspecify.annotations.Nullable TeleportTarget createTeleportTarget(ServerWorld world, Entity entity,
		BlockPos pos) {
	   // TODO
	return null;
   }
}
