package net.zhengzhengyiyi.renderer.pet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PolarBearEntityModel;
import net.minecraft.client.render.entity.state.PolarBearEntityRenderState;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.entity.pet.PetPolarBearEntity;

/** class_11158 - Pet Polar Bear renderer */
@Environment(EnvType.CLIENT)
public class PetPolarBearRenderer extends AgeableMobEntityRenderer<PetPolarBearEntity, PolarBearEntityRenderState, PolarBearEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/bear/polarbear.png");

    public PetPolarBearRenderer(EntityRendererFactory.Context context) {
        super(context,
            new PolarBearEntityModel(context.getPart(EntityModelLayers.POLAR_BEAR)),
            new PolarBearEntityModel(context.getPart(EntityModelLayers.POLAR_BEAR_BABY)),
            0.9F);
    }

    @Override
    public Identifier getTexture(PolarBearEntityRenderState state) { return TEXTURE; }

    @Override
    public PolarBearEntityRenderState createRenderState() { return new PolarBearEntityRenderState(); }

    @Override
    public void updateRenderState(PetPolarBearEntity entity, PolarBearEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.warningAnimationProgress = entity.getStandProgress(tickDelta);
    }
}
