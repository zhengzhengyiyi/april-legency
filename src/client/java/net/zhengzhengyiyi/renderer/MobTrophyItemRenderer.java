package net.zhengzhengyiyi.renderer;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.zhengzhengyiyi.component.MobTrophyComponent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import org.joml.Vector3fc;

/**
 * class_11165 - MobTrophy item special model renderer.
 */
@Environment(EnvType.CLIENT)
public class MobTrophyItemRenderer implements SpecialModelRenderer<MobTrophyComponent> {
    @Override
    public void render(@Nullable MobTrophyComponent trophy, ItemDisplayContext displayContext,
                       MatrixStack matrices, OrderedRenderCommandQueue queue,
                       int light, int overlay, boolean leftHanded, int seed) {
        // Trophy entity rendering requires the new render pipeline; currently a no-op stub.
    }

    @Override
    public void collectVertices(Consumer<Vector3fc> consumer) {}

    @Override
    @Nullable
    public MobTrophyComponent getData(ItemStack stack) {
        return stack.get(net.zhengzhengyiyi.component.ModDataComponentTypes.TYPE_MOB_TROPHY);
    }

    @Environment(EnvType.CLIENT)
    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public MapCodec<Unbaked> getCodec() { return CODEC; }

        @Override
        public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
            return new MobTrophyItemRenderer();
        }
    }
}
