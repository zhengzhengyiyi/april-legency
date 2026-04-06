package net.zhengzhengyiyi.renderer.pet;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.MooshroomMushroomFeatureRenderer;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.MooshroomEntityRenderState;
import net.minecraft.entity.passive.MooshroomEntity.Variant;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.zhengzhengyiyi.entity.pet.PetMooshroomEntity;

/** class_11157 - Pet Mooshroom renderer */
@Environment(EnvType.CLIENT)
public class PetMooshroomRenderer extends AgeableMobEntityRenderer<PetMooshroomEntity, MooshroomEntityRenderState, CowEntityModel> {
    private static final Map<Variant, Identifier> TEXTURES = Util.make(Maps.newHashMap(), map -> {
        map.put(Variant.BROWN, Identifier.ofVanilla("textures/entity/cow/brown_mooshroom.png"));
        map.put(Variant.RED,   Identifier.ofVanilla("textures/entity/cow/red_mooshroom.png"));
    });

    public PetMooshroomRenderer(EntityRendererFactory.Context context) {
        super(context,
            new CowEntityModel(context.getPart(EntityModelLayers.MOOSHROOM)),
            new CowEntityModel(context.getPart(EntityModelLayers.MOOSHROOM_BABY)),
            0.7F);
        this.addFeature(new MooshroomMushroomFeatureRenderer(this, context.getBlockRenderManager()));
    }

    @Override
    public Identifier getTexture(MooshroomEntityRenderState state) { return TEXTURES.get(state.type); }

    @Override
    public MooshroomEntityRenderState createRenderState() { return new MooshroomEntityRenderState(); }

    @Override
    public void updateRenderState(PetMooshroomEntity entity, MooshroomEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.type = entity.isBrownVariant() ? Variant.BROWN : Variant.RED;
    }
}
