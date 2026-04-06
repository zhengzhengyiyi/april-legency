package net.zhengzhengyiyi.entity.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.zhengzhengyiyi.AprilsLegacy;

/**
 * class_10988 - Warden Acting Stage 0 Task
 * Stage 0: plays sound, on finish advances to stage 1.
 */
public class WardenActingStage0Task<E extends WardenEntity> extends MultiTickTask<E> {
    final Vec3d field_58553;
    final EntityPose field_58554;

    public WardenActingStage0Task(int duration, Vec3d vec3d, EntityPose entityPose) {
        super(ImmutableMap.of(), duration);
        this.field_58553 = vec3d;
        this.field_58554 = entityPose;
    }

    @Override
    protected boolean shouldRun(ServerWorld world, E entity) {
        return !entity.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET)
            && entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).isPresent()
            && entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).get() == 0;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, E entity, long time) {
        return !entity.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET)
            && entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).isPresent()
            && entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).get() == 0;
    }

    @Override
    protected void run(ServerWorld world, E entity, long time) {
        entity.setPose(this.field_58554);
        entity.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, entity.getEntityPos().add(-1.0, 0.0, 0.0));
        world.playSound(null, entity.getBlockPos(), AprilsLegacy.field_58484, SoundCategory.MASTER, 10.0F, 1.0F);
    }

    @Override
    protected void finishRunning(ServerWorld world, E entity, long time) {
        if (method_69324(entity)) {
            WalkTarget walkTarget = new WalkTarget(entity.getEntityPos().add(this.field_58553), 0.7F, 0);
            entity.getBrain().remember(MemoryModuleType.WALK_TARGET, walkTarget);
            entity.getBrain().remember(ModMemoryModuleTypes.ACTING_STAGE, 1);
        } else {
            if (world.getPlayers().size() > 0) {
                world.getPlayers().get(0).sendMessageToClient(
                    net.minecraft.text.Text.literal("Even the Warden got too tired of waiting for the music to start..."), true);
            }
            entity.stopRiding();
        }
    }

    /** method_69324 - Checks if stage advanced to 1 */
    boolean method_69324(E entity) {
        return entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).isPresent()
            && entity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTING_STAGE).get() == 1;
    }
}
