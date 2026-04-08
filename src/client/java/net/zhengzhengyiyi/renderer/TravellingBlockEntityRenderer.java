package net.zhengzhengyiyi.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.zhengzhengyiyi.block.TravellingBlockEntity;

/**
 * class_11145 - TravellingBlock (mine portal) block entity renderer.
 */
@Environment(EnvType.CLIENT)
public class TravellingBlockEntityRenderer implements BlockEntityRenderer<TravellingBlockEntity, TravellingBlockEntityRenderer.State> {
    private static final Identifier BEAM_TEXTURE = Identifier.ofVanilla("textures/entity/end_gateway_beam.png");
    public TravellingBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    public static class State extends BlockEntityRenderState {
        public boolean active;
        public boolean revisit;
        public float progress;
        public long age;
        public int topY;
    }

    @Override
    public State createRenderState() { return new State(); }

    @Override
    public void updateRenderState(TravellingBlockEntity entity, State state, float tickDelta,
                                  Vec3d cameraPos, ModelCommandRenderer.CrumblingOverlayCommand crumbling) {
        BlockEntityRenderState.updateBlockEntityRenderState(entity, state, crumbling);
        state.active = entity.isActive();
        state.revisit = entity.isRevisit();
        state.progress = entity.getProgress(tickDelta);
        state.age = entity.getAge();
        state.topY = entity.getWorld() != null ? entity.getWorld().getTopYInclusive() : 256;
    }

    @Override
    public void render(State state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState camera) {
        double beamHeight = state.active ? state.topY : 50.0;
        float sinProgress = MathHelper.sin(state.progress * (float) Math.PI);
        int beamLength = MathHelper.floor(sinProgress * beamHeight);
        int color = state.revisit ? DyeColor.LIGHT_BLUE.getEntityColor() : DyeColor.BLUE.getEntityColor();

        BeaconBlockEntityRenderer.renderBeam(matrices, queue, BEAM_TEXTURE,
            1.0F, sinProgress, (int)(state.age & Integer.MAX_VALUE),
            -beamLength, beamLength * 2, color, 0.15F);

        matrices.push();
        double rot = state.age * 0.1;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float)(rot % (Math.PI * 2))), 0.5F, 0.0F, 0.5F);
        // Block rendering requires VertexConsumerProvider which is no longer available here.
        matrices.pop();
    }

    @Override
    public int getRenderDistance() { return 256; }
}
