package net.zhengzhengyiyi.mine.effect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;

public class BiomeModifier {
   private final List<Predicate<RegistryEntry.Reference<Biome>>> field_58897 = new ArrayList<>();
   private final List<Consumer<class_11058>> field_58898 = new ArrayList<>();

   public BiomeModifier method_69679(Predicate<RegistryEntry.Reference<Biome>> predicate) {
      this.field_58897.add(predicate);
      return this;
   }

   public BiomeModifier method_69677(Consumer<Weather.Builder> consumer) {
      this.field_58898.add(arg -> consumer.accept(arg.method_69665()));
      return this;
   }

   public BiomeModifier method_69680(Consumer<BiomeEffects.Builder> consumer) {
      this.field_58898.add(arg -> consumer.accept(arg.method_69670()));
      return this;
   }

   public BiomeModifier method_69682(Consumer<SpawnSettings.Builder> consumer) {
      this.field_58898.add(arg -> consumer.accept(arg.method_69671()));
      return this;
   }

   public BiomeModifier method_69684(Consumer<GenerationSettings.Builder> consumer) {
      this.field_58898.add(arg -> consumer.accept(arg.method_69672()));
      return this;
   }

   public void method_69675(class_11058 arg) {
      this.field_58898.forEach(consumer -> consumer.accept(arg));
   }
}
