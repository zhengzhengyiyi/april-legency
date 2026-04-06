package net.zhengzhengyiyi.entity.task;

import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Custom memory module types for this mod.
 */
public class ModMemoryModuleTypes {
    /** ACTING_STAGE - tracks the current acting stage (0-4) for the Warden performance sequence */
    public static final MemoryModuleType<Integer> ACTING_STAGE =
        Registry.register(Registries.MEMORY_MODULE_TYPE, Identifier.of("aprils-legacy", "acting_stage"),
            new MemoryModuleType<>(java.util.Optional.of(com.mojang.serialization.Codec.INT)));

    public static void init() {
        // trigger static initialization
    }
}
