package net.zhengzhengyiyi.rules.options;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/**
 * Governs how the light engine behaves based on the active vote rule.
 * This can range from normal behavior to completely disabling light updates.
 * <p>
 * Official Obfuscated Class: bfm
 */
public enum OptimizeLightMode implements StringIdentifiable {
    /** Normal vanilla behavior. */
    NONE("none"),
    /** Reduces light updates to save performance. */
    LOADSHEDDING("loadshedding"),
    /** Light updates are suppressed, making the world stay dark or glitchy. */
    NEVER_LIGHT("never_light"),
    /** Forces maximum light levels everywhere. */
    ALWAYS_LIGHT("always_light");

    public static final Codec<OptimizeLightMode> CODEC = StringIdentifiable.createCodec(OptimizeLightMode::values);
    
    /** Thread-local random source to ensure deterministic but varied sheds across threads. */
    private static final ThreadLocal<Random> RANDOM = ThreadLocal.withInitial(Random::create);
    
    private final String id;
    private final Text description;

    OptimizeLightMode(String id) {
        this.id = id;
        this.description = Text.translatable("rule.optimize_light_engine." + id);
    }

    /**
     * Determines if a light update should be skipped (shed).
     * * @param world The current world instance.
     * @return true if the light update should be canceled.
     */
    public boolean shouldShed(World world) {
        switch (this) {
            case LOADSHEDDING:
                // Original logic: Uses world time mixed with a seed to determine 
                // if this tick/chunk should skip light updates.
                Random random = RANDOM.get();
                random.setSeed(HashCommon.mix(world.getTime() / 2400L));
                return random.nextFloat() < 0.5f; // Example: 50% chance to skip
            case NEVER_LIGHT:
                return true;
            default:
                return false;
        }
    }

    /**
     * Checks if the light engine is forced to always-on mode.
     * * @param world The current world instance.
     * @return true if always_light is active.
     */
    public boolean isAlwaysLight(World world) {
        return this == ALWAYS_LIGHT;
    }

    @Override
    public String asString() {
        return this.id;
    }

    public Text getDescription() {
        return this.description;
    }
}