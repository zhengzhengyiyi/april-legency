package net.zhengzhengyiyi.feature;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.zhengzhengyiyi.AprilsLegacy;

/**
 * Mirrors craftmine's MiscConfiguredFeatures.
 * field_59596 = "mine_start" uses Feature.PLACE_TEMPLATE with class_11086(List.of("mines/start_platform"))
 * We mirror this exactly: StructureFeature (class_11085) + StructureFeatureConfig (class_11086).
 */
public class ModConfiguredFeatures {

    /** Mirrors MiscConfiguredFeatures.field_59596 ("mine_start") */
    public static final RegistryKey<ConfiguredFeature<?, ?>> MINE_START =
        RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE,
            Identifier.of(AprilsLegacy.MOD_ID, "mine_start"));

    /** Mirrors Feature.PLACE_TEMPLATE (class_11085) */
    public static final StructureFeature MINE_START_FEATURE =
        new StructureFeature(StructureFeatureConfig.CODEC);

    public static void init() {
        Registry.register(Registries.FEATURE,
            Identifier.of(AprilsLegacy.MOD_ID, "mine_start"),
            MINE_START_FEATURE);
    }
}
