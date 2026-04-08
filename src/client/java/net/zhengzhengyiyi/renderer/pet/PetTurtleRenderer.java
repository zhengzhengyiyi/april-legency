package net.zhengzhengyiyi.renderer.pet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.TurtleEntityModel;
import net.minecraft.client.render.entity.state.TurtleEntityRenderState;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.entity.pet.PetTurtleEntity;

/** class_11160 - Pet Turtle renderer */
@Environment(EnvType.CLIENT)
public class PetTurtleRenderer extends MobEntityRenderer<PetTurtleEntity, TurtleEntityRenderState, TurtleEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/turtle/big_sea_turtle.png");

    public PetTurtleRenderer(EntityRendererFactory.Context context) {
        super(context, new TurtleEntityModel(context.getPart(EntityModelLayers.TURTLE)), 0.7F);
    }

    @Override
    public TurtleEntityRenderState createRenderState() { return new TurtleEntityRenderState(); }

    @Override
    public void updateRenderState(PetTurtleEntity entity, TurtleEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.onLand      = !entity.isTouchingWater() && entity.isOnGround();
        state.diggingSand = false;  // PetTurtleEntity never lays eggs
        state.hasEgg      = false;  // PetTurtleEntity never carries eggs
    }

    @Override
    public Identifier getTexture(TurtleEntityRenderState state) { return TEXTURE; }
}
