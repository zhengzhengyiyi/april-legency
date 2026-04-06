package net.zhengzhengyiyi.mine;

import java.util.List;
import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.zhengzhengyiyi.AprilsLegacy;
import net.zhengzhengyiyi.ModDimensionTypes;
import net.zhengzhengyiyi.accessor.LevelPropertiesAccessor;
import net.zhengzhengyiyi.block.ModBlocks;
import net.zhengzhengyiyi.component.BiomeMineComponent;
import net.zhengzhengyiyi.mine.effect.class_11113;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

public class class_10967 {

   public static class_10967.class_10970 method_69062(MinecraftServer server, List<MineEffect> effects, Optional<SpecialMine> mine) {
      LevelPropertiesAccessor props = (LevelPropertiesAccessor)(Object)server.getSaveProperties().getMainWorldProperties();
      int mineIndex = props.getLevelCount();
      Identifier id = Identifier.of(AprilsLegacy.MOD_ID, "level" + mineIndex);
      RegistryKey<DimensionOptions> dimensionKey = RegistryKey.of(RegistryKeys.DIMENSION, id);

      RegistryWrapper.WrapperLookup registryManager = server.getRegistryManager();

      // Build dimension settings from effects (biomes, spawns, dimension type modifiers)
      DimensionSettingsBuilder builder = new DimensionSettingsBuilder(registryManager);
      class_11113.method_70020(effects, BiomeMineComponent.class).forEach(c -> c.apply(builder));

      // Base dimension type: use minecraft:generated (void-like overworld, matches reference)
      RegistryEntry<DimensionType> generatedTypeEntry = registryManager.getEntryOrThrow(ModDimensionTypes.GENERATED);
      Optional<DimensionType> modifiedType = builder.modifyDimensionType(generatedTypeEntry.value());
      RegistryEntry<DimensionType> dimensionTypeEntry = modifiedType
         .map(RegistryEntry::of)
         .orElse(generatedTypeEntry);

      // Base generator: void FlatChunkGenerator (no terrain) — effects can override via setGeneratorFactory
      ChunkGenerator generator = createVoidGenerator(registryManager, builder, id.getPath());

      RuntimeWorldConfig config = new RuntimeWorldConfig()
         .setGenerator(generator)
         .setDimensionType(dimensionTypeEntry)
         .setSeed(id.hashCode());

      try {
         RuntimeWorldHandle handle = AprilsLegacy.fantasy.getOrOpenPersistentWorld(id, config);
         ServerWorld world = handle.asWorld();

         if (world == null) {
            System.err.println("[ERROR] Fantasy dimension creation returned null world for: " + id);
         } else {
            System.out.println("[SUCCESS] Mine dimension created: " + id);
            // Persist the effects list so ServerWorldMixin.getEffectSet() can load them
            MineWorldEffectsState effectsState = world.getPersistentStateManager()
               .getOrCreate(MineWorldEffectsState.TYPE);
            effectsState.setEffects(effects);
            placeSpawnPlatform(world);
         }

         return new class_10970(dimensionKey, world);
      } catch (Exception e) {
         System.err.println("[ERROR] Failed to create mine dimension: " + e.getMessage());
         e.printStackTrace();
         return new class_10970(dimensionKey, null);
      }
   }

   /**
    * Creates the chunk generator for the mine world.
    * Default is a void FlatChunkGenerator (no terrain, matching the reference).
    * Effects that call setGeneratorFactory() override this with a custom generator.
    */
   private static ChunkGenerator createVoidGenerator(RegistryWrapper.WrapperLookup registryManager, DimensionSettingsBuilder builder, String subPath) {
      // If an effect registered a custom generator factory, use it
      if (builder.hasCustomGeneratorFactory()) {
         return builder.createGenerator(subPath);
      }

      // Otherwise: void flat world — no layers, plains biome, no structures
      var biomeRegistry = registryManager.getOrThrow(RegistryKeys.BIOME);
      RegistryEntry<net.minecraft.world.biome.Biome> biome = biomeRegistry.getOrThrow(BiomeKeys.PLAINS);
      FlatChunkGeneratorConfig voidConfig = new FlatChunkGeneratorConfig(
         Optional.empty(), biome, List.of()
      );
      // No layers = void world
      voidConfig.updateLayerBlocks();
      return new FlatChunkGenerator(voidConfig);
   }

   /**
    * Places a 3x3 stone platform with a MineCrafter block at y=64.
    * The player spawns here in the otherwise empty void dimension.
    */
   private static void placeSpawnPlatform(ServerWorld world) {
      try {
         BlockPos spawnPos = world.getSpawnPoint().getPos();
         BlockPos center = new BlockPos(spawnPos.getX(), 64, spawnPos.getZ());

         // 3x3 stone floor
         for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
               world.setBlockState(center.add(dx, -1, dz), Blocks.STONE.getDefaultState());
            }
         }

         // Clear air above platform
         for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
               world.setBlockState(center.add(dx, 0, dz), Blocks.AIR.getDefaultState());
               world.setBlockState(center.add(dx, 1, dz), Blocks.AIR.getDefaultState());
            }
         }

         // MineCrafter in the center
         world.setBlockState(center, ModBlocks.MINE_CRAFTER.getDefaultState());

         System.out.println("[SPAWN] Platform placed at " + center + " in " + world.getRegistryKey().getValue());
      } catch (Exception e) {
         System.err.println("[SPAWN] Failed to place spawn platform: " + e.getMessage());
      }
   }

   public record class_10970(RegistryKey<DimensionOptions> id, ServerWorld world) {
   }
}
