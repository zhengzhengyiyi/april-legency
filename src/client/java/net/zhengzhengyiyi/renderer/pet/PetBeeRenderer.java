package net.zhengzhengyiyi.renderer.pet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BeeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BeeEntityRenderState;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.entity.pet.PetBeeEntity;

/** class_11150 - Pet Bee renderer */
@Environment(EnvType.CLIENT)
public class PetBeeRenderer extends AgeableMobEntityRenderer<PetBeeEntity, BeeEntityRenderState, BeeEntityModel> {
    private static final Identifier TEXTURE        = Identifier.ofVanilla("textures/entity/bee/bee.png");
    private static final Identifier NECTAR_TEXTURE = Identifier.ofVanilla("textures/entity/bee/bee_nectar.png");

    public PetBeeRenderer(EntityRendererFactory.Context context) {
        super(context,
            new BeeEntityModel(context.getPart(EntityModelLayers.BEE)),
            new BeeEntityModel(context.getPart(EntityModelLayers.BEE_BABY)),
            0.4F);
    }

    @Override
    public Identifier getTexture(BeeEntityRenderState state) {
        // Pet bees are never angry; show nectar variant when carrying pollen
        return state.hasNectar ? NECTAR_TEXTURE : TEXTURE;
    }

    @Override
    public BeeEntityRenderState createRenderState() { return new BeeEntityRenderState(); }

    @Override
    public void updateRenderState(PetBeeEntity entity, BeeEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.bodyPitch      = entity.getBodyPitch(tickDelta);
        state.hasStinger     = true;   // pet bees never sting
        state.stoppedOnGround = entity.isOnGround() && entity.getVelocity().lengthSquared() < 1.0E-7;
        state.angry          = false;  // pet bees are never angry
        state.hasNectar      = entity.hasNectar();
    }
}
