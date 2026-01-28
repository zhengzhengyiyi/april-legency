package net.zhengzhengyiyi.generator.generation;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterLists;

import java.util.Objects;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;

public class BiomeSourceFactory {
   public static FixedBiomeSource createFixedBiomeSource(RegistryEntry<Biome> biome) {
      return Objects.requireNonNull(new FixedBiomeSource(biome));
   }

   public static MultiNoiseBiomeSource createOverworldBiomeSource(MinecraftServer server) {
      return MultiNoiseBiomeSource.create(
          server.getRegistryManager()
                .getOrThrow(net.minecraft.registry.RegistryKeys.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST)
                .getOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD)
      );
   }
}
