package net.zhengzhengyiyi.biome;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class ModBiomeKeys {
public static final RegistryKey<Biome> THE_MOON = register("the_moon");

   private static RegistryKey<Biome> register(String name) {
      return RegistryKey.of(RegistryKeys.BIOME, Identifier.of("zhengzhengyiyi", name));
   }
   
   public static void init() {
   }
}
