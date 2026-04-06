package net.zhengzhengyiyi.renderer.pet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.ArmadilloEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.ArmadilloEntityRenderState;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.entity.pet.PetArmadilloEntity;

/** class_11148 - Pet Armadillo renderer */
@Environment(EnvType.CLIENT)
public class PetArmadilloRenderer extends AgeableMobEntityRenderer<PetArmadilloEntity, ArmadilloEntityRenderState, ArmadilloEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/armadillo.png");

    public PetArmadilloRenderer(EntityRendererFactory.Context context) {
        super(context,
            new ArmadilloEntityModel(context.getPart(EntityModelLayers.ARMADILLO)),
            new ArmadilloEntityModel(context.getPart(EntityModelLayers.ARMADILLO_BABY)),
            0.4F);
    }

    @Override
    public Identifier getTexture(ArmadilloEntityRenderState state) { return TEXTURE; }

    @Override
    public ArmadilloEntityRenderState createRenderState() { return new ArmadilloEntityRenderState(); }

    @Override
    public void updateRenderState(PetArmadilloEntity entity, ArmadilloEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.rolledUp = entity.isRolledUp();
        state.scaredAnimationState.copyFrom(entity.field_58585);
        state.unrollingAnimationState.copyFrom(entity.field_58583);
        state.rollingAnimationState.copyFrom(entity.field_58584);
    }
}
