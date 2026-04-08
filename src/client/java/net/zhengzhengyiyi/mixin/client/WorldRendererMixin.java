package net.zhengzhengyiyi.mixin.client;

import net.minecraft.client.render.SkyRendering;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Exposes WorldRenderer's skyRendering field so other client code can reach it.
 * In 1.21.11 the sky framebuffer was removed; sky renders directly to mainFramebuffer.
 */
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Shadow
    private SkyRendering skyRendering;
}
