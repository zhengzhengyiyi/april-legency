package net.zhengzhengyiyi.item;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.zhengzhengyiyi.block.ShimmeringDoorBlock;
import net.zhengzhengyiyi.component.ModDataComponentTypes;
import net.zhengzhengyiyi.component.RoomComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Mirrors craftmine class_11051.
 *
 * When used on a closed ShimmeringDoor:
 * - If the key has an INSTANT_ROOM component: finds a valid rotation for the
 *   room structure (mirrors class_11034.method_69469) and places it immediately.
 * - If the key has no component: just opens the door.
 *
 * The full GUI (class_11034 screen + ClientPacket2) is not yet ported, so
 * placement happens server-side without a preview screen.
 */
public class ShimmeringKeyItem extends Item {

    public ShimmeringKeyItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (!(state.getBlock() instanceof ShimmeringDoorBlock)) {
            return super.useOnBlock(context);
        }
        if (state.get(DoorBlock.OPEN)) {
            return ActionResult.FAIL;
        }
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        ServerWorld serverWorld = (ServerWorld) world;
        RoomComponent room = context.getStack().get(ModDataComponentTypes.INSTANT_ROOM);

        if (room == null) {
            // No room component — just open the door (plain key)
            openDoor(serverWorld, pos, state);
            context.getStack().decrementUnlessCreative(1, player);
            return ActionResult.SUCCESS_SERVER;
        }

        // Find valid placements (mirrors class_11034.method_69469)
        Direction facing = state.get(DoorBlock.FACING);
        DoubleBlockHalf half = state.get(DoorBlock.HALF);
        List<Pair<Object, PlacementData>> placements = findPlacements(
            serverWorld, pos, facing, half, room);

        if (placements.isEmpty()) {
            if (player != null) {
                player.sendMessage(Text.translatable("item.minecraft.shimmering_key.no_space"), true);
            }
            return ActionResult.FAIL;
        }

        // Place the first valid rotation (mirrors onButtonClick(2) in class_11034)
        PlacementData placement = placements.get(0).getSecond();
        Random random = Random.create(placement.seed);
        placement.settings.setRandom(random);
        placement.structure.place(serverWorld, placement.origin, placement.origin,
            placement.settings, serverWorld.random, 3);

        // Open all connecting doors
        for (BlockPos doorPos : placement.doorsToOpen) {
            BlockState doorState = serverWorld.getBlockState(doorPos);
            BlockState opened = doorState.withIfExists(DoorBlock.OPEN, true);
            serverWorld.setBlockState(doorPos, opened, 3);
        }

        context.getStack().decrementUnlessCreative(1, player);
        return ActionResult.SUCCESS_SERVER;
    }

    // ── Placement logic (mirrors class_11034.method_69469) ──────────────────

    private static List<Pair<Object, PlacementData>> findPlacements(
            ServerWorld world, BlockPos doorPos, Direction facing,
            DoubleBlockHalf half, RoomComponent room) {

        Optional<StructureTemplate> templateOpt =
            world.getStructureTemplateManager().getTemplate(room.getFullId());
        if (templateOpt.isEmpty()) return List.of();

        StructureTemplate template = templateOpt.get();
        Direction attachDir = facing.getOpposite();
        BlockPos attachPos = doorPos.offset(attachDir);

        List<Pair<Object, PlacementData>> results = new ArrayList<>();
        StructurePlacementData placementData = new StructurePlacementData();
        long seed = world.random.nextLong();
        Random random = Random.create(seed);
        placementData.setRandom(random);

        for (BlockRotation rotation : BlockRotation.values()) {
            placementData.setRotation(rotation);
            random.setSeed(seed);

            // Get all block infos at ORIGIN (mirrors method_69832 with BlockPos.ORIGIN)
            List<StructureTemplate.StructureBlockInfo> blockInfos =
                template.getInfosForBlock(BlockPos.ORIGIN, placementData, net.minecraft.block.Blocks.STRUCTURE_VOID);

            // Find shimmering doors in the structure that match our door's facing/half
            Map<BlockPos, BlockState> structureDoors = getStructureDoors(blockInfos);
            List<BlockPos> matchingOffsets = structureDoors.entrySet().stream()
                .filter(e -> doorMatches(e.getValue(), half, attachDir))
                .map(e -> attachPos.subtract(e.getKey()))
                .toList();

            for (BlockPos origin : matchingOffsets) {
                Set<BlockPos> doorsToOpen = new HashSet<>();
                // Check all structure blocks are in air
                boolean clear = blockInfos.stream()
                    .allMatch(info -> world.getBlockState(info.pos().add(origin)).isAir());
                if (!clear) continue;

                // Collect connecting doors to open
                collectConnectingDoors(world, origin, structureDoors, doorsToOpen);

                PlacementData pd = new PlacementData(
                    template, origin, placementData.copy(), seed, doorsToOpen);
                results.add(Pair.of(new Object(), pd));
            }
        }
        return results;
    }

    /** Returns all shimmering door blocks in the structure (mirrors method_69495). */
    private static Map<BlockPos, BlockState> getStructureDoors(
            List<StructureTemplate.StructureBlockInfo> infos) {
        Map<BlockPos, BlockState> map = new HashMap<>();
        for (StructureTemplate.StructureBlockInfo info : infos) {
            if (info.state().getBlock() instanceof ShimmeringDoorBlock
                    && !info.state().get(DoorBlock.OPEN)) {
                map.put(info.pos(), info.state());
            }
        }
        return map;
    }

    /** Checks if a door state matches the target facing and half (mirrors method_69474). */
    private static boolean doorMatches(BlockState state, DoubleBlockHalf half, Direction facing) {
        return state.getBlock() instanceof ShimmeringDoorBlock
            && !state.get(DoorBlock.OPEN)
            && state.get(DoorBlock.FACING) == facing
            && state.get(DoorBlock.HALF) == half;
    }

    /**
     * Finds existing shimmering doors in the world that connect to the structure's
     * doors and adds them to the set to open (mirrors method_69468).
     */
    private static void collectConnectingDoors(ServerWorld world, BlockPos origin,
            Map<BlockPos, BlockState> structureDoors, Set<BlockPos> doorsToOpen) {
        structureDoors.forEach((relPos, structState) -> {
            BlockPos worldPos = relPos.add(origin);
            Direction dir = structState.get(DoorBlock.FACING);
            DoubleBlockHalf half = structState.get(DoorBlock.HALF);
            BlockPos opposite = worldPos.offset(dir.getOpposite());
            BlockState existing = world.getBlockState(opposite);
            if (doorMatches(existing, half, dir.getOpposite())) {
                doorsToOpen.add(worldPos);
                doorsToOpen.add(opposite);
            }
        });
    }

    /** Opens both halves of the door at pos. */
    private static void openDoor(ServerWorld world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state.with(DoorBlock.OPEN, true), 3);
        BlockPos other = state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
        BlockState otherState = world.getBlockState(other);
        if (otherState.getBlock() instanceof ShimmeringDoorBlock) {
            world.setBlockState(other, otherState.with(DoorBlock.OPEN, true), 3);
        }
    }

    /** Mirrors class_11034.class_11037 — holds placement data for a valid room position. */
    private record PlacementData(
        StructureTemplate structure,
        BlockPos origin,
        StructurePlacementData settings,
        long seed,
        Set<BlockPos> doorsToOpen
    ) {}
}
