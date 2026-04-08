package net.zhengzhengyiyi.feature;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.zhengzhengyiyi.AprilsLegacy;

/**
 * Mirrors craftmine's MiscConfiguredFeatures.
 */
public class ModConfiguredFeatures {

    /** Mirrors MiscConfiguredFeatures.field_59596 ("mine_start") */
    public static final RegistryKey<ConfiguredFeature<?, ?>> MINE_START =
        RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE,
            Identifier.of(AprilsLegacy.MOD_ID, "mine_start"));

    public static final Feature<DefaultFeatureConfig> MINE_START_FEATURE =
        new MineStartFeature(DefaultFeatureConfig.CODEC);

    public static void init() {
        Registry.register(Registries.FEATURE,
            Identifier.of(AprilsLegacy.MOD_ID, "mine_start"),
            MINE_START_FEATURE);
    }
}
