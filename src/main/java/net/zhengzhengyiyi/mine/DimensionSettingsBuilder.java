package net.zhengzhengyiyi.mine;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.collection.Weighted;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.zhengzhengyiyi.accessor.BiomeAccessor;
import net.zhengzhengyiyi.accessor.ChunkSettingsAccessor;
import net.zhengzhengyiyi.mine.effect.BiomeModifier;

import org.apache.commons.lang3.function.TriFunction;

public class DimensionSettingsBuilder {
   private final Set<RegistryKey<Biome>> allowedBiomes = new HashSet<>();
   private final Map<SpawnGroup, List<Weighted<SpawnSettings.SpawnEntry>>> extraSpawns = new HashMap<>();
   private final List<Modifier<DimensionType>> dimensionModifiers = new ArrayList<>();
   private final RegistryWrapper.WrapperLookup registryManager;
   private final List<BiomeModifier> biomeModifiers = new ArrayList<>();
   private RegistryKey<ChunkGeneratorSettings> baseSettingsKey = ChunkGeneratorSettings.OVERWORLD;
   private Optional<GeneratorFactory> customGeneratorFactory = Optional.empty();
   private final List<Consumer<ChunkSettingsAccessor.Builder>> settingsModifiers = new ArrayList<>();
   private SpawnLocator spawnLocator = SpawnLocator.SURFACE;
   private long salt = 0L;

   public DimensionSettingsBuilder(RegistryWrapper.WrapperLookup wrapperLookup) {
      this.registryManager = wrapperLookup;
   }

   public RegistryWrapper.WrapperLookup getRegistryManager() {
      return this.registryManager;
   }

   public <T> RegistryEntry<T> getEntry(RegistryKey<T> registryKey) {
      return this.registryManager.getEntryOrThrow(registryKey);
   }

   public DimensionSettingsBuilder allowBiome(RegistryKey<Biome> registryKey) {
      this.allowedBiomes.add(registryKey);
      return this;
   }

   public DimensionSettingsBuilder addSpawn(SpawnGroup spawnGroup, int i, SpawnSettings.SpawnEntry spawnEntry) {
      this.extraSpawns.computeIfAbsent(spawnGroup, group -> new ArrayList<>()).add(new Weighted<>(spawnEntry, i));
      return this;
   }

   public DimensionSettingsBuilder addDimensionModifier(Modifier<DimensionType> arg) {
      this.dimensionModifiers.add(arg);
      return this;
   }

   public DimensionSettingsBuilder modifyBiome(RegistryKey<Biome> registryKey, Consumer<BiomeModifier> consumer) {
      return this.addGlobalBiomeModifier(arg -> consumer.accept(arg.method_69679(reference -> reference.registryKey() == registryKey)));
   }

   public DimensionSettingsBuilder addGlobalBiomeModifier(Consumer<BiomeModifier> consumer) {
      BiomeModifier lv = new BiomeModifier();
      consumer.accept(lv);
      this.biomeModifiers.add(lv);
      return this;
   }

   public DimensionSettingsBuilder setBaseSettings(RegistryKey<ChunkGeneratorSettings> registryKey) {
      this.baseSettingsKey = registryKey;
      return this;
   }

   public DimensionSettingsBuilder modifySettings(Consumer<ChunkSettingsAccessor.Builder> consumer) {
      this.settingsModifiers.add(consumer);
      return this;
   }

   public DimensionSettingsBuilder setSalt(long salt) {
      this.salt = salt;
      return this;
   }

   public DimensionSettingsBuilder setSpawnLocator(SpawnLocator arg) {
      this.spawnLocator = arg;
      return this;
   }

   public DimensionSettingsBuilder setGeneratorFactory(GeneratorFactory arg) {
      this.customGeneratorFactory = Optional.of(arg);
      return this;
   }

   public boolean hasCustomGeneratorFactory() {
      return this.customGeneratorFactory.isPresent();
   }

   public ChunkGenerator createGenerator(String subPath) {
      // Mirrors craftmine class_11114.method_70204:
      // Filter biome source to allowed biomes if specified, otherwise use all overworld biomes.
      var biomeRegistry = this.registryManager.getOrThrow(RegistryKeys.BIOME);
      var paramListRegistry = this.registryManager
         .getOrThrow(RegistryKeys.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST)
         .getOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD);

