package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.entity.passive.FrogVariants;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/** class_11015 - Pet Frog */
public class PetFrogEntity extends BasePetEntity {
    private static final TrackedData<RegistryEntry<FrogVariant>> field_58676 =
        DataTracker.registerData(PetFrogEntity.class, TrackedDataHandlerRegistry.FROG_VARIANT);

    public final AnimationState field_58672 = new AnimationState(); // long jump
    public final AnimationState field_58673 = new AnimationState(); // croaking
    public final AnimationState field_58674 = new AnimationState(); // using tongue
    public final AnimationState field_58675 = new AnimationState(); // idling in water

    public PetFrogEntity(EntityType<? extends PetFrogEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return AnimalEntity.createAnimalAttributes()
            .add(EntityAttributes.MOVEMENT_SPEED, 3.0)
            .add(EntityAttributes.MAX_HEALTH, 10.0)
            .add(EntityAttributes.ATTACK_DAMAGE, 10.0)
            .add(EntityAttributes.STEP_HEIGHT, 1.0);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(field_58676, net.minecraft.entity.Variants.getOrDefaultOrThrow(this.getRegistryManager(), FrogVariants.TEMPERATE));
    }

    /** method_69397 - Gets variant */
    public RegistryEntry<FrogVariant> getVariant() { return this.dataTracker.get(field_58676); }

    /** method_69392 - Sets variant */
    public void setVariant(RegistryEntry<FrogVariant> variant) { this.dataTracker.set(field_58676, variant); }

    @Override
    public void tick() {
        super.tick();
        if (this.getEntityWorld().isClient()) {
            this.field_58675.setRunning(this.isTouchingWater() && !this.limbAnimator.isLimbMoving(), this.age);
        }
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (POSE.equals(data)) {
            EntityPose pose = this.getPose();
            if (pose == EntityPose.LONG_JUMPING) field_58672.start(this.age); else field_58672.stop();
            if (pose == EntityPose.CROAKING) field_58673.start(this.age); else field_58673.stop();
            if (pose == EntityPose.USING_TONGUE) field_58674.start(this.age); else field_58674.stop();
        }
        super.onTrackedDataSet(data);
    }

    @Override @Nullable
    public PetFrogEntity createChild(ServerWorld world, PassiveEntity entity) { return null; }
}
