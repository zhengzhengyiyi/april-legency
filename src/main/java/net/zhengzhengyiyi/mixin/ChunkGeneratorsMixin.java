package net.zhengzhengyiyi.mixin;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGenerators;
import net.zhengzhengyiyi.generator.HubChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mirrors craftmine ChunkGenerators — registers "minecraft:hub" chunk generator.
 * Reference: Registry.register(registry, "hub", class_11083.field_59021)
 */
@Mixin(ChunkGenerators.class)
public class ChunkGeneratorsMixin {

    @Inject(method = "registerAndGetDefault", at = @At("HEAD"))
    private static void registerHub(
        Registry<MapCodec<? extends ChunkGenerator>> registry,
        CallbackInfoReturnable<MapCodec<? extends ChunkGenerator>> cir
    ) {
        Registry.register(registry, "hub", HubChunkGenerator.CODEC);
    }
}
