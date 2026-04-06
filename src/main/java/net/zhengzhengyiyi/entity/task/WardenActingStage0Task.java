package net.zhengzhengyiyi.entity.task;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

import java.util.Map;

/**
 * class_10988 - Warden Acting Stage 0 Task
 * Stage 0: sets pose and plays sound.
 */
public class WardenActingStage0Task extends MultiTickTask<WardenEntity> {

    public WardenActingStage0Task() {
        super(Map.of(), 20, 40);
    }

    @Override
    protected boolean shouldRun(ServerWorld world, WardenEntity entity) {
        return method_69324(entity);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, WardenEntity entity, long time) {
        return method_69324(entity);
    }

    /** run - Sets pose and plays sound */
    @Override
    protected void run(ServerWorld world, WardenEntity entity, long time) {
        entity.setPose(EntityPose.EMERGING);
        entity.getLookControl().lookAt(entity.getX(), entity.getEyeY(), entity.getZ());
        world.playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_WARDEN_AMBIENT, entity.getSoundCategory(), 1.0f, 1.0f);
    }

    /** finishRunning - Sets walk target and stage */
    @Override
    protected void finishRunning(ServerWorld world, WardenEntity entity, long time) {
        entity.getBrain().remember(MemoryModuleType.WALK_TARGET,
            new net.minecraft.entity.ai.brain.WalkTarget(entity.getBlockPos(), 0.4f, 1));
    }

    /** method_69324 - Checks stage */
    private static boolean method_69324(WardenEntity entity) {
        return entity.getAnger() == 0;
    }
}
