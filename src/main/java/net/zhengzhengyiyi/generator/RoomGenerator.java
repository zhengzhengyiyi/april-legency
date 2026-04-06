package net.zhengzhengyiyi.generator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;

/**
 * class_11034 - Door/Room Generator
 * Generates room layouts with doors and walls using pattern-based placement.
 */
public class RoomGenerator {
    private final ServerWorld world;
    private final BlockPos origin;
    private final Map<BlockPos, BlockType> blockMap = new HashMap<>();

    public RoomGenerator(ServerWorld world, BlockPos origin) {
        this.world = world;
        this.origin = origin;
    }

    /** method_69472 - Creates pattern from string */
    public static Pattern method_69472(String patternStr) {
        String[] lines = patternStr.split("\n");
        char[][] grid = new char[lines.length][];
        for (int i = 0; i < lines.length; i++) {
            grid[i] = lines[i].toCharArray();
        }
        return new Pattern(grid);
    }

    /** method_69480 - Gets column position */
    public BlockPos method_69480(int column) {
        return this.origin.add(column * 5, 0, 0);
    }

    /** method_69464 - Gets adjacent columns */
    public List<BlockPos> method_69464(BlockPos pos) {
        return List.of(
            pos.north(5), pos.south(5), pos.east(5), pos.west(5)
        );
    }

    /** method_69469 - Generates room layouts */
    public List<RoomData> method_69469(int roomCount) {
        List<RoomData> rooms = new ArrayList<>();
        Random random = new java.util.Random(this.world.getRandom().nextLong());
        Set<BlockPos> occupied = new HashSet<>();
        BlockPos current = this.origin;

        for (int i = 0; i < roomCount; i++) {
            Bounds bounds = new Bounds(current, current.add(9, 5, 9));
            RoomData room = new RoomData(current, bounds, new ArrayList<>());
            rooms.add(room);
            occupied.add(current);

            // Pick next position
            List<BlockPos> candidates = method_69464(current).stream()
                .filter(p -> !occupied.contains(p))
                .toList();
            if (candidates.isEmpty()) break;
            current = candidates.get(random.nextInt(candidates.size()));
        }

        method_69468(rooms);
        method_69485(rooms);
        return rooms;
    }

    /** method_69495 - Gets door blocks */
    public List<BlockPos> method_69495(RoomData room) {
        List<BlockPos> doors = new ArrayList<>();
        for (Map.Entry<BlockPos, BlockType> entry : this.blockMap.entrySet()) {
            if (entry.getValue() == BlockType.DOOR && isInBounds(entry.getKey(), room.bounds())) {
                doors.add(entry.getKey());
            }
        }
        return doors;
    }

    /** method_69473 - Checks if door block */
    public boolean method_69473(BlockPos pos) {
        return this.blockMap.getOrDefault(pos, BlockType.AIR) == BlockType.DOOR;
    }

    /** method_69474 - Checks if matching door */
    public boolean method_69474(BlockPos a, BlockPos b) {
        return method_69473(a) && method_69473(b) && a.getManhattanDistance(b) <= 2;
    }

    /** method_69498 - Gets block map */
    public Map<BlockPos, BlockType> method_69498() {
        return Collections.unmodifiableMap(this.blockMap);
    }

    /** method_69465 - Validates room layout */
    public boolean method_69465(List<RoomData> rooms) {
        return rooms.stream().allMatch(r -> !r.doors().isEmpty());
    }

    /** method_69468 - Processes doors between adjacent rooms */
    public void method_69468(List<RoomData> rooms) {
        for (int i = 0; i < rooms.size() - 1; i++) {
            RoomData a = rooms.get(i);
            RoomData b = rooms.get(i + 1);
            BlockPos doorPos = a.origin().add(4, 1, 4); // center wall
            a.doors().add(doorPos);
            b.doors().add(doorPos);
            this.blockMap.put(doorPos, BlockType.DOOR);
        }
    }

