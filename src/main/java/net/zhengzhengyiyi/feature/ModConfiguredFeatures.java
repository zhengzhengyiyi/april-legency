package net.zhengzhengyiyi.feature;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.zhengzhengyiyi.AprilsLegacy;

/**
 * Mirrors craftmine's MiscConfiguredFeatures fields field_59596–field_59601.
 * Each is a PLACE_TEMPLATE feature pointing to an NBT structure under mines/.
 */
public class ModConfiguredFeatures {

    /** field_59596 — mines/start_platform (placed on every first mine entry) */
    public static final RegistryKey<ConfiguredFeature<?, ?>> MINE_START =
        RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE,
            Identifier.of(AprilsLegacy.MOD_ID, "mine_start"));

    /** field_59597 — mines/warden_arena (placed when warden_boss_fight effect is active) */
    public static final RegistryKey<ConfiguredFeature<?, ?>> WARDEN_ARENA =
        RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE,
            Identifier.of(AprilsLegacy.MOD_ID, "warden_arena"));

    /** field_59598 — mines/dirty_ice_ball (placed 150× when kuiper_world effect is active) */
    public static final RegistryKey<ConfiguredFeature<?, ?>> DIRTY_ICE_BALL =
        RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE,
            Identifier.of(AprilsLegacy.MOD_ID, "dirty_ice_ball"));

    /** field_59599 — mines/dirty_ice_ball_fox (placed 150× when kuiper_world effect is active) */
    public static final RegistryKey<ConfiguredFeature<?, ?>> DIRTY_ICE_BALL_FOX =
        RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE,
            Identifier.of(AprilsLegacy.MOD_ID, "dirty_ice_ball_fox"));

    /** field_59600 — mines/dirty_ice_ball_golems (placed 150× when kuiper_world effect is active) */
    public static final RegistryKey<ConfiguredFeature<?, ?>> DIRTY_ICE_BALL_GOLEMS =
        RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE,
            Identifier.of(AprilsLegacy.MOD_ID, "dirty_ice_ball_golems"));

    /** field_59601 — mines/space_igloo (placed at spawn when kuiper_world effect is active, CLOCKWISE_90) */
    public static final RegistryKey<ConfiguredFeature<?, ?>> SPACE_IGLOO =
        RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE,
            Identifier.of(AprilsLegacy.MOD_ID, "space_igloo"));

    /** Shared StructureFeature instance (class_11085) used for all PLACE_TEMPLATE features */
    public static final StructureFeature MINE_START_FEATURE =
        new StructureFeature(StructureFeatureConfig.CODEC);

    public static void init() {
        Registry.register(Registries.FEATURE,
            Identifier.of(AprilsLegacy.MOD_ID, "place_template"),
            MINE_START_FEATURE);
    }
}
