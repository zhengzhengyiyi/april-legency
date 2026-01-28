package net.zhengzhengyiyi;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class ModDimensionTypes {
   public static final RegistryKey<DimensionType> THE_MOON = of("the_moon");
   public static final Identifier THE_MOON_IDENTIFIER = Identifier.of("the_moon");
   public static final RegistryKey<World> THE_MOON_KEY = RegistryKey.of(RegistryKeys.WORLD, Identifier.of("zhengzhengyiyi", "the_moon"));
   
   public static final RegistryKey<DimensionType> INFINITE_DIM_TYPE_KEY = RegistryKey.of(
		    RegistryKeys.DIMENSION_TYPE, 
		    Identifier.of(AprilsLegacy.MOD_ID, "infinite_type")
		);

   private static RegistryKey<DimensionType> of(String id) {
      return RegistryKey.of(RegistryKeys.DIMENSION_TYPE, Identifier.of("zhengzhengyiyi", id));
   }
   
   public static void init() {
   }
}
