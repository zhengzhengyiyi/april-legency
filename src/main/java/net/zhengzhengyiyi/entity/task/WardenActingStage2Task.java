package net.zhengzhengyiyi.entity.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

/**
 * class_10987 - Warden Acting Stage 2 Task ("The Worm")
 * Stage 2: sends "IT'S DOING THE WORM!" message, on finish advances to stage 3.
 */
public class WardenActingStage2Task<E extends WardenEntity> extends MultiTickTask<E> {
    final Vec3d field_58548;
    final EntityPose field_58549;
    public boolean field_58552 = false;

    public WardenActingStage2Task(int duration, Vec3d vec3d, EntityPose entityPose) {
        super(ImmutableMap.of(), duration);
        this.field_58548 = vec3d;
        this.field_58549 = entityPose;
    }

    @Override
    protected boolean shouldRun(ServerWorld world, E entity) {
        return !entity.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET)
            && entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).isPresent()
            && entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).get() == 2;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, E entity, long time) {
        if (world.getPlayers().size() > 0 && !this.field_58552) {
            this.field_58552 = true;
            world.getPlayers().get(0).sendMessageToClient(Text.literal("IT'S DOING THE WORM! THE WORM!"), true);
        }
        return !entity.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET)
            && entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).isPresent()
            && entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).get() == 2;
    }

    @Override
    protected void run(ServerWorld world, E entity, long time) {
        entity.setPose(this.field_58549);
        entity.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, entity.getEntityPos().add(-1.0, 0.0, 0.0));
    }

    @Override
    protected void keepRunning(ServerWorld world, E entity, long time) {
        if (!entity.getBrain().hasMemoryModule(MemoryModuleType.WALK_TARGET) && entity.getPose() == EntityPose.SLEEPING) {
            entity.setPose(EntityPose.STANDING);
        }
        super.keepRunning(world, entity, time);
    }

    @Override
    protected void finishRunning(ServerWorld world, E entity, long time) {
        if (method_69318(entity)) {
            WalkTarget walkTarget = new WalkTarget(entity.getEntityPos().add(this.field_58548), 0.7F, 0);
            entity.getBrain().remember(MemoryModuleType.WALK_TARGET, walkTarget);
            entity.getBrain().remember(ModMemoryModuleTypes.ACTING_STAGE, 3);
        } else {
            entity.stopRiding();
        }
    }

    /** method_69318 - Checks if stage advanced to 3 */
    boolean method_69318(E entity) {
        return entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).isPresent()
            && entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).get() == 3;
    }
}