      final BiomeSource biomeSource;
      if (this.allowedBiomes.isEmpty()) {
         // No biome filter — use the full overworld biome source
         biomeSource = MultiNoiseBiomeSource.create(paramListRegistry);
      } else {
         // Use a FixedBiomeSource with the first allowed biome so the mine has a distinct look.
         // For multi-biome effects (e.g. forests = oak+birch+flower), pick based on the subPath
         // hash so the choice is stable across reloads but varies per mine.
         List<net.minecraft.registry.entry.RegistryEntry<Biome>> entries = this.allowedBiomes.stream()
            .map(biomeRegistry::getOrThrow)
            .collect(java.util.stream.Collectors.toList());
         int index = Math.abs(subPath.hashCode()) % entries.size();
         biomeSource = new net.minecraft.world.biome.source.FixedBiomeSource(entries.get(index));
      }

      RegistryWrapper.Impl<ChunkGeneratorSettings> settingsRegistry = this.registryManager.getOrThrow(RegistryKeys.CHUNK_GENERATOR_SETTINGS);

      ChunkSettingsAccessor.Builder builder = ((ChunkSettingsAccessor)(Object)settingsRegistry.getOrThrow(this.baseSettingsKey).value()).getBuilder();

      this.settingsModifiers.forEach(consumer -> consumer.accept(builder));
      
      // Apply salt to chunk generator settings
      // This mirrors Craftmine's NoiseConfig which XORs seed with salt: seed ^ settings.salt()
      // The salt provides additional terrain variation beyond the world seed
      builder.method_69805(this.salt);

      RegistryEntry<ChunkGeneratorSettings> registryEntry = RegistryEntry.of(builder.build());

      return (ChunkGenerator)(this.customGeneratorFactory.isPresent()
         ? (ChunkGenerator)this.customGeneratorFactory.get().apply(this.registryManager, biomeSource, registryEntry)
         : new NoiseChunkGenerator(biomeSource, registryEntry));
   }

   public Optional<DimensionType> modifyDimensionType(DimensionType dimensionType) {
      if (this.dimensionModifiers.isEmpty()) {
         return Optional.empty();
      } else {
         DimensionType currentType = dimensionType;
         for (Modifier<DimensionType> modifier : this.dimensionModifiers) {
            currentType = modifier.apply(currentType);
         }
         return currentType.equals(dimensionType) ? Optional.empty() : Optional.of(currentType);
      }
   }

   @SuppressWarnings("unused")
public List<BiomeMapping> buildBiomeMappings(RegistryWrapper<Biome> registryWrapper, String prefix) {
      Stream<RegistryEntry.Reference<Biome>> entries = this.allowedBiomes.isEmpty() ? registryWrapper.streamEntries() : this.allowedBiomes.stream().map(registryWrapper::getOrThrow);

      return entries.filter(reference -> !reference.registryKey().getValue().getPath().startsWith("level"))
         .map(
            reference -> {
               BiomeBuilder biomeBuilder = ((BiomeAccessor)(Object)reference.value()).getBuilder();

               for (Entry<SpawnGroup, List<Weighted<SpawnSettings.SpawnEntry>>> entry : this.extraSpawns.entrySet()) {
                  SpawnGroup spawnGroup = entry.getKey();
                  List<Weighted<SpawnSettings.SpawnEntry>> spawns = entry.getValue();
//                  biomeBuilder.method_69671().method_69697(spawnGroup);
                  // TODO

                  for (Weighted<SpawnSettings.SpawnEntry> weighted : spawns) {
                     biomeBuilder.method_69671().getSpawnEntries(spawnGroup);
                  }
               }

               this.biomeModifiers.forEach(mod -> mod.method_69675(biomeBuilder));
               Biome biome = biomeBuilder.build();
               RegistryKey<Biome> registryKey = biome.equals(reference.value())
                  ? reference.registryKey()
                  : RegistryKey.of(RegistryKeys.BIOME, reference.registryKey().getValue().withPrefixedPath(prefix + "/"));
               return new BiomeMapping(reference.registryKey(), registryKey, biome);
            }
         )
         .filter(mapping -> mapping.original() != mapping.modified())
         .toList();
   }

   public SpawnLocator getSpawnLocator() {
      return this.spawnLocator;
   }

   public interface GeneratorFactory extends TriFunction<RegistryWrapper.WrapperLookup, BiomeSource, RegistryEntry<ChunkGeneratorSettings>, ChunkGenerator> {
   }

   public record BiomeMapping(RegistryKey<Biome> original, RegistryKey<Biome> modified, Biome biome) {
   }

   public interface Modifier<T> extends Function<T, T> {
   }
}
