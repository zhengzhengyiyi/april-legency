package net.zhengzhengyiyi.renderer.pet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.CreeperChargeFeatureRenderer;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.CreeperEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.entity.pet.PetCreeperEntity;

/** class_11154 - Pet Creeper renderer */
@Environment(EnvType.CLIENT)
public class PetCreeperRenderer extends MobEntityRenderer<PetCreeperEntity, CreeperEntityRenderState, CreeperEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/creeper/creeper.png");

    public PetCreeperRenderer(EntityRendererFactory.Context context) {
        super(context, new CreeperEntityModel(context.getPart(EntityModelLayers.CREEPER)), 0.2F);
        this.addFeature(new CreeperChargeFeatureRenderer(this, context.getEntityModels()));
    }

    @Override
    protected void scale(CreeperEntityRenderState state, MatrixStack matrices) {
        matrices.scale(0.5F, 0.5F, 0.5F);
    }

    @Override
    public Identifier getTexture(CreeperEntityRenderState state) { return TEXTURE; }

    @Override
    public CreeperEntityRenderState createRenderState() { return new CreeperEntityRenderState(); }
}
