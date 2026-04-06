package net.zhengzhengyiyi.renderer.pet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

/** class_11162 - Wolf held item feature renderer */
@Environment(EnvType.CLIENT)
public class PetWolfHeldItemFeatureRenderer extends FeatureRenderer<WolfEntityRenderState, WolfEntityModel> {
    public PetWolfHeldItemFeatureRenderer(FeatureRendererContext<WolfEntityRenderState, WolfEntityModel> ctx) {
        super(ctx);
    }

    @Override
    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light,
                       WolfEntityRenderState state, float yaw, float pitch) {
        // WolfEntityRenderState does not carry item render state; no-op
    }
}
