package net.zhengzhengyiyi.mixin.client;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.MoonPhase;
import net.minecraft.world.World;
import net.zhengzhengyiyi.world.WorldShape;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalDouble;
import java.util.OptionalInt;

@Mixin(SkyRendering.class)
public abstract class SkyRendererMixin {

    @Shadow @Final private RenderSystem.ShapeIndexBuffer indexBuffer2;
    @Shadow @Final private SpriteAtlasTexture celestialAtlasTexture;
    @Shadow public abstract void renderTopSky(int color);

    @Unique private GpuBuffer earthVertexBuffer;

    @Unique private static final Identifier EARTH_TEXTURE = WorldShape.DEFAULT.getTexture();

    // ── Mine sky ──────────────────────────────────────────────────────────────
    @Inject(method = "renderEndSky()V", at = @At("HEAD"), cancellable = true)
    private void injectMineSky(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;
        if (!client.world.getRegistryKey().getValue().getPath().startsWith("level")) return;
        ci.cancel();
        this.renderTopSky(0xFF0A0A1A);
    }

    // ── Moon dimension: replace sun with Earth ────────────────────────────────
    @Inject(method = "renderSun", at = @At("HEAD"), cancellable = true)
    private void renderSun(float alpha, MatrixStack matrices, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || !client.world.getRegistryKey().getValue().getPath().contains("moon")) return;
        ci.cancel();

        if (this.earthVertexBuffer == null) {
            Sprite sprite = this.celestialAtlasTexture.getSprite(EARTH_TEXTURE);
            this.earthVertexBuffer = createEarthBuffer(sprite);
        }

        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.mul(matrices.peek().getPositionMatrix());
        matrix4fStack.translate(0.0F, 100.0F, 0.0F);
        matrix4fStack.scale(30.0F, 1.0F, 30.0F);

        GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write(
            matrix4fStack, new Vector4f(1.0F, 1.0F, 1.0F, alpha), new Vector3f(), new Matrix4f()
        );

        GpuTextureView colorView = MinecraftClient.getInstance().getFramebuffer().getColorAttachmentView();
        GpuTextureView depthView = MinecraftClient.getInstance().getFramebuffer().getDepthAttachmentView();
        GpuBuffer indexBuf = this.indexBuffer2.getIndexBuffer(6);

        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder()
                .createRenderPass(() -> "Sky earth", colorView, OptionalInt.empty(), depthView, OptionalDouble.empty())) {
            renderPass.setPipeline(RenderPipelines.POSITION_TEX_COLOR_CELESTIAL);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
            renderPass.bindTexture("Sampler0", this.celestialAtlasTexture.getGlTextureView(), this.celestialAtlasTexture.getSampler());
            renderPass.setVertexBuffer(0, this.earthVertexBuffer);
            renderPass.setIndexBuffer(indexBuf, this.indexBuffer2.getIndexType());
            renderPass.drawIndexed(0, 0, 6, 1);
        }

        matrix4fStack.popMatrix();
    }

    // ── Moon dimension: suppress vanilla moon quad ────────────────────────────
    @Inject(method = "renderMoon", at = @At("HEAD"), cancellable = true)
    private void suppressMoon(MoonPhase phase, float alpha, MatrixStack matrices, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;
        if (!client.world.getRegistryKey().getValue().getPath().contains("moon")) return;
        ci.cancel();
    }

    @Unique
    private static GpuBuffer buildCodeSkyBuffer() {
        try (net.minecraft.client.util.BufferAllocator allocator =
                net.minecraft.client.util.BufferAllocator.fixedSized(4 * VertexFormats.POSITION_TEXTURE.getVertexSize())) {
            BufferBuilder builder = new BufferBuilder(allocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            builder.vertex(-1.0F, -1.0F, 0.0F).texture(0.0F, 1.0F);
            builder.vertex(-1.0F,  1.0F, 0.0F).texture(0.0F, 0.0F);
            builder.vertex( 1.0F,  1.0F, 0.0F).texture(1.0F, 0.0F);
            builder.vertex( 1.0F, -1.0F, 0.0F).texture(1.0F, 1.0F);
            try (BuiltBuffer builtBuffer = builder.end()) {
                return RenderSystem.getDevice().createBuffer(
                    () -> "Code sky vertex buffer", GpuBuffer.USAGE_VERTEX, builtBuffer.getBuffer()
                );
            }
        }
    }

    @Unique
    private GpuBuffer createEarthBuffer(Sprite sprite) {
        try (net.minecraft.client.util.BufferAllocator allocator =
                net.minecraft.client.util.BufferAllocator.fixedSized(4 * VertexFormats.POSITION_TEXTURE.getVertexSize())) {
            BufferBuilder builder = new BufferBuilder(allocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            builder.vertex(-1.0F, 0.0F, -1.0F).texture(sprite.getMinU(), sprite.getMinV());
            builder.vertex( 1.0F, 0.0F, -1.0F).texture(sprite.getMaxU(), sprite.getMinV());
            builder.vertex( 1.0F, 0.0F,  1.0F).texture(sprite.getMaxU(), sprite.getMaxV());
            builder.vertex(-1.0F, 0.0F,  1.0F).texture(sprite.getMinU(), sprite.getMaxV());
            try (BuiltBuffer builtBuffer = builder.end()) {
                return RenderSystem.getDevice().createBuffer(
                    () -> "Earth vertex buffer", GpuBuffer.USAGE_VERTEX, builtBuffer.getBuffer()
                );
            }
        }
    }
}
