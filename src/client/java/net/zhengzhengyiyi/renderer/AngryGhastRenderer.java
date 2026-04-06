package net.zhengzhengyiyi.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.GhastEntityModel;
import net.minecraft.client.render.entity.state.GhastEntityRenderState;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.entity.AngryGhastEntity;

/** class_11147 - Angry Ghast renderer */
@Environment(EnvType.CLIENT)
public class AngryGhastRenderer extends MobEntityRenderer<AngryGhastEntity, GhastEntityRenderState, GhastEntityModel> {
    private static final Identifier TEXTURE         = Identifier.ofVanilla("textures/entity/angry_ghast/ghast.png");
    private static final Identifier TEXTURE_SHOOTING = Identifier.ofVanilla("textures/entity/angry_ghast/ghast_shooting.png");

    public AngryGhastRenderer(EntityRendererFactory.Context context) {
        super(context, new GhastEntityModel(context.getPart(EntityModelLayers.GHAST)), 1.5F);
    }

    @Override
    public Identifier getTexture(GhastEntityRenderState state) {
        return state.shooting ? TEXTURE_SHOOTING : TEXTURE;
    }

    @Override
    public GhastEntityRenderState createRenderState() {
        return new GhastEntityRenderState();
    }

    @Override
    public void updateRenderState(AngryGhastEntity entity, GhastEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.shooting = entity.isCharging();
    }
}
