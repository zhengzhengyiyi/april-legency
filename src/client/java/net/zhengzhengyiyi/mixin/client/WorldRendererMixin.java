package net.zhengzhengyiyi.mixin.client;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.Handle;
import net.zhengzhengyiyi.CodeSkyPipeline;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalDouble;
import java.util.OptionalInt;

/**
 * Adds the code sky frame pass for the overworld (hub/mine control dimension).
 * Mirrors craftmine WorldRenderer.renderSky case class_11076 → skyRendering.method_70443(framebuffer).
 *
 * Injects into renderSky (private method) to add a FramePass outside any active render pass.
 * The framebufferSet.field_59497 (sky framebuffer handle) is accessed via @Shadow.
 */
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Shadow private SkyRendering skyRendering;
    @Shadow private DefaultFramebufferSet framebufferSet;

    @Unique private static final net.minecraft.util.Identifier CODE_SKY_TEXTURE = net.minecraft.util.Identifier.ofVanilla("textures/font/code.png");
    @Unique private GpuBuffer codeSkyVertexBuffer;

    /**
     * Injects into the private renderSky method. When in the overworld, cancels normal sky
     * and adds a code sky FramePass — safe because FramePass.setRenderer runs outside any render pass.
     * Uses the raw field name field_59497 which is the sky framebuffer handle on DefaultFramebufferSet.
     */
    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void renderCodeSkyPass(FrameGraphBuilder frameGraphBuilder, Camera camera, GpuBufferSlice fogBuffer, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.world.getRegistryKey() != World.OVERWORLD) return;

        ci.cancel();

        FramePass framePass = frameGraphBuilder.createPass("code_sky");
        this.framebufferSet.mainFramebuffer = framePass.transfer(this.framebufferSet.mainFramebuffer);
        Handle<Framebuffer> handle = this.framebufferSet.mainFramebuffer;

        framePass.setRenderer(() -> {
            Framebuffer framebuffer = handle.get();

            RenderSystem.getDevice().createCommandEncoder()
                .clearColorAndDepthTextures(framebuffer.getColorAttachment(), 0xFF000000, framebuffer.getDepthAttachment(), 1.0);

            if (this.codeSkyVertexBuffer == null) {
                this.codeSkyVertexBuffer = buildCodeSkyBuffer();
            }

            AbstractTexture texture = client.getTextureManager().getTexture(CODE_SKY_TEXTURE);
            GpuTextureView colorView = framebuffer.getColorAttachmentView();
            GpuTextureView depthView = framebuffer.getDepthAttachmentView();
            RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
            // 4 panels × 6 indices each = 24
            GpuBuffer indexBuf = shapeIndexBuffer.getIndexBuffer(24);

            GpuBufferSlice uniforms = RenderSystem.getDynamicUniforms().write(
                RenderSystem.getModelViewStack(),
                new Vector4f(1.0F, 1.0F, 1.0F, 1.0F),
                new Vector3f(),
                new Matrix4f()
            );

            try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder()
                    .createRenderPass(() -> "Code sky", colorView, OptionalInt.empty(), depthView, OptionalDouble.empty())) {
                renderPass.setPipeline(CodeSkyPipeline.CODE_SKY);
                RenderSystem.bindDefaultUniforms(renderPass);
                renderPass.setUniform("DynamicTransforms", uniforms);
                renderPass.bindTexture("Sampler0", texture.getGlTextureView(), texture.getSampler());
                renderPass.setVertexBuffer(0, this.codeSkyVertexBuffer);
                renderPass.setIndexBuffer(indexBuf, shapeIndexBuffer.getIndexType());
                renderPass.drawIndexed(0, 0, 24, 1);
            }
        });
    }

    /**
     * Builds a 4-panel cylinder sky box matching craftmine's method_70440.
     * Uses POSITION_TEXTURE_COLOR format (white color) for POSITION_TEX_COLOR_END_SKY pipeline.
     * 4 rotated panels at 90° intervals, each a tall quad at z=-100, y from -400 to 400.
     */
    @Unique
    private static GpuBuffer buildCodeSkyBuffer() {
        try (net.minecraft.client.util.BufferAllocator allocator =
                net.minecraft.client.util.BufferAllocator.fixedSized(16 * VertexFormats.POSITION_TEXTURE_COLOR.getVertexSize())) {
            BufferBuilder builder = new BufferBuilder(allocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            for (int i = 0; i < 4; i++) {
                Matrix4f mat = new Matrix4f();
                switch (i) {
                    case 1 -> mat.rotationY((float)(-Math.PI / 2));
                    case 2 -> mat.rotationY((float)(Math.PI / 2));
                    case 3 -> mat.rotationY((float)Math.PI);
                    // case 0: identity
                }
                builder.vertex(mat,  100.0F,  400.0F, -100.0F).texture(1.0F, 1.0F).color(-1);
                builder.vertex(mat, -100.0F,  400.0F, -100.0F).texture(0.0F, 1.0F).color(-1);
                builder.vertex(mat, -100.0F, -400.0F, -100.0F).texture(0.0F, 0.0F).color(-1);
                builder.vertex(mat,  100.0F, -400.0F, -100.0F).texture(1.0F, 0.0F).color(-1);
            }
            try (BuiltBuffer builtBuffer = builder.end()) {
                return RenderSystem.getDevice().createBuffer(
                    () -> "Code sky vertex buffer", GpuBuffer.USAGE_VERTEX, builtBuffer.getBuffer()
                );
            }
        }
    }
}
