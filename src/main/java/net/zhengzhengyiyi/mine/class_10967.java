package net.zhengzhengyiyi.mine;

import java.util.List;
import java.util.Optional;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.zhengzhengyiyi.AprilsLegacy;
import net.zhengzhengyiyi.accessor.LevelPropertiesAccessor;
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
      DimensionSettingsBuilder builder = new DimensionSettingsBuilder(registryManager);
      class_11113.method_70020(effects, BiomeMineComponent.class).forEach(c -> c.apply(builder));

      Optional<DimensionType> modifiedType = builder.modifyDimensionType(
         registryManager.getEntryOrThrow(DimensionTypes.OVERWORLD).value()
      );

      ChunkGenerator generator = builder.createGenerator(id.getPath());

      RuntimeWorldConfig config = new RuntimeWorldConfig()
         .setGenerator(generator)
         .setSeed(id.hashCode());

      modifiedType.ifPresent(type -> {
         var dimensionTypes = registryManager.getOrThrow(RegistryKeys.DIMENSION_TYPE);
         var entry = dimensionTypes.getOrThrow(DimensionTypes.OVERWORLD);
         config.setDimensionType(entry);
      });

      return new class_10967.class_10970(dimensionKey, () -> {
         RuntimeWorldHandle handle = AprilsLegacy.fantasy.getOrOpenPersistentWorld(id, config);
         handle.asWorld();
      });
   }

   public record class_10970(RegistryKey<DimensionOptions> id, Runnable synchronize) {
   }
}
