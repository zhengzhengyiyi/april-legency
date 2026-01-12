package net.zhengzhengyiyi.generator;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChainBlock;
import net.minecraft.block.EndRodBlock;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;

public class MoonLabGenerator {
   @SuppressWarnings("unused")
private static final int MAX_SEARCH_DEPTH = 4;
   private final ServerWorld world;
   private final BlockPos origin;
   private final Random random;
   private final int tickDelay;
   private final int maxDepth;
   private final Map<BlockPos, BranchData> branchMap = new HashMap<>();
   private final List<BranchData> pendingBranches = new ArrayList<>();
   private final List<BlockPos> terminalPoints = new ArrayList<>();
   private final Map<BlockPos, Integer> posToRustLevel = new HashMap<>();
   private final Block[] copperBlocks = new Block[]{
      Blocks.WAXED_COPPER_BLOCK, Blocks.WAXED_EXPOSED_COPPER, Blocks.WAXED_WEATHERED_COPPER, Blocks.WAXED_OXIDIZED_COPPER
   };
   private final Block[] cutCopperBlocks = new Block[]{
      Blocks.WAXED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER
   };

   public MoonLabGenerator(ServerWorld serverWorld, BlockPos blockPos, Random random, int tickDelay, int maxDepth) {
      this.world = serverWorld;
      this.origin = blockPos;
      this.random = random;
      this.tickDelay = tickDelay;
      this.maxDepth = maxDepth;
   }

   public void generate() {
      if (this.tickDelay == 0) {
         this.generateInstantly();
      } else {
         this.generateWithDelay();
      }
   }

   private void generateInstantly() {
      this.pendingBranches.addAll(this.createBranches(this.world, this.origin, this.random, 0, 0, 0));

      while (!this.pendingBranches.isEmpty()) {
         this.processNextLevel();
      }

      int minRust = this.calculateRustLevels();

      for (int level = 4; level >= minRust; level--) {
         Iterator<Entry<BlockPos, Integer>> iterator = this.posToRustLevel.entrySet().iterator();

         while (iterator.hasNext()) {
            Entry<BlockPos, Integer> entry = iterator.next();
            if (entry.getValue() == level) {
               this.placeDecorations(this.world, this.branchMap.get(entry.getKey()), this.random, level, false);
               iterator.remove();
            }
         }
      }
   }

   private void generateWithDelay() {
      this.pendingBranches.addAll(this.createBranches(this.world, this.origin, this.random, 0, 0, this.tickDelay));
      MoonLabGenerator.schedule(this.world.getServer(), this.tickDelay, this::tickDelayedGeneration);
   }

   private void tickDelayedGeneration() {
      if (this.pendingBranches.isEmpty()) {
         this.finalizeDelayedGeneration();
      } else {
         this.processNextLevel();
         MoonLabGenerator.schedule(this.world.getServer(), this.tickDelay, this::tickDelayedGeneration);
      }
   }

   private void finalizeDelayedGeneration() {
      this.calculateRustLevels();
      this.posToRustLevel.forEach((pos, rustLevel) -> {
         int delay = this.tickDelay * (4 - rustLevel) + this.random.nextInt(this.tickDelay);
         MoonLabGenerator.schedule(this.world.getServer(), delay, () -> this.placeDecorations(this.world, this.branchMap.get(pos), this.random, rustLevel, true));
      });
   }

   private int calculateRustLevels() {
      int minLevel = 4;

      for (BlockPos pos : this.terminalPoints) {
         BranchData branch = this.branchMap.get(pos);
         this.posToRustLevel.put(branch.pos, 4);
         int currentLevel = 4;

         while (this.branchMap.containsKey(branch.start)) {
            branch = this.branchMap.get(branch.start);
            minLevel = Math.min(minLevel, --currentLevel);
            this.posToRustLevel.put(branch.pos, Math.min(this.posToRustLevel.getOrDefault(branch.pos, currentLevel), currentLevel));
         }
      }

      return minLevel;
   }

   private void processNextLevel() {
      List<BranchData> nextLevel = new ArrayList<>();

      for (BranchData branch : this.pendingBranches) {
         List<BranchData> children = this.createBranches(this.world, branch.pos, this.random, branch.depth, branch.rustLevel, this.tickDelay);
         nextLevel.addAll(children);
         if (children.isEmpty()) {
            this.terminalPoints.add(branch.pos);
         }

         this.branchMap.put(branch.pos, branch);
      }

      this.pendingBranches.clear();
      this.pendingBranches.addAll(nextLevel);
   }

