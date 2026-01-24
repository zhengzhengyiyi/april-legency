package net.zhengzhengyiyi.generator;

import net.minecraft.registry.Registries;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;

public class RandomBiomeFactory {
   public static Biome createRandomBiome(int seed) {
      Random random = Random.create(seed);
      return new RandomBiome(random).build();
   }

   static class RandomBiome {
      private final Biome.Builder builder = new Biome.Builder();
      private final SpawnSettings.Builder spawnBuilder = new SpawnSettings.Builder();
      private final GenerationSettings.Builder genBuilder = new GenerationSettings.Builder();
      public final Random random;

      public RandomBiome(Random random) {
         this.random = random;
         
         this.builder.precipitation(random.nextBoolean());
         this.builder.temperature(random.nextFloat() * 2.0F);
         this.builder.downfall(random.nextFloat());
         this.builder.effects(new BiomeEffects.Builder()
               .waterColor(random.nextInt(0xFFFFFF))
               .waterColor(random.nextInt(0xFFFFFF))
//               .fogColor(random.nextInt(0xFFFFFF))
//               .skyColor(random.nextInt(0xFFFFFF))
               .build());

         Util.copyShuffled(Registries.ENTITY_TYPE.stream(), random)
//            .limit(32)
            .forEach(entityType -> {
               int min = random.nextInt(4);
               int max = min + random.nextInt(4);
//               this.spawnBuilder.spawn(entityType.getSpawnGroup(), 100, new SpawnSettings.SpawnEntry(entityType, random.nextInt(20) + 1, min, max));
               this.spawnBuilder.spawn(
            		    entityType.getSpawnGroup(), 
            		    100,
            		    new SpawnSettings.SpawnEntry(
            		        entityType,
            		        min,
            		        max
            		    )
            		);
            });
         // TODO
         
//         for (int i = 0; i < 32; i++) {
//             GenerationStep.Feature step = Util.getRandom(GenerationStep.Feature.values(), random);
//             Registries.FEATURE.getRandom(random).ifPresent(feature -> {
//                 this.genBuilder.feature(step, feature);
//             });
//         }
      }

      public Biome build() {
         return this.builder
               .spawnSettings(this.spawnBuilder.build())
               .generationSettings(this.genBuilder.build())
               .build();
      }
   }
}
