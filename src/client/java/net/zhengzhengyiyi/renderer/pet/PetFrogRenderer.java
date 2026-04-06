package net.zhengzhengyiyi.renderer.pet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.FrogEntityModel;
import net.minecraft.client.render.entity.state.FrogEntityRenderState;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.entity.pet.PetFrogEntity;

/** class_11156 - Pet Frog renderer */
@Environment(EnvType.CLIENT)
public class PetFrogRenderer extends MobEntityRenderer<PetFrogEntity, FrogEntityRenderState, FrogEntityModel> {
    public PetFrogRenderer(EntityRendererFactory.Context context) {
        super(context, new FrogEntityModel(context.getPart(EntityModelLayers.FROG)), 0.3F);
    }

    @Override
    public Identifier getTexture(FrogEntityRenderState state) { return state.texture; }

    @Override
    public FrogEntityRenderState createRenderState() { return new FrogEntityRenderState(); }

    @Override
    public void updateRenderState(PetFrogEntity entity, FrogEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.insideWaterOrBubbleColumn = entity.isTouchingWater();
        state.longJumpingAnimationState.copyFrom(entity.field_58672);
        state.croakingAnimationState.copyFrom(entity.field_58673);
        state.usingTongueAnimationState.copyFrom(entity.field_58674);
        state.idlingInWaterAnimationState.copyFrom(entity.field_58675);
        state.texture = ((FrogVariant) entity.getVariant().value()).assetInfo().texturePath();
    }
}
