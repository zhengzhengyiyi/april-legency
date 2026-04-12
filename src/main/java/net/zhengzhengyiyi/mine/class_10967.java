package net.zhengzhengyiyi.mine;

import java.util.List;
import java.util.Optional;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.zhengzhengyiyi.AprilsLegacy;
import net.zhengzhengyiyi.ModDimensionTypes;
import net.zhengzhengyiyi.accessor.LevelPropertiesAccessor;
import net.zhengzhengyiyi.component.BiomeMineComponent;
import net.zhengzhengyiyi.mine.effect.class_11113;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

public class class_10967 {

   /**
    * Mirrors craftmine's class_10967.method_69062.
    *
    * Builds the dimension type and chunk generator from effects using class_11114
    * (the world modifier builder), then creates the world via Fantasy.
    */
   public static class_10967.class_10970 method_69062(MinecraftServer server, List<MineEffect> effects, Optional<SpecialMine> mine) {
      LevelPropertiesAccessor props = (LevelPropertiesAccessor)(Object)server.getSaveProperties().getMainWorldProperties();
      int i = props.getLevelCount();
      Identifier id = Identifier.ofVanilla("level" + i);
      RegistryKey<DimensionOptions> dimensionKey = RegistryKey.of(RegistryKeys.DIMENSION, id);

      RegistryWrapper.WrapperLookup registryManager = server.getRegistryManager();

      // --- Mirrors craftmine: build world modifier from effects ---
      DimensionSettingsBuilder worldModifier = new DimensionSettingsBuilder(registryManager);
      class_11113.method_70020(effects, BiomeMineComponent.class).forEach(c -> c.apply(worldModifier));

      // --- Dimension type: use minecraft:generated as base, apply modifiers ---
      RegistryEntry<DimensionType> generatedEntry = registryManager.getEntryOrThrow(ModDimensionTypes.GENERATED);
      Optional<DimensionType> modifiedType = worldModifier.modifyDimensionType(generatedEntry.value());
      RegistryEntry<DimensionType> dimensionTypeEntry = modifiedType
         .map(RegistryEntry::of)
         .orElse(generatedEntry);

      // --- Chunk generator: mirrors craftmine's method_70204 ---
      ChunkGenerator generator = worldModifier.createGenerator(id.getPath());

      // --- SpawnLocator from modifier ---
      // (used by method_69093 in ServerWorldMixin, not needed here directly)

      // --- Create world via Fantasy ---
      RuntimeWorldConfig config = new RuntimeWorldConfig()
         .setGenerator(generator)
         .setDimensionType(dimensionTypeEntry)
         .setSeed(id.hashCode());

      try {
         RuntimeWorldHandle handle = AprilsLegacy.fantasy.getOrOpenPersistentWorld(id, config);
         ServerWorld world = handle.asWorld();

         if (world == null) {
            System.err.println("[ERROR] Fantasy returned null world for: " + id);
         } else {
            System.out.println("[SUCCESS] Mine dimension created: " + id);
            // Persist effects and spawn locator for ServerWorldMixin — mirrors craftmine's
            // class_10969 which stores effects, mine, and spawn in the dimension JSON.
            MineWorldEffectsState effectsState = world.getPersistentStateManager()
               .getOrCreate(MineWorldEffectsState.TYPE);
            effectsState.setEffects(effects);
            effectsState.setSpawnLocator(worldModifier.getSpawnLocator());
            // Trigger first-entry: places platform and teleports all players (method_69093)
            ((net.zhengzhengyiyi.accessor.MineServerWorldAccessor) world)
               .method_69093(false, Optional.empty());
         }

         return new class_10970(dimensionKey, world);
      } catch (Exception e) {
         System.err.println("[ERROR] Failed to create mine dimension: " + e.getMessage());
         e.printStackTrace();
         return new class_10970(dimensionKey, null);
      }
   }

   public record class_10970(RegistryKey<DimensionOptions> id, ServerWorld world) {
   }
}
