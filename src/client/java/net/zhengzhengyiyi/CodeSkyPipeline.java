package net.zhengzhengyiyi;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class CodeSkyPipeline {
    public static final RenderPipeline CODE_SKY = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.POSITION_TEX_COLOR_SNIPPET)
            .withLocation(Identifier.of("aprils-legacy", "pipeline/code_sky"))
            .withVertexShader("core/code_sky")
            .withFragmentShader("core/code_sky")
            .withSampler("Sampler0")
            .withDepthWrite(false)
            .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, VertexFormat.DrawMode.QUADS)
            .build()
    );

    public static void init() {
        // trigger static initializer
    }
}
