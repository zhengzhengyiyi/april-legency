package net.zhengzhengyiyi.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.zhengzhengyiyi.accessor.BiomeAccessor;
import net.zhengzhengyiyi.mine.BiomeBuilder;

@Mixin(Biome.class)
public abstract class BiomeMixin implements BiomeAccessor {
   @Shadow @Final private BiomeEffects effects;
   @Shadow @Final private SpawnSettings spawnSettings;
   @Shadow @Final private GenerationSettings generationSettings;

   @Shadow public abstract float getTemperature();

   @Override
   public BiomeBuilder getBuilder() {
      return new BiomeBuilder()
         .setOriginal((Biome)(Object)this)
         .method_69668(this.getTemperature())
         .method_69667(this.effects)
         .method_69669(this.spawnSettings)
         .method_69666(this.generationSettings);
   }
}
