package net.zhengzhengyiyi.entity.task;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;

/**
 * class_10984 - Warden Acting Stage -1 Task
 * Pre-stage: clears memory and targets.
 */
public class WardenActingStageNeg1Task extends MultiTickTask<WardenEntity> {
    private static final int STAGE = -1;

    public WardenActingStageNeg1Task() {
        super(Map.of(), 20, 40);
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
        entity.setPose(EntityPose.EMERGING);
        entity.getLookControl().lookAt(entity.getX(), entity.getEyeY(), entity.getZ());
    }

    /** finishRunning - Clears memory and targets */
    @Override
    protected void finishRunning(ServerWorld world, WardenEntity entity, long time) {
        entity.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
        entity.getBrain().forget(MemoryModuleType.ANGRY_AT);
    }

    /** method_69303 - Checks stage */
    private static boolean method_69303(WardenEntity entity) {
        return !entity.getAngriness().isAngry();
    }
}
