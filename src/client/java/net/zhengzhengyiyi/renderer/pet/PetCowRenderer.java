package net.zhengzhengyiyi.renderer.pet;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.BabyModelPair;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.CowEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.CowVariant;
import net.minecraft.entity.passive.CowVariant.Model;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.entity.pet.PetCowEntity;

/** class_11153 - Pet Cow renderer */
@Environment(EnvType.CLIENT)
public class PetCowRenderer extends MobEntityRenderer<PetCowEntity, CowEntityRenderState, CowEntityModel> {
    private final Map<Model, BabyModelPair<CowEntityModel>> models;

    public PetCowRenderer(EntityRendererFactory.Context context) {
        super(context, new CowEntityModel(context.getPart(EntityModelLayers.COW)), 0.7F);
        this.models = Maps.newEnumMap(Map.of(
            Model.NORMAL, new BabyModelPair<>(
                new CowEntityModel(context.getPart(EntityModelLayers.COW)),
                new CowEntityModel(context.getPart(EntityModelLayers.COW_BABY))),
            Model.WARM, new BabyModelPair<>(
                new CowEntityModel(context.getPart(EntityModelLayers.WARM_COW)),
                new CowEntityModel(context.getPart(EntityModelLayers.WARM_COW_BABY))),
            Model.COLD, new BabyModelPair<>(
                new CowEntityModel(context.getPart(EntityModelLayers.COLD_COW)),
                new CowEntityModel(context.getPart(EntityModelLayers.COLD_COW_BABY)))
        ));
    }

    @Override
    public Identifier getTexture(CowEntityRenderState state) {
        return state.variant == null ? MissingSprite.getMissingSpriteId() : state.variant.modelAndTexture().asset().texturePath();
    }

    @Override
    public CowEntityRenderState createRenderState() { return new CowEntityRenderState(); }

    @Override
    public void updateRenderState(PetCowEntity entity, CowEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.variant = (CowVariant) entity.method_69385().value();
    }

    @Override
    public void render(CowEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState camera) {
        if (state.variant != null) {
            this.model = this.models.get(state.variant.modelAndTexture().model()).get(state.baby);
            super.render(state, matrices, queue, camera);
        }
    }
}
