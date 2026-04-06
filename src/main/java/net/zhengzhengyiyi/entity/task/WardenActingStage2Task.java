package net.zhengzhengyiyi.entity.task;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.Map;

/**
 * class_10987 - Warden Acting Stage 2 Task (The Worm)
 * Stage 2: sends message and handles sleeping pose.
 */
public class WardenActingStage2Task extends MultiTickTask<WardenEntity> {
    private boolean messageSent = false;

    public WardenActingStage2Task() {
        super(Map.of(), 60, 120);
    }

    @Override
    protected boolean shouldRun(ServerWorld world, WardenEntity entity) {
        return method_69318(entity);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, WardenEntity entity, long time) {
        return method_69318(entity);
    }

    @Override
    protected void run(ServerWorld world, WardenEntity entity, long time) {
        entity.setPose(EntityPose.STANDING);
        entity.getLookControl().lookAt(entity.getX(), entity.getEyeY(), entity.getZ());
        this.messageSent = false;
    }

    /** keepRunning - Handles sleeping pose and sends message */
    @Override
    protected void keepRunning(ServerWorld world, WardenEntity entity, long time) {
        if (!this.messageSent && getRunningTime(time) > 20) {
            world.getPlayers().forEach(p ->
                p.sendMessage(Text.translatable("entity.minecraft.warden.acting"), true));
            this.messageSent = true;
        }
    }

    /** finishRunning - Sets walk target and stage */
    @Override
    protected void finishRunning(ServerWorld world, WardenEntity entity, long time) {
        entity.getBrain().remember(MemoryModuleType.WALK_TARGET,
            new net.minecraft.entity.ai.brain.WalkTarget(entity.getBlockPos(), 0.6f, 1));
    }

    /** method_69318 - Checks stage */
    private static boolean method_69318(WardenEntity entity) {
        return entity.getAnger() >= 80 && entity.getAnger() < 120;
    }

    private long getRunningTime(long currentTime) {
        return currentTime;
    }
}