    /** method_69485 - Applies structure blocks to world */
    public void method_69485(List<RoomData> rooms) {
        for (RoomData room : rooms) {
            placeRoom(room);
        }
    }

    /** method_69466 - Checks adjacent blocks */
    public boolean method_69466(BlockPos pos, BlockType type) {
        for (Direction dir : Direction.values()) {
            if (this.blockMap.getOrDefault(pos.offset(dir), BlockType.AIR) == type) {
                return true;
            }
        }
        return false;
    }

    /** method_69488 - Creates pattern */
    public Pattern method_69488(int width, int height, char fill) {
        char[][] grid = new char[height][width];
        for (char[] row : grid) Arrays.fill(row, fill);
        return new Pattern(grid);
    }

    /** method_69501 - Determines block type */
    public BlockType method_69501(char c) {
        return switch (c) {
            case '#' -> BlockType.WALL;
            case 'D' -> BlockType.DOOR;
            case ' ' -> BlockType.AIR;
            default -> BlockType.FLOOR;
        };
    }

    /** method_69479 - Marks walls */
    public void method_69479(RoomData room) {
        Bounds b = room.bounds();
        for (int x = b.min().getX(); x <= b.max().getX(); x++) {
            for (int z = b.min().getZ(); z <= b.max().getZ(); z++) {
                BlockPos pos = new BlockPos(x, b.min().getY(), z);
                boolean isWall = x == b.min().getX() || x == b.max().getX()
                    || z == b.min().getZ() || z == b.max().getZ();
                if (isWall && !method_69473(pos)) {
                    this.blockMap.put(pos, BlockType.WALL);
                }
            }
        }
    }

    /** method_69471 - Checks if new block */
    public boolean method_69471(BlockPos pos) {
        return !this.blockMap.containsKey(pos);
    }

    /** method_69492 - Checks if existing block */
    public boolean method_69492(BlockPos pos) {
        return this.blockMap.containsKey(pos);
    }

    private void placeRoom(RoomData room) {
        Bounds b = room.bounds();
        for (int x = b.min().getX(); x <= b.max().getX(); x++) {
            for (int z = b.min().getZ(); z <= b.max().getZ(); z++) {
                for (int y = b.min().getY(); y <= b.max().getY(); y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockType type = this.blockMap.getOrDefault(pos, BlockType.AIR);
                    BlockState state = switch (type) {
                        case WALL -> Blocks.STONE_BRICKS.getDefaultState();
                        case DOOR -> Blocks.OAK_DOOR.getDefaultState();
                        case FLOOR -> Blocks.STONE.getDefaultState();
                        default -> Blocks.AIR.getDefaultState();
                    };
                    this.world.setBlockState(pos, state);
                }
            }
        }
    }

    private boolean isInBounds(BlockPos pos, Bounds bounds) {
        return pos.getX() >= bounds.min().getX() && pos.getX() <= bounds.max().getX()
            && pos.getZ() >= bounds.min().getZ() && pos.getZ() <= bounds.max().getZ();
    }

    /** Inner enum class_11035 - Block types */
    public enum BlockType {
        AIR, WALL, DOOR, FLOOR
    }

    /** Inner record class_11036 - Pattern */
    public record Pattern(char[][] grid) {
        public int width() { return grid.length > 0 ? grid[0].length : 0; }
        public int height() { return grid.length; }
        public char get(int row, int col) { return grid[row][col]; }
    }

    /** Inner record class_11037 - Room data */
    public record RoomData(BlockPos origin, Bounds bounds, List<BlockPos> doors) {}

    /** Inner record class_11038 - Bounds */
    public record Bounds(BlockPos min, BlockPos max) {
        public boolean contains(BlockPos pos) {
            return pos.getX() >= min.getX() && pos.getX() <= max.getX()
                && pos.getY() >= min.getY() && pos.getY() <= max.getY()
                && pos.getZ() >= min.getZ() && pos.getZ() <= max.getZ();
        }
    }
}
