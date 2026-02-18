package net.zhengzhengyiyi.component;

import java.util.List;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.zhengzhengyiyi.mine.DimensionSettingsBuilder;

public interface BiomeMineComponent extends MineEffectComponent {
   public record BiomeEntry(List<RegistryKey<Biome>> biomes) implements BiomeMineComponent {
      @SafeVarargs
      public BiomeEntry(RegistryKey<Biome>... registryKeys) {
         this(List.of(registryKeys));
      }

      @Override
      public void apply(DimensionSettingsBuilder context) {
         this.biomes.forEach(context::allowBiome);
      }
   }
   
   public static interface Entry {
      void apply(DimensionSettingsBuilder context);
   }
}
