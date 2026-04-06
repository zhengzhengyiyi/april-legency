package net.zhengzhengyiyi.renderer.pet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.client.render.entity.state.SlimeEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.zhengzhengyiyi.entity.pet.PetSlimeEntity;

/** class_11159 - Pet Slime renderer */
@Environment(EnvType.CLIENT)
public class PetSlimeRenderer extends MobEntityRenderer<PetSlimeEntity, SlimeEntityRenderState, SlimeEntityModel> {
    public static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/slime/slime.png");

    public PetSlimeRenderer(EntityRendererFactory.Context context) {
        super(context, new SlimeEntityModel(context.getPart(EntityModelLayers.SLIME)), 0.25F);
        this.addFeature(new SlimeOverlayFeatureRenderer(this, context.getEntityModels()));
    }

    @Override
    protected float getShadowRadius(SlimeEntityRenderState state) { return state.size * 0.25F; }

    @Override
    protected void scale(SlimeEntityRenderState state, MatrixStack matrices) {
        float g = state.size;
        float h = state.stretch / (g * 0.5F + 1.0F);
        float i = 1.0F / (h + 1.0F);
        matrices.scale(0.999F, 0.999F, 0.999F);
        matrices.translate(0.0F, 0.001F, 0.0F);
        matrices.scale(i * g, 1.0F / i * g, i * g);
    }

    @Override
    public Identifier getTexture(SlimeEntityRenderState state) { return TEXTURE; }

    @Override
    public SlimeEntityRenderState createRenderState() { return new SlimeEntityRenderState(); }

    @Override
    public void updateRenderState(PetSlimeEntity entity, SlimeEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.stretch = MathHelper.lerp(tickDelta, entity.field_58703, entity.field_58702);
        state.size = entity.getSize();
    }
}
