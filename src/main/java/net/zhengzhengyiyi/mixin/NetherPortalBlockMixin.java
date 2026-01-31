package net.zhengzhengyiyi.mixin;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.zhengzhengyiyi.InfiniteDimensionManager;
import net.zhengzhengyiyi.block.AbstractDimensionBlock;
import net.zhengzhengyiyi.block.ModBlocks;
import net.zhengzhengyiyi.block.NeitherPortalEntity;
import net.zhengzhengyiyi.generator.DimensionHasher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin extends Block implements Portal, AbstractDimensionBlock {
    private static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;

    public NetherPortalBlockMixin(Settings settings) {
        super(settings);
    }

    @Overwrite
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
        if (entity instanceof ItemEntity itemEntity) {
            ItemStack stack = itemEntity.getStack();
            if (stack.isOf(Items.WRITTEN_BOOK) || stack.isOf(Items.WRITABLE_BOOK)) {
                WrittenBookContentComponent content = stack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
                
                String bookText = "";
                if (content != null) {
                    bookText = content.pages().stream()
                            .map(page -> page.raw().getString())
                            .collect(Collectors.joining("\n")).trim();
                }

                if (!bookText.isEmpty()) {
                    int dimId = DimensionHasher.hash(bookText);
                    this.convertPortal(world, pos, state, dimId, true);
                } else {
                    this.convertPortal(world, pos, state, 0, false);
                }
                entity.discard();
                return;
            }
        }

        if (!entity.hasVehicle() && !entity.hasPassengers() && entity.canUsePortals(false)) {
            entity.tryUsePortal(this, pos);
        }
    }

    private void convertPortal(World world, BlockPos startPos, BlockState state, int dimId, boolean toCustom) {
        Set<BlockPos> visited = Sets.newHashSet();
        Queue<BlockPos> queue = Queues.newArrayDeque();
        Direction.Axis axis = state.get(AXIS);
        
        BlockState targetState = toCustom 
            ? ModBlocks.NEITHER_PORTAL.getDefaultState().with(AXIS, axis)
            : Blocks.NETHER_PORTAL.getDefaultState().with(AXIS, axis);
            
        queue.add(startPos);
        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            if (visited.add(current)) {
                BlockState currentState = world.getBlockState(current);
                if (currentState.isOf(this) || currentState.isOf(Blocks.NETHER_PORTAL) || currentState.isOf(ModBlocks.NEITHER_PORTAL)) {
                    world.setBlockState(current, targetState, 18);
                    if (toCustom) {
                        BlockEntity be = world.getBlockEntity(current);
                        if (be instanceof NeitherPortalEntity shiny) {
                            shiny.setDimensionId(dimId);
                        }
                    }
                    world.updateListeners(current, targetState, targetState, 3);
                    for (Direction dir : Direction.values()) {
                        queue.add(current.offset(dir));
                    }
                }
            }
        }
    }

    @Overwrite
    public TeleportTarget createTeleportTarget(ServerWorld world, Entity entity, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof NeitherPortalEntity neitherPortal)) {
            return null;
        }

        int dimId = neitherPortal.getDimensionId();
        ServerWorld targetWorld = InfiniteDimensionManager.getOrCreateInfiniteDimension(world.getServer(), dimId);

        if (targetWorld == null) {
            return null;
        }

        return new TeleportTarget(
                targetWorld,
                entity.getEntityPos(),
                entity.getVelocity(),
                entity.getYaw(),
                entity.getPitch(),
                TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET
        );
    }
}
