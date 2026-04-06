package net.zhengzhengyiyi.renderer.pet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.CatCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.CatEntityRenderState;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.entity.pet.PetCatEntity;

/** class_11151 - Pet Cat renderer */
@Environment(EnvType.CLIENT)
public class PetCatRenderer extends AgeableMobEntityRenderer<PetCatEntity, CatEntityRenderState, CatEntityModel> {
    public PetCatRenderer(EntityRendererFactory.Context context) {
        super(context,
            new CatEntityModel(context.getPart(EntityModelLayers.CAT)),
            new CatEntityModel(context.getPart(EntityModelLayers.CAT_BABY)),
            0.4F);
        this.addFeature(new CatCollarFeatureRenderer(this, context.getEntityModels()));
    }

    @Override
    public Identifier getTexture(CatEntityRenderState state) { return state.texture; }

    @Override
    public CatEntityRenderState createRenderState() { return new CatEntityRenderState(); }

    @Override
    public void updateRenderState(PetCatEntity entity, CatEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.texture = ((CatVariant) entity.getVariant().value()).assetInfo().texturePath();
        state.collarColor = entity.isTamed() ? entity.getCollarColor() : null;
    }
}
