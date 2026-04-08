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
import net.minecraft.util.math.MathHelper;
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
        // Pet creepers are half-size; fuse animation still applies if fuseTime > 0
        float f = state.fuseTime;
        float g = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        f *= f; f *= f;
        float h = (1.0F + f * 0.4F) * g;
        float i = (1.0F + f * 0.1F) / g;
        matrices.scale(0.5F * h, 0.5F * i, 0.5F * h);
    }

    @Override
    protected float getAnimationCounter(CreeperEntityRenderState state) {
        float f = state.fuseTime;
        return (int)(f * 10.0F) % 2 == 0 ? 0.0F : MathHelper.clamp(f, 0.5F, 1.0F);
    }

    @Override
    public Identifier getTexture(CreeperEntityRenderState state) { return TEXTURE; }

    @Override
    public CreeperEntityRenderState createRenderState() { return new CreeperEntityRenderState(); }

    @Override
    public void updateRenderState(PetCreeperEntity entity, CreeperEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        // Pet creepers never fuse or charge — keep defaults (fuseTime=0, charged=false)
        state.fuseTime = 0.0F;
        state.charged  = false;
    }
}