   private void placeDecorations(StructureWorldAccess world, BranchData branch, Random random, int rustLevel, boolean playSound) {
      if (rustLevel < 0) {
         this.placeButtons(world, branch, random);
         if (playSound) {
            world.toServerWorld().playSound(null, branch.pos, SoundEvents.BLOCK_NETHER_WOOD_PLACE, SoundCategory.BLOCKS, 0.6F, 0.6F + 0.2F * random.nextFloat());
         }
      } else {
         if (playSound) {
            world.toServerWorld().playSound(null, branch.pos, SoundEvents.BLOCK_SCULK_SPREAD, SoundCategory.BLOCKS, 0.6F, 0.5F + 0.2F * random.nextFloat());
         }

         Direction.Axis axis = branch.direction.getAxis();

         for (int i = 0; i < rustLevel; i++) {
            int offset = random.nextInt(branch.length);
            Direction direction = Util.getRandom(this.getPerpendiculars(axis), random);
            BlockPos.Mutable mutablePos = branch.start.offset(branch.direction, offset).offset(direction).mutableCopy();

            for (int j = 0; j < rustLevel && world.isAir(mutablePos); j++) {
               BlockState leaves = Blocks.COPPER_BLOCK.getDefaultState();
               this.safeSetBlock(world, mutablePos, leaves);
               int moveDirIndex = random.nextInt(6);

               mutablePos.move(switch (moveDirIndex) {
                  case 0, 1, 2 -> branch.direction;
                  case 3 -> direction;
                  case 4 -> direction.rotateClockwise(axis);
                  case 5 -> direction.rotateCounterclockwise(axis);
                  default -> throw new IllegalStateException("Unexpected value: " + moveDirIndex);
               });
            }
         }

         if (rustLevel == 4) {
            BlockPos tipPos = branch.pos.offset(branch.direction);
            boolean isSurface = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, tipPos).getY() == tipPos.getY();

            BlockState decoration = switch (branch.direction) {
               case UP -> isSurface ? Blocks.CHEST.getDefaultState() : Blocks.GREEN_SHULKER_BOX.getDefaultState();
               case DOWN -> {
                  switch (random.nextInt(10)) {
                     case 0:
                        yield Blocks.END_ROD.getDefaultState().with(EndRodBlock.FACING, Direction.DOWN);
                     case 1:
                     case 2:
                        yield Blocks.SOUL_LANTERN.getDefaultState().with(LanternBlock.HANGING, true);
                     default:
                        yield Blocks.IRON_CHAIN.getDefaultState().with(ChainBlock.AXIS, Direction.Axis.Y);
                  }
               }
               default -> Blocks.COPPER_BLOCK.getDefaultState();
            };
            this.safeSetBlock(world, tipPos, decoration);
            // TODO
//            if (world.getBlockEntity(tipPos) instanceof LootableContainerBlockEntity container) {
//               if (isSurface) {
//                  LootableContainerBlockEntity.setLootTable(world, random, tipPos, LootTables.MOON_RESUPLY_CHEST);
//                  container.setCustomName(Text.translatable("block.minecraft.chest.moon"));
//               } else {
//                  LootableContainerBlockEntity.setLootTable(world, random, tipPos, LootTables.MOON_LAB_CHEST);
//                  container.setCustomName(Text.translatable("block.minecraft.chest.lab"));
//               }
//            }
         }
      }
   }

   private void placeButtons(StructureWorldAccess world, BranchData branch, Random random) {
      Direction.Axis axis = branch.direction.getAxis();

      for (int i = 0; i < branch.length; i++) {
         for (int j = random.nextInt(3); j < 2; j++) {
            Direction direction = Util.getRandom(this.getPerpendiculars(axis), random);
            BlockPos buttonPos = branch.start.offset(branch.direction, i).offset(direction);
            if (world.isAir(buttonPos)) {
               this.safeSetBlock(
                  world,
                  buttonPos,
                  Blocks.POLISHED_BLACKSTONE_BUTTON.getDefaultState()
               );
            }
         }
      }
   }

   private List<BranchData> createBranches(StructureWorldAccess world, BlockPos pos, Random random, int depth, int rustLevel, int delay) {
      if (depth > this.maxDepth) {
         return List.of();
      } else if (rustLevel > 100) {
         return List.of();
      } else {
         Map<Direction, Integer> lengthMap = new EnumMap<>(Direction.class);
         List<Direction> directions = new ArrayList<>(this.pickDirections(depth, random).filter(dir -> {
            int length = random.nextInt(dir == Direction.UP ? 3 : 4) + 3;
            lengthMap.put(dir, length);

            for (int i = 2; i < length + 1; i++) {
               for (int dx = -1; dx < 2; dx++) {
                  for (int dy = -1; dy < 2; dy++) {
                     for (int dz = -1; dz < 2; dz++) {
                        if (!world.getBlockState(pos.offset(dir, i).add(dx, dy, dz)).isAir()) {
                           return false;
                        }
                     }
                  }
               }
            }

            for (int i = 3; i < length + 1; i++) {
               for (Direction checkDir : Direction.values()) {
                  if (!world.getBlockState(pos.offset(dir, i).offset(checkDir, 2)).isAir()) {
                     return false;
                  }
               }
            }

            return true;
         }).toList());
         Util.shuffle(directions, random);

         for (Direction dir : directions) {
            int length = lengthMap.get(dir);

            for (int i = 1; i <= length; i++) {
               boolean isEnd = i == length || random.nextInt() == 1;
               BlockState state = this.getCopperState(rustLevel + i, random, isEnd, dir);
               if (delay > 0) {
                  spawnBlockDisplay(world, pos.offset(dir, i), state, new AnimationTarget(pos, 0.99F / i, delay));
               } else {
                  this.safeSetBlock(world, pos.offset(dir, i), state);
               }
            }
         }

         if (directions.isEmpty()) {
            return List.of();
         } else {
            if (delay > 0) {
               world.toServerWorld().playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 0.3F, 0.3F + 0.3F * random.nextFloat());
               world.toServerWorld().playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 0.8F, 1.5F + 0.5F * random.nextFloat());
            }

            return directions.stream()
               .map(dir -> new BranchData(
                     pos, pos.offset(dir, lengthMap.get(dir)), lengthMap.get(dir), depth + 1, rustLevel + lengthMap.get(dir), dir
                  )
               )
               .collect(Collectors.toList());
         }
      }
   }

   private void safeSetBlock(StructureWorldAccess world, BlockPos pos, BlockState state) {
      if (world.getBlockState(pos).isAir()) {
         world.setBlockState(pos, state, 3);
      }
   }

   private BlockState getCopperState(int rustScore, Random random, boolean isPillar, Direction direction) {
      int variant = rustScore + random.nextInt(20) - random.nextInt(20);
      Block[] fullBlocks = this.copperBlocks; 
      Block[] cutBlocks = this.cutCopperBlocks;
      BlockState state;
      if (variant < 40) {
         state = (isPillar ? cutBlocks[0] : fullBlocks[0]).getDefaultState();
      } else if (variant < 60) {
         state = (isPillar ? cutBlocks[1] : fullBlocks[1]).getDefaultState();
      } else if (variant < 80) {
         state = (isPillar ? cutBlocks[2] : fullBlocks[2]).getDefaultState();
      } else {
         state = (isPillar ? cutBlocks[3] : fullBlocks[3]).getDefaultState();
      }

      if (state.contains(PillarBlock.AXIS)) {
         state = state.with(PillarBlock.AXIS, direction.getAxis());
      }

      return state;
   }

   private Stream<Direction> pickDirections(int depth, Random random) {
      List<Direction> list = new ArrayList<>();
      float upChance = depth < 4 ? 1.0F : 2.0F / (depth + 1);
      float horizontalChance = (float)Math.sin(depth * Math.PI / 20.0);
      float downChance = (depth - 10) / 5.0F;
      
      if (random.nextFloat() < upChance) list.add(Direction.UP);
      if (random.nextFloat() < horizontalChance) list.add(Direction.NORTH);
      if (random.nextFloat() < horizontalChance) list.add(Direction.SOUTH);
      if (random.nextFloat() < horizontalChance) list.add(Direction.EAST);
      if (random.nextFloat() < horizontalChance) list.add(Direction.WEST);
      if (random.nextFloat() < downChance) list.add(Direction.DOWN);

      return list.stream();
   }

   public static AnimationTarget[] getLaunchTransforms(BlockPos pos) {
      return new AnimationTarget[]{
         new AnimationTarget(pos.up(50), 2.2F, 50),
         new AnimationTarget(pos.up(3), 1.0F, 45),
         new AnimationTarget(pos.up(4), 1.5F, 5)
      };
   }

   public static void spawnRocketLaunch(ServerWorld serverWorld, BlockPos pos) {
      serverWorld.createExplosion(null, pos.getX(), pos.getY() + 50.0, pos.getZ(), 15.0F, World.ExplosionSourceType.NONE);
      Direction[] directions = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

      for (Direction direction : directions) {
         spawnBlockDisplay(serverWorld, pos.offset(direction), Blocks.RAW_COPPER_BLOCK.getDefaultState(), getLaunchTransforms(pos.offset(direction)));
      }

      spawnBlockDisplay(serverWorld, pos, Blocks.RAW_COPPER_BLOCK.getDefaultState(), getLaunchTransforms(pos));

      for (Direction direction : directions) {
         spawnBlockDisplay(
            serverWorld,
            pos.up().offset(direction),
            Blocks.WAXED_CUT_COPPER_STAIRS.getDefaultState().with(StairsBlock.FACING, direction.getOpposite()),
            getLaunchTransforms(pos.up().offset(direction))
         );
      }

      spawnBlockDisplay(serverWorld, pos.up(), Blocks.RAW_COPPER_BLOCK.getDefaultState(), getLaunchTransforms(pos.up()));
      spawnBlockDisplay(serverWorld, pos.up().up(), Blocks.WAXED_COPPER_BLOCK.getDefaultState(), getLaunchTransforms(pos.up().up()));
      
      schedule(serverWorld.getServer(), 50, () -> {
         for (int i = 0; i < 40; i++) {
            schedule(serverWorld.getServer(), i, () -> {
               serverWorld.playSound(null, pos.getX(), pos.getY() + 3.0, pos.getZ(), SoundEvents.AMBIENT_BASALT_DELTAS_MOOD.value(), SoundCategory.BLOCKS, 0.2F, 1.0F);
               serverWorld.spawnParticles(ParticleTypes.SMOKE, pos.getX(), pos.getY() + 3.0, pos.getZ(), 100, 1.0, 0.5, 1.0, 0.3);
            });
         }
      });

      schedule(serverWorld.getServer(), 95, () -> {
         for (int i = 0; i < 5; i++) {
            int delay = i;
            schedule(serverWorld.getServer(), i, () -> {
               serverWorld.playSound(null, pos.getX(), pos.getY() + 3.0 + delay, pos.getZ(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 0.4F, 1.4F);
               serverWorld.spawnParticles(ParticleTypes.FLAME, pos.getX(), pos.getY() + 3.0 + delay, pos.getZ(), 100, 0.7, 0.5, 0.7, 0.0);
            });
         }
      });

      schedule(serverWorld.getServer(), 100, () -> {
         serverWorld.playSound(null, pos.getX(), pos.getY() + 3.0, pos.getZ(), SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.BLOCKS, 1.0F, 1.4F);

         for (Direction direction : directions) {
            spawnBlockDisplay(
               serverWorld,
               pos.up().up().offset(direction),
               Blocks.LIGHTNING_ROD.getDefaultState().with(LightningRodBlock.FACING, direction.getOpposite()),
               new AnimationTarget(pos.up(2), 1.0F, 100)
            );
         }

         BlockState basalt = Blocks.POLISHED_BASALT.getDefaultState().with(PillarBlock.AXIS, Direction.Axis.Y);
         BlockState chain = Blocks.IRON_CHAIN.getDefaultState().with(PillarBlock.AXIS, Direction.Axis.Y);
         serverWorld.setBlockState(pos.north().east(), basalt, 3);
         serverWorld.setBlockState(pos.north().east().up(), chain, 3);
         serverWorld.setBlockState(pos.north().west(), basalt, 3);
         serverWorld.setBlockState(pos.south().east(), basalt, 3);
         serverWorld.setBlockState(pos.south().west().up(), chain, 3);
         serverWorld.setBlockState(pos.south().west(), basalt, 3);
      });

      schedule(serverWorld.getServer(), 200, () -> {
         ChunkRandom chunkRandom = new ChunkRandom(serverWorld.random);
         chunkRandom.setCarverSeed(serverWorld.getSeed(), pos.getX() >> 4, pos.getZ() >> 4);
         MoonLabGenerator generator = new MoonLabGenerator(serverWorld, pos.up().up(), chunkRandom, 20, 20);
         generator.generate();
      });
   }

   private List<Direction> getPerpendiculars(Direction.Axis axis) {
      return switch (axis) {
         case X -> List.of(Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH);
         case Y -> List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
         case Z -> List.of(Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST);
      };
   }

   private static void spawnBlockDisplay(StructureWorldAccess world, BlockPos pos, BlockState state, AnimationTarget... targets) {
      if (world instanceof ServerWorld serverWorld) {
         DisplayEntity.BlockDisplayEntity entity = new DisplayEntity.BlockDisplayEntity(EntityType.BLOCK_DISPLAY, serverWorld);
         entity.setPos(pos.getX(), pos.getY(), pos.getZ());
         entity.setBlockState(state);
         serverWorld.spawnEntity(entity);
      }
   }
   
   private static void schedule(MinecraftServer server, int ticks, Runnable task) {
       server.execute(task); 
   }

   record BranchData(BlockPos start, BlockPos pos, int length, int depth, int rustLevel, Direction direction) {}
   
   record AnimationTarget(BlockPos targetPos, float scale, int duration) {}
}
