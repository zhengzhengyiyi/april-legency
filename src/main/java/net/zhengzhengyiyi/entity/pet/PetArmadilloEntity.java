package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

/**
 * class_10996 - Pet Armadillo
 */
public class PetArmadilloEntity extends BasePetEntity {

    private State armadilloState = State.IDLE;

    public PetArmadilloEntity(EntityType<? extends PetArmadilloEntity> entityType, World world) {
        super(entityType, world);
    }

    /** method_69344 - Creates attributes */
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.MAX_HEALTH, 12.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.14);
    }

    /** method_69345 - Checks if moving */
    public boolean isMoving() {
        return this.getVelocity().horizontalLengthSquared() > 0.01;
    }

    /** method_69346 - Checks if rolled up */
    public boolean isRolledUp() {
        return this.armadilloState == State.ROLLED;
    }

    /** method_69347 - Gets state */
    public State getArmadilloState() {
        return this.armadilloState;
    }

    /** method_69342 - Sets state */
    public void setArmadilloState(State state) {
        this.armadilloState = state;
    }

    /** method_69349 - Updates animations */
    @Override
    public void tickMovement() {
        super.tickMovement();
        // Update roll animation based on state
    }

    /** method_69348 - Checks if can roll */
    public boolean canRoll() {
        return !this.isSitting() && !this.isMoving();
    }

    @Override
    @Nullable
    public PetArmadilloEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
        return null;
    }

    public enum State implements StringIdentifiable {
        IDLE("idle"), ROLLING("rolling"), ROLLED("rolled"), UNROLLING("unrolling");

        private final String id;
        State(String id) { this.id = id; }

        @Override
        public String asString() { return this.id; }
    }
}
