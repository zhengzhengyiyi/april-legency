package net.zhengzhengyiyi.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.zhengzhengyiyi.block.MobTrophyBlock;
import net.zhengzhengyiyi.block.MobTrophyBlockEntity;
import net.zhengzhengyiyi.component.MobTrophyComponent;
import org.jetbrains.annotations.Nullable;

/**
 * class_11146 - MobTrophy block entity renderer.
 */
@Environment(EnvType.CLIENT)
public class MobTrophyBlockEntityRenderer implements BlockEntityRenderer<MobTrophyBlockEntity, MobTrophyBlockEntityRenderer.State> {
    public MobTrophyBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    public static class State extends BlockEntityRenderState {
        @Nullable public MobTrophyComponent trophy;
        public Direction facing = Direction.NORTH;
    }

    @Override
    public State createRenderState() { return new State(); }

    @Override
    public void updateRenderState(MobTrophyBlockEntity entity, State state, float tickDelta,
                                  Vec3d cameraPos, ModelCommandRenderer.CrumblingOverlayCommand crumbling) {
        BlockEntityRenderState.updateBlockEntityRenderState(entity, state, crumbling);
        state.trophy = entity.getMobTrophy();
        state.facing = entity.getCachedState().get(MobTrophyBlock.FACING);
    }

    @Override
    public void render(State state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState camera) {
        // TODO: state.trophy and state.facing are populated but rendering requires
        // entity-based render pipeline support not yet available here.
        // Implement once EntityRenderManager integration is available.
    }
}
