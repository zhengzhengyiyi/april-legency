package net.zhengzhengyiyi.mine;

import java.util.Objects;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import org.jetbrains.annotations.Nullable;

public class BiomeBuilder {
   @Nullable
   private Float field_58893;
   private boolean field_precipitation = true;
   private Biome.TemperatureModifier field_temperatureModifier = Biome.TemperatureModifier.NONE;
   @Nullable
   private Float field_downfall;
   @Nullable
   private BiomeEffects field_58894;
   @Nullable
   private SpawnSettings field_58895;
   @Nullable
   private GenerationSettings field_58896;
   private final EnvironmentAttributeMap.Builder field_environmentAttributeBuilder = EnvironmentAttributeMap.builder();
   @Nullable
   private Biome originalBiome;

   public BiomeBuilder setOriginal(Biome biome) {
      this.originalBiome = biome;
      return this;
   }

   public BiomeBuilder method_69668(float temperature) {
      this.field_58893 = temperature;
      return this;
   }

   public float method_69665() {
      return Objects.requireNonNull(this.field_58893);
   }

   public BiomeBuilder method_69667(BiomeEffects builder) {
      this.field_58894 = builder;
      return this;
   }

   public BiomeEffects method_69670() {
      return Objects.requireNonNull(this.field_58894);
   }

   public BiomeBuilder method_69669(SpawnSettings builder) {
      this.field_58895 = builder;
      return this;
   }

   public SpawnSettings method_69671() {
      return Objects.requireNonNull(this.field_58895);
   }

   public BiomeBuilder method_69666(GenerationSettings builder) {
      this.field_58896 = builder;
      return this;
   }

   public GenerationSettings method_69672() {
      return Objects.requireNonNull(this.field_58896);
   }

   public BiomeBuilder method_precipitation(boolean precipitation) {
      this.field_precipitation = precipitation;
      return this;
   }

   public BiomeBuilder method_downfall(float downfall) {
      this.field_downfall = downfall;
      return this;
   }

   public BiomeBuilder method_temperatureModifier(Biome.TemperatureModifier modifier) {
      this.field_temperatureModifier = modifier;
      return this;
   }

   public Biome build() {
      // Return the original biome if available and no structural changes were made.
      // Biome.Weather is a private nested class and cannot be constructed directly;
      // actual biome reconstruction requires a mixin invoker which is not yet implemented.
      if (this.originalBiome != null) {
         return this.originalBiome;
      }
      throw new IllegalStateException("Cannot build a new Biome without an original reference\n" + this);
   }

   @Override
   public String toString() {
      return "BiomeBuilder{\nclimateSettings="
         + this.field_58893
         + ",\nspecialEffects="
         + this.field_58894
         + ",\nmobSpawnSettings="
         + this.field_58895
         + ",\ngenerationSettings="
         + this.field_58896
         + ",\n}";
   }
}
