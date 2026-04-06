package net.zhengzhengyiyi.renderer.pet;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.BabyModelPair;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.client.render.entity.model.ColdChickenEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.ChickenEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.ChickenVariant;
import net.minecraft.entity.passive.ChickenVariant.Model;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.zhengzhengyiyi.entity.pet.PetChickenEntity;

/** class_11152 - Pet Chicken renderer */
@Environment(EnvType.CLIENT)
public class PetChickenRenderer extends MobEntityRenderer<PetChickenEntity, ChickenEntityRenderState, ChickenEntityModel> {
    private final Map<Model, BabyModelPair<ChickenEntityModel>> models;

    public PetChickenRenderer(EntityRendererFactory.Context context) {
        super(context, new ChickenEntityModel(context.getPart(EntityModelLayers.CHICKEN)), 0.3F);
        this.models = Maps.newEnumMap(Map.of(
            Model.NORMAL, new BabyModelPair<>(
                new ChickenEntityModel(context.getPart(EntityModelLayers.CHICKEN)),
                new ChickenEntityModel(context.getPart(EntityModelLayers.CHICKEN_BABY))),
            Model.COLD, new BabyModelPair<>(
                new ColdChickenEntityModel(context.getPart(EntityModelLayers.COLD_CHICKEN)),
                new ColdChickenEntityModel(context.getPart(EntityModelLayers.COLD_CHICKEN_BABY)))
        ));
    }

    @Override
    public void render(ChickenEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState camera) {
        if (state.variant != null) {
            this.model = this.models.get(state.variant.modelAndTexture().model()).get(state.baby);
            super.render(state, matrices, queue, camera);
        }
    }

    @Override
    public Identifier getTexture(ChickenEntityRenderState state) {
        return state.variant == null ? MissingSprite.getMissingSpriteId() : state.variant.modelAndTexture().asset().texturePath();
    }

    @Override
    public ChickenEntityRenderState createRenderState() { return new ChickenEntityRenderState(); }

    @Override
    public void updateRenderState(PetChickenEntity entity, ChickenEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.flapProgress = MathHelper.lerp(tickDelta, entity.field_58654, entity.field_58651);
        state.maxWingDeviation = MathHelper.lerp(tickDelta, entity.field_58653, entity.field_58652);
        state.variant = (ChickenVariant) entity.method_69383().value();
    }
}
