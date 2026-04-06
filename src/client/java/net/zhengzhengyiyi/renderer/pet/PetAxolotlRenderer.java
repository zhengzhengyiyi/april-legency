package net.zhengzhengyiyi.renderer.pet;

import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.AxolotlEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.AxolotlEntityRenderState;
import net.minecraft.entity.passive.AxolotlEntity.Variant;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.zhengzhengyiyi.entity.pet.PetAxolotlEntity;

/** class_11149 - Pet Axolotl renderer */
@Environment(EnvType.CLIENT)
public class PetAxolotlRenderer extends AgeableMobEntityRenderer<PetAxolotlEntity, AxolotlEntityRenderState, AxolotlEntityModel> {
    private static final Map<Variant, Identifier> TEXTURES = Util.make(Maps.newHashMap(), map -> {
        for (Variant v : Variant.values()) {
            map.put(v, Identifier.ofVanilla(String.format(Locale.ROOT, "textures/entity/axolotl/axolotl_%s.png", v.getId())));
        }
    });

    public PetAxolotlRenderer(EntityRendererFactory.Context context) {
        super(context,
            new AxolotlEntityModel(context.getPart(EntityModelLayers.AXOLOTL)),
            new AxolotlEntityModel(context.getPart(EntityModelLayers.AXOLOTL_BABY)),
            0.5F);
    }

    @Override
    public Identifier getTexture(AxolotlEntityRenderState state) { return TEXTURES.get(state.variant); }

    @Override
    public AxolotlEntityRenderState createRenderState() { return new AxolotlEntityRenderState(); }

    @Override
    public void updateRenderState(PetAxolotlEntity entity, AxolotlEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.variant = entity.getVariant();
        state.inWaterValue = entity.field_58597.getValue(tickDelta);
        state.onGroundValue = entity.field_58598.getValue(tickDelta);
        state.isMovingValue = entity.field_58599.getValue(tickDelta);
    }
}
