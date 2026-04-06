package net.zhengzhengyiyi.renderer.pet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.WolfArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.WolfCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.zhengzhengyiyi.entity.pet.PetWolfEntity;

/** class_11161 - Pet Wolf renderer */
@Environment(EnvType.CLIENT)
public class PetWolfRenderer extends AgeableMobEntityRenderer<PetWolfEntity, WolfEntityRenderState, WolfEntityModel> {
    public PetWolfRenderer(EntityRendererFactory.Context context) {
        super(context,
            new WolfEntityModel(context.getPart(EntityModelLayers.WOLF)),
            new WolfEntityModel(context.getPart(EntityModelLayers.WOLF_BABY)),
            0.5F);
        this.addFeature(new WolfArmorFeatureRenderer(this, context.getEntityModels(), context.getEquipmentRenderer()));
        this.addFeature(new WolfCollarFeatureRenderer(this));
        this.addFeature(new PetWolfHeldItemFeatureRenderer(this));
    }

    @Override
    protected int getMixColor(WolfEntityRenderState state) {
        float f = state.furWetBrightnessMultiplier;
        return f == 1.0F ? -1 : ColorHelper.fromFloats(1.0F, f, f, f);
    }

    @Override
    public Identifier getTexture(WolfEntityRenderState state) { return state.texture; }

    @Override
    public WolfEntityRenderState createRenderState() { return new WolfEntityRenderState(); }

    @Override
    public void updateRenderState(PetWolfEntity entity, WolfEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.angerTime = false;
        state.inSittingPose = entity.isInSittingPose();
        state.begAnimationProgress = entity.method_69426(tickDelta);
        state.shakeProgress = entity.method_69425(tickDelta);
        state.texture = entity.getTexture();
        state.furWetBrightnessMultiplier = entity.method_69424(tickDelta);
        state.collarColor = entity.isTamed() ? entity.getCollarColor() : null;
        state.bodyArmor = entity.getBodyArmor().copy();
    }
}
