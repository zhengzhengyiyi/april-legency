package net.zhengzhengyiyi.renderer;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.zhengzhengyiyi.component.MobTrophyComponent;
import org.jetbrains.annotations.Nullable;

/**
 * class_11165 - MobTrophy item special model renderer.
 * Renders the entity model when the trophy is held/displayed as an item.
 */
@Environment(EnvType.CLIENT)
public class MobTrophyItemRenderer implements SpecialModelRenderer<MobTrophyComponent> {
    @Override
    public void render(@Nullable MobTrophyComponent trophy, ItemDisplayContext displayContext,
                       MatrixStack matrices, VertexConsumerProvider vcp, int light, int overlay, boolean leftHanded) {
        if (trophy == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        EntityRenderDispatcher dispatcher = client.getEntityRenderDispatcher();
        MobTrophyBlockEntityRenderer.renderTrophy(matrices, vcp, light, trophy, dispatcher, Direction.NORTH, client.world);
    }

    @Nullable
    @Override
    public MobTrophyComponent getData(ItemStack stack) {
        return stack.get(DataComponentTypes.TYPE_MOB_TROPHY);
    }

    @Environment(EnvType.CLIENT)
    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public MapCodec<Unbaked> getCodec() { return CODEC; }

        @Override
        public SpecialModelRenderer<?> bake(LoadedEntityModels entityModels) {
            return new MobTrophyItemRenderer();
        }
    }
}
