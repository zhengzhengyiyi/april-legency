package net.zhengzhengyiyi;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.registry.entry.RegistryEntry;

public enum DirectBiomeAccessType {
   INSTANCE;

   public RegistryEntry<Biome> getBiome(long seed, int x, int y, int z, BiomeAccess.Storage storage) {
      return storage.getBiomeForNoiseGen(x >> 2, y >> 2, z >> 2);
   }
}
