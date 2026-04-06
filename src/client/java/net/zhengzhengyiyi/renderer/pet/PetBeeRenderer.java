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
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/bee/bee.png");

    public PetBeeRenderer(EntityRendererFactory.Context context) {
        super(context,
            new BeeEntityModel(context.getPart(EntityModelLayers.BEE)),
            new BeeEntityModel(context.getPart(EntityModelLayers.BEE_BABY)),
            0.4F);
    }

    @Override
    public Identifier getTexture(BeeEntityRenderState state) { return TEXTURE; }

    @Override
    public BeeEntityRenderState createRenderState() { return new BeeEntityRenderState(); }
}
