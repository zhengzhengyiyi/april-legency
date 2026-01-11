package net.zhengzhengyiyi.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.state.CowEntityRenderState;
import net.minecraft.util.Identifier;
import net.zhengzhengyiyi.entity.MoonCowEntity;

@Environment(EnvType.CLIENT)
public class MoonCowEntityRenderer extends MobEntityRenderer<MoonCowEntity, CowEntityRenderState, MoonCowEntityRenderer.MoonCowModel> {
    private static final Identifier TEXTURE = Identifier.of("zhengzhengyiyi", "textures/entity/cow/moon_cow.png");

    @SuppressWarnings({"unchecked", "rawtypes"})
	public MoonCowEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new MoonCowModel(context.getPart(EntityModelLayers.COW)), 0.7F);
        this.addFeature(new HeadFeatureRenderer(this, context.getEntityModels(), context.getPlayerSkinCache()));
    }

    @Override
    public CowEntityRenderState createRenderState() {
        return new CowEntityRenderState();
    }

    @Override
    public Identifier getTexture(CowEntityRenderState state) {
        return TEXTURE;
    }

    @Environment(EnvType.CLIENT)
    public static class MoonCowModel extends CowEntityModel implements ModelWithHead {
        private final ModelPart head;

        public MoonCowModel(ModelPart root) {
            super(root);
            this.head = root.getChild("head");
        }

        @Override
        public ModelPart getHead() {
            return this.head;
        }
    }
}
