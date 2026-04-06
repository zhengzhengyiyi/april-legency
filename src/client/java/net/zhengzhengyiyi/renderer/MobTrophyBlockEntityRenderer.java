package net.zhengzhengyiyi.renderer;

import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.zhengzhengyiyi.block.MobTrophyBlock;
import net.zhengzhengyiyi.block.MobTrophyBlockEntity;
import net.zhengzhengyiyi.component.MobTrophyComponent;
import org.jetbrains.annotations.Nullable;

/**
 * class_11146 - MobTrophy block entity renderer.
 * Renders a scaled entity model on the trophy block.
 */
@Environment(EnvType.CLIENT)
public class MobTrophyBlockEntityRenderer implements BlockEntityRenderer<MobTrophyBlockEntity> {
    private final EntityRenderDispatcher entityRenderDispatcher;

    public MobTrophyBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.entityRenderDispatcher = context.getEntityRenderDispatcher();
    }

    @Override
    public void render(MobTrophyBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vcp, int light, int overlay, Vec3d cameraPos) {
        MobTrophyComponent trophy = entity.getMobTrophy();
        if (trophy == null) return;
        Direction facing = entity.getCachedState().get(MobTrophyBlock.FACING);
        renderTrophy(matrices, vcp, light, trophy, this.entityRenderDispatcher, facing, entity.getWorld());
    }

    public static void renderTrophy(MatrixStack matrices, VertexConsumerProvider vcp, int light,
                                    MobTrophyComponent trophy, EntityRenderDispatcher dispatcher,
                                    Direction direction, @Nullable World world) {
        EntityType<?> type = (EntityType<?>) trophy.type().value();
        EntityRenderer<?, ?> renderer = dispatcher.method_70459(type);
        if (renderer == null) return;

        matrices.push();
        EntityDimensions dims = type.getDimensions();
        float scaleH = 0.875F / dims.height();
        float scaleW = 0.6875F / dims.width();
        float scale = Math.max(0.1F, Math.min(scaleH, scaleW) * 0.5F);
        if (!Float.isFinite(scale)) scale = 1.0F;

        float yaw = -direction.getPositiveHorizontalDegrees();
        matrices.translate(0.5F, 0.0625F, 0.5F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));
        matrices.scale(scale, scale, scale);
        renderEntityState(renderer, matrices, vcp, light, trophy.shiny(), world);
        matrices.pop();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity, S extends EntityRenderState> void renderEntityState(
        EntityRenderer<T, S> renderer, MatrixStack matrices, VertexConsumerProvider vcp,
        int light, boolean shiny, @Nullable World world) {

        VertexConsumerProvider vcpFinal = shiny
            ? renderLayer -> renderLayer.getVertexFormat().contains(VertexFormatElement.UV0)
                ? ItemRenderer.getItemGlintConsumer(vcp, renderLayer, false, ItemRenderState.Glint.STANDARD)
                : vcp.getBuffer(renderLayer)
            : vcp;

        S state = renderer.method_70457(world);
        renderer.render(state, matrices, vcpFinal, light);
    }
}
