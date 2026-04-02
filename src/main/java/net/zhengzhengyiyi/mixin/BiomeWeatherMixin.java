package net.zhengzhengyiyi.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.world.biome.Biome;
import net.zhengzhengyiyi.accessor.BiomeWeatherAccessor;

@Mixin(targets = "net.minecraft.world.biome.Biome$Weather")
public class BiomeWeatherMixin implements BiomeWeatherAccessor {
   @Shadow @Final private boolean hasPrecipitation;
   @Shadow @Final private float temperature;
   @Shadow @Final private Biome.TemperatureModifier temperatureModifier;
   @Shadow @Final private float downfall;

   @Override
   public boolean getHasPrecipitation() {
      return this.hasPrecipitation;
   }

   @Override
   public float getWeatherTemperature() {
      return this.temperature;
   }

   @Override
   public Biome.TemperatureModifier getWeatherTemperatureModifier() {
      return this.temperatureModifier;
   }

   @Override
   public float getWeatherDownfall() {
      return this.downfall;
   }
}
