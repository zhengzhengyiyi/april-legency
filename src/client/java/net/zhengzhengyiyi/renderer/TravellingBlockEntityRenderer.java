package net.zhengzhengyiyi.renderer;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.zhengzhengyiyi.block.TravellingBlockEntity;

/**
 * class_11145 - TravellingBlock (mine portal) block entity renderer.
 * Renders a spinning block with a beacon beam.
 */
@Environment(EnvType.CLIENT)
public class TravellingBlockEntityRenderer implements BlockEntityRenderer<TravellingBlockEntity> {
    private static final Identifier BEAM_TEXTURE = Identifier.ofVanilla("textures/entity/end_gateway_beam.png");
    private final BlockRenderManager blockRenderManager;

    public TravellingBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.blockRenderManager = context.getRenderManager();
    }

    @Override
    public void render(TravellingBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vcp, int light, int overlay, Vec3d cameraPos) {
        double beamHeight = entity.isActive() ? entity.getWorld().getTopYInclusive() : 50.0;
        float progress = entity.getProgress(tickDelta);
        float sinProgress = MathHelper.sin(progress * (float) Math.PI);
        int beamLength = MathHelper.floor(sinProgress * beamHeight);
        int color = entity.isRevisit() ? DyeColor.LIGHT_BLUE.getEntityColor() : DyeColor.BLUE.getEntityColor();
        long time = entity.getWorld().getTime();

        BeaconBlockEntityRenderer.renderBeam(matrices, vcp, BEAM_TEXTURE, tickDelta, sinProgress, time,
            -beamLength, beamLength * 2, color, 0.15F, 0.175F);

        matrices.push();
        double rot = ((float) entity.getAge() + tickDelta) * 0.1;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float)(rot % (Math.PI * 2))), 0.5F, 0.0F, 0.5F);

        BlockState state = Blocks.MINE_TRAVELLING_BLOCK.getDefaultState();
        List<BlockModelPart> parts = this.blockRenderManager.getModel(state)
            .getParts(Random.create(state.getRenderingSeed(entity.getPos())));
        this.blockRenderManager.getModelRenderer().render(
            entity.getWorld(), parts, state, entity.getPos(), matrices,
            vcp.getBuffer(RenderLayers.getMovingBlockLayer(state)), false, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }

    @Override
    public int getRenderDistance() { return 256; }
}
