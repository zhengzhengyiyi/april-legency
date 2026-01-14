package net.zhengzhengyiyi.mixin.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.RotationAxis;
import net.zhengzhengyiyi.world.WorldShape;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalDouble;
import java.util.OptionalInt;

@Mixin(SkyRendering.class)
public class WorldRendererMixin {

    @Unique private static final Vector4f COLOR = new Vector4f(1f, 1f, 1f, 1f);
    @Unique private static final Vector3f OFFSET = new Vector3f();
    @Unique private static final Matrix4f TEX_MATRIX = new Matrix4f();
    @Unique private MappableRingBuffer vertexBuffer;

    @Inject(
        method = "renderCelestialBodies",
        at = @At("TAIL")
    )
    private void renderEarthInMoon(MatrixStack matrices, float sunAngle, float moonAngle, float starAngle, int moonPhase, float rainGradient, float starBrightness, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;

        if (world != null && world.getRegistryKey().getValue().getPath().contains("moon")) {
            RenderPipeline pipeline = client.getRenderPipelineManager().getPipeline(GameRenderer.getPositionTexProgram());
            
            float k = 80.0F;
            
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(client.getRenderTickCounter().getTickDelta(true)) * 360.0F));
            Matrix4f posMat = matrices.peek().getPositionMatrix();

            BufferBuilder builder = Tessellator.getInstance().getBuffer();
            builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            builder.vertex(posMat, -k, -100.0F, k).texture(0.0F, 0.0F).next();
            builder.vertex(posMat, k, -100.0F, k).texture(1.0F, 0.0F).next();
            builder.vertex(posMat, k, -100.0F, -k).texture(1.0F, 1.0F).next();
            builder.vertex(posMat, -k, -100.0F, -k).texture(0.0F, 1.0F).next();
            
            MeshData meshData = builder.end();
            draw(client, pipeline, meshData);

            matrices.pop();
        }
    }

    @Unique
    private void draw(MinecraftClient client, RenderPipeline pipeline, MeshData meshData) {
        MeshData.DrawState state = meshData.drawState();
        int size = state.vertexCount() * state.format().getVertexSize();

        if (vertexBuffer == null || vertexBuffer.size() < size) {
            vertexBuffer = new MappableRingBuffer(() -> "earth_render", GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_MAP_WRITE, size);
        }

        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        try (GpuBuffer.MappedView view = encoder.mapBuffer(vertexBuffer.currentBuffer().slice(0, meshData.vertexBuffer().remaining()), false, true)) {
            MemoryUtil.memCopy(meshData.vertexBuffer(), view.data());
        }

        GpuBufferSlice transforms = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrix(), COLOR, OFFSET, TEX_MATRIX);

        try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "earth_pass", 
                client.getMainRenderTarget().getColorTextureView(), OptionalInt.empty(), 
                client.getMainRenderTarget().getDepthTextureView(), OptionalDouble.empty())) {
            
            pass.setPipeline(pipeline);
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", transforms);
            pass.bindTexture("Sampler0", WorldShape.DEFAULT.getTexture(), RenderSystem.getLinearSampler());
            pass.setVertexBuffer(0, vertexBuffer.currentBuffer());
            
            RenderSystem.AutoStorageIndexBuffer indices = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
            pass.setIndexBuffer(indices.getBuffer(state.indexCount()), indices.type());

            pass.drawIndexed(0, 0, state.indexCount(), 1);
        }

        vertexBuffer.rotate();
        meshData.close();
    }
}
