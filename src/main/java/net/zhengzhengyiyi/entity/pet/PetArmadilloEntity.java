package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/** class_10996 - Pet Armadillo */
public class PetArmadilloEntity extends BasePetEntity {
    private static final TrackedData<ArmadilloEntity.State> field_58586 =
        DataTracker.registerData(PetArmadilloEntity.class, TrackedDataHandlerRegistry.ARMADILLO_STATE);
    private long field_58587 = 0L;
    public final AnimationState field_58583 = new AnimationState(); // unrolling
    public final AnimationState field_58584 = new AnimationState(); // rolling
    public final AnimationState field_58585 = new AnimationState(); // scared

    public PetArmadilloEntity(EntityType<? extends PetArmadilloEntity> entityType, World world) {
        super(entityType, world);
        this.getNavigation().setCanSwim(true);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return AnimalEntity.createAnimalAttributes()
            .add(EntityAttributes.MAX_HEALTH, 12.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.14);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(field_58586, ArmadilloEntity.State.IDLE);
    }

    public boolean isMoving() { return this.getVelocity().horizontalLengthSquared() > 0.01; }

    /** method_69346 - Checks if rolled up */
    public boolean isRolledUp() { return method_69347().isRolledUp(this.field_58587); }

    /** method_69347 - Gets state */
    public ArmadilloEntity.State method_69347() { return this.dataTracker.get(field_58586); }

    /** method_69342 - Sets state */
    public void method_69342(ArmadilloEntity.State state) { this.dataTracker.set(field_58586, state); }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (field_58586.equals(data)) this.field_58587 = 0L;
        super.onTrackedDataSet(data);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getEntityWorld().isClient()) method_69349();
        if (method_69347() != ArmadilloEntity.State.IDLE) this.clampHeadYaw();
        this.field_58587++;
    }

    private void method_69349() {
        switch (method_69347()) {
            case IDLE -> { field_58583.stop(); field_58584.stop(); field_58585.stop(); }
            case UNROLLING -> { field_58583.startIfNotRunning(this.age); field_58584.stop(); field_58585.stop(); }
            case ROLLING -> { field_58583.stop(); field_58584.startIfNotRunning(this.age); field_58585.stop(); }
            default -> { field_58583.stop(); field_58584.stop(); field_58585.startIfNotRunning(this.age); }
        }
    }

    public boolean method_69348() {
        return !this.isPanicking() && !this.isInFluid() && !this.isLeashed() && !this.hasVehicle() && !this.hasPassengers();
    }

    @Override @Nullable
    public PetArmadilloEntity createChild(ServerWorld world, PassiveEntity entity) { return null; }
}
