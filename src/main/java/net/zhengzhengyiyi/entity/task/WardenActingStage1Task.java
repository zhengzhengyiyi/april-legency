package net.zhengzhengyiyi.entity.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

/**
 * class_10985 - Warden Acting Stage 1 Task
 * Stage 1: on finish sets walk target and advances to stage 2.
 */
public class WardenActingStage1Task<E extends WardenEntity> extends MultiTickTask<E> {
    final Vec3d field_58540;
    final EntityPose field_58541;

    public WardenActingStage1Task(int duration, Vec3d vec3d, EntityPose entityPose) {
        super(ImmutableMap.of(), duration);
        this.field_58540 = vec3d;
        this.field_58541 = entityPose;
    }

    @Override
    protected boolean shouldRun(ServerWorld world, E entity) {
        return !entity.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET)
            && entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).isPresent()
            && entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).get() == 1;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, E entity, long time) {
        return !entity.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET)
            && entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).isPresent()
            && entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).get() == 1;
    }

    @Override
    protected void run(ServerWorld world, E entity, long time) {
        entity.setPose(this.field_58541);
        entity.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, entity.getEntityPos().add(-1.0, 0.0, 0.0));
    }

    @Override
    protected void finishRunning(ServerWorld world, E entity, long time) {
        if (method_69308(entity)) {
            WalkTarget walkTarget = new WalkTarget(entity.getEntityPos().add(this.field_58540), 0.7F, 0);
            entity.getBrain().remember(MemoryModuleType.WALK_TARGET, walkTarget);
            entity.getBrain().remember(ModMemoryModuleTypes.ACTING_STAGE, 2);
        } else {
            entity.stopRiding();
        }
    }

    /** method_69308 - Checks if stage advanced to 2 */
    boolean method_69308(E entity) {
        return entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).isPresent()
            && entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).get() == 2;
    }
}
