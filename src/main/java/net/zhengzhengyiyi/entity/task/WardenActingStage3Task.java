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
 * class_10986 - Warden Acting Stage 3 Task
 * Stage 3: on finish sets walk target and advances to stage 4.
 */
public class WardenActingStage3Task<E extends WardenEntity> extends MultiTickTask<E> {
    final Vec3d field_58544;
    final EntityPose field_58545;

    public WardenActingStage3Task(int duration, Vec3d vec3d, EntityPose entityPose) {
        super(ImmutableMap.of(), duration);
        this.field_58544 = vec3d;
        this.field_58545 = entityPose;
    }

    @Override
    protected boolean shouldRun(ServerWorld world, E entity) {
        return !entity.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET)
            && entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).isPresent()
            && entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).get() == 3;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, E entity, long time) {
        return !entity.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET)
            && entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).isPresent()
            && entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).get() == 3;
    }

    @Override
    protected void run(ServerWorld world, E entity, long time) {
        entity.setPose(this.field_58545);
        entity.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, entity.getEntityPos().add(-1.0, 0.0, 0.0));
    }

    @Override
    protected void finishRunning(ServerWorld world, E entity, long time) {
        if (method_69313(entity)) {
            WalkTarget walkTarget = new WalkTarget(entity.getEntityPos().add(this.field_58544), 0.7F, 0);
            entity.getBrain().remember(MemoryModuleType.WALK_TARGET, walkTarget);
            entity.getBrain().remember(ModMemoryModuleTypes.ACTING_STAGE, 4);
        } else {
            entity.stopRiding();
        }
    }

    /** method_69313 - Checks if stage advanced to 4 */
    boolean method_69313(E entity) {
        return entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).isPresent()
            && entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).get() == 4;
    }
}
