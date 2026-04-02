package net.zhengzhengyiyi.accessor;

import net.minecraft.world.biome.Biome;

public interface BiomeWeatherAccessor {
   boolean getHasPrecipitation();
   float getWeatherTemperature();
   Biome.TemperatureModifier getWeatherTemperatureModifier();
   float getWeatherDownfall();
}
