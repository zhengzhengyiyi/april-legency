package net.zhengzhengyiyi.renderer.pet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FoxHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.client.render.entity.state.FoxEntityRenderState;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.FoxEntity.Variant;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.zhengzhengyiyi.entity.pet.PetFoxEntity;

/** class_11155 - Pet Fox renderer */
@Environment(EnvType.CLIENT)
public class PetFoxRenderer extends AgeableMobEntityRenderer<PetFoxEntity, FoxEntityRenderState, FoxEntityModel> {
    private static final Identifier TEX_RED        = Identifier.ofVanilla("textures/entity/fox/fox.png");
    private static final Identifier TEX_RED_SLEEP  = Identifier.ofVanilla("textures/entity/fox/fox_sleep.png");
    private static final Identifier TEX_SNOW       = Identifier.ofVanilla("textures/entity/fox/snow_fox.png");
    private static final Identifier TEX_SNOW_SLEEP = Identifier.ofVanilla("textures/entity/fox/snow_fox_sleep.png");

    public PetFoxRenderer(EntityRendererFactory.Context context) {
        super(context,
            new FoxEntityModel(context.getPart(EntityModelLayers.FOX)),
            new FoxEntityModel(context.getPart(EntityModelLayers.FOX_BABY)),
            0.4F);
        this.addFeature(new FoxHeldItemFeatureRenderer(this));
    }

    @Override
    protected void setupTransforms(FoxEntityRenderState state, MatrixStack matrices, float animProgress, float bodyYaw) {
        super.setupTransforms(state, matrices, animProgress, bodyYaw);
        if (state.chasing || state.walking) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-state.pitch));
        }
    }

    @Override
    public Identifier getTexture(FoxEntityRenderState state) {
        if (state.type == Variant.RED) return state.sleeping ? TEX_RED_SLEEP : TEX_RED;
        return state.sleeping ? TEX_SNOW_SLEEP : TEX_SNOW;
    }

    @Override
    public FoxEntityRenderState createRenderState() { return new FoxEntityRenderState(); }

    @Override
    public void updateRenderState(PetFoxEntity entity, FoxEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        ItemHolderEntityRenderState.update(entity, state, this.itemModelResolver);
        state.headRoll                 = 0.0F;   // PetFoxEntity has no headRoll
        state.bodyRotationHeightOffset = 0.0F;   // PetFoxEntity has no bodyRotationHeightOffset
        state.inSneakingPose           = entity.isSneaking();
        state.sleeping                 = entity.isSleeping();
        state.sitting                  = entity.isSitting();
        state.walking                  = entity.getVelocity().horizontalLengthSquared() > 1.0E-6;
        state.chasing                  = false;  // PetFoxEntity does not chase
        state.type                     = entity.method_69389();
    }
}
