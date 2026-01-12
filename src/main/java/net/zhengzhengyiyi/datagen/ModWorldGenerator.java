package net.zhengzhengyiyi.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;
import net.zhengzhengyiyi.AprilsLegacy;
import net.zhengzhengyiyi.feature.CraterFeatureConfig;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModWorldGenerator extends FabricDynamicRegistryProvider {
	public ModWorldGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		super(output, registriesFuture);
	}

	public static void bootstrapConfigured(Registerable<ConfiguredFeature<?, ?>> context) {
		context.register(AprilsLegacy.MEGA_CRATER, new ConfiguredFeature<>(AprilsLegacy.CRATER_FEATURE, 
			new CraterFeatureConfig(UniformIntProvider.create(32, 48), UniformIntProvider.create(8, 16))));
		
		context.register(AprilsLegacy.LARGE_CRATER, new ConfiguredFeature<>(AprilsLegacy.CRATER_FEATURE, 
			new CraterFeatureConfig(UniformIntProvider.create(12, 15), UniformIntProvider.create(3, 5))));
		
		context.register(AprilsLegacy.SMALL_CRATER, new ConfiguredFeature<>(AprilsLegacy.CRATER_FEATURE, 
			new CraterFeatureConfig(UniformIntProvider.create(4, 7), UniformIntProvider.create(2, 3))));
	}

	public static void bootstrapPlaced(Registerable<PlacedFeature> context) {
		var lookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

		context.register(RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of("zhengzhengyiyi", "crater_mega")),
			new PlacedFeature(lookup.getOrThrow(AprilsLegacy.MEGA_CRATER), 
			List.of(RarityFilterPlacementModifier.of(8), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())));

		context.register(RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of("zhengzhengyiyi", "crater_large")),
			new PlacedFeature(lookup.getOrThrow(AprilsLegacy.LARGE_CRATER), 
			List.of(RarityFilterPlacementModifier.of(4), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())));

		context.register(RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of("zhengzhengyiyi", "crater_small")),
			new PlacedFeature(lookup.getOrThrow(AprilsLegacy.SMALL_CRATER), 
			List.of(RarityFilterPlacementModifier.of(2), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())));
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
	}
	
	public static <C extends FeatureConfig, F extends Feature<C>> F register(String name, F feature) {
        return Registry.register(Registries.FEATURE, Identifier.of("zhengzhengyiyi", name), feature);
    }

	@Override
	public String getName() {
		return "World Gen";
	}
}
