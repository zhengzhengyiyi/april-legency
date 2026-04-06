package net.zhengzhengyiyi.entity.task;

import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Map;

/**
 * class_10983 - Warden Acting Stage 4 Task
 * Final stage: builds structure and drops rewards.
 */
public class WardenActingStage4Task extends MultiTickTask<WardenEntity> {
    private static final int STAGE = 4;

    public WardenActingStage4Task() {
        super(Map.of(), 60, 120);
    }

    @Override
    protected boolean shouldRun(ServerWorld world, WardenEntity entity) {
        return method_69303(entity);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, WardenEntity entity, long time) {
        return method_69303(entity);
    }

    @Override
    protected void run(ServerWorld world, WardenEntity entity, long time) {
        entity.setPose(net.minecraft.entity.EntityPose.ROARING);
        entity.getLookControl().lookAt(entity.getX(), entity.getEyeY(), entity.getZ());
    }

    /** finishRunning - Builds structure and drops rewards */
    @Override
    protected void finishRunning(ServerWorld world, WardenEntity entity, long time) {
        BlockPos pos = entity.getBlockPos();
        // Drop experience and rewards at warden's position
        net.minecraft.entity.ExperienceOrbEntity.spawn(world, net.minecraft.util.math.Vec3d.ofCenter(pos), 50);
        entity.kill(world);
    }

    /** method_69303 - Checks stage */
    private static boolean method_69303(WardenEntity entity) {
        return entity.getAngriness().isAngry();
    }
}
