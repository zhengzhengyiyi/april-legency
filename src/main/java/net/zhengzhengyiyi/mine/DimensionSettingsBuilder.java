package net.zhengzhengyiyi.mine;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
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
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
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

   public DimensionSettingsBuilder setSpawnLocator(SpawnLocator arg) {
      this.spawnLocator = arg;
      return this;
   }

   public DimensionSettingsBuilder setGeneratorFactory(GeneratorFactory arg) {
      this.customGeneratorFactory = Optional.of(arg);
      return this;
   }

   public ChunkGenerator createGenerator(String subPath) {
      RegistryWrapper.Impl<Biome> biomeRegistry = this.registryManager.getOrThrow(RegistryKeys.BIOME);
      Stream<RegistryKey<Biome>> biomeStream = this.allowedBiomes.isEmpty() ? biomeRegistry.streamKeys() : this.allowedBiomes.stream();
      List<BiomeMapping> mappings = this.buildBiomeMappings(biomeRegistry, subPath);
      
      Map<RegistryKey<Biome>, RegistryKey<Biome>> keyMap = mappings.stream()
         .collect(Collectors.toMap(BiomeMapping::original, BiomeMapping::modified));
      
      Map<RegistryEntry<Biome>, RegistryEntry<Biome>> entryMap = keyMap.entrySet()
         .stream()
         .collect(Collectors.toMap(entry -> biomeRegistry.getOrThrow(entry.getKey()), entry -> biomeRegistry.getOrThrow(entry.getValue())));
      
      List<RegistryEntry<Biome>> finalBiomes = biomeStream.map(
            registryKey -> entryMap.getOrDefault(biomeRegistry.getOrThrow((RegistryKey<Biome>)registryKey), biomeRegistry.getOrThrow((RegistryKey<Biome>)registryKey))
         )
         .toList();
      
      MultiNoiseBiomeSourceParameterList parameterList = new MultiNoiseBiomeSourceParameterList(finalBiomes, entryMap, biomeRegistry);
      MultiNoiseBiomeSource biomeSource = MultiNoiseBiomeSource.create(RegistryEntry.of(parameterList));
      
      RegistryWrapper.Impl<ChunkGeneratorSettings> settingsRegistry = this.registryManager.getOrThrow(RegistryKeys.CHUNK_GENERATOR_SETTINGS);
      
      ChunkSettingsAccessor.Builder builder = ((ChunkSettingsAccessor)(Object)settingsRegistry.getOrThrow(this.baseSettingsKey).value()).getBuilder();
      
      this.settingsModifiers.forEach(consumer -> consumer.accept(builder));
      builder.method_69811(materialRule -> materialRule.method_69822(keyMap)).method_69805(subPath.hashCode());
      
      RegistryEntry<ChunkGeneratorSettings> registryEntry = RegistryEntry.of(builder.method_69813());
      
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

   public List<BiomeMapping> buildBiomeMappings(RegistryWrapper<Biome> registryWrapper, String prefix) {
      Stream<RegistryEntry.Reference<Biome>> entries = this.allowedBiomes.isEmpty() ? registryWrapper.streamEntries() : this.allowedBiomes.stream().map(registryWrapper::getOrThrow);

      return entries.filter(reference -> !reference.registryKey().getValue().getPath().startsWith("level"))
         .map(
            reference -> {
               class_11058 biomeBuilder = reference.value().method_69664();

               for (Entry<SpawnGroup, List<Weighted<SpawnSettings.SpawnEntry>>> entry : this.extraSpawns.entrySet()) {
                  SpawnGroup spawnGroup = entry.getKey();
                  List<Weighted<SpawnSettings.SpawnEntry>> spawns = entry.getValue();
                  biomeBuilder.method_69671().method_69697(spawnGroup);

                  for (Weighted<SpawnSettings.SpawnEntry> weighted : spawns) {
                     biomeBuilder.method_69671().spawn(spawnGroup, weighted.weight(), weighted.value());
                  }
               }

               this.biomeModifiers.forEach(mod -> mod.method_69675(biomeBuilder));
               Biome biome = biomeBuilder.method_69673();
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
