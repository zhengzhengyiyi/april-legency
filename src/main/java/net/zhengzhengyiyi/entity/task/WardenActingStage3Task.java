package net.zhengzhengyiyi.entity.task;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;

/**
 * class_10986 - Warden Acting Stage 3 Task
 * Stage 3: similar to stage 1 but for higher anger level.
 */
public class WardenActingStage3Task extends MultiTickTask<WardenEntity> {

    public WardenActingStage3Task() {
        super(Map.of(), 40, 80);
    }

    @Override
    protected boolean shouldRun(ServerWorld world, WardenEntity entity) {
        return method_69308(entity);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, WardenEntity entity, long time) {
        return method_69308(entity);
    }

    @Override
    protected void run(ServerWorld world, WardenEntity entity, long time) {
        entity.setPose(EntityPose.STANDING);
        entity.getLookControl().lookAt(entity.getX(), entity.getEyeY(), entity.getZ());
    }

    /** finishRunning - Sets walk target and stage */
    @Override
    protected void finishRunning(ServerWorld world, WardenEntity entity, long time) {
        entity.getBrain().remember(MemoryModuleType.WALK_TARGET,
            new net.minecraft.entity.ai.brain.WalkTarget(entity.getBlockPos(), 0.7f, 1));
    }

    /** method_69308 - Checks stage */
    private static boolean method_69308(WardenEntity entity) {
        return entity.getAnger() >= 40 && entity.getAnger() < 80;
    }
}
