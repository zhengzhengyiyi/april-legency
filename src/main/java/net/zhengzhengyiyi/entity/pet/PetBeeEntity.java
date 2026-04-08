package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

/**
 * class_11002 - Pet Bee
 */
public class PetBeeEntity extends BasePetEntity {

    private boolean flapping = false;
    private float wingFlapProgress = 0.0f;

    public PetBeeEntity(EntityType<? extends PetBeeEntity> entityType, World world) {
        super(entityType, world);
    }

    /** method_69333 - Creates attributes */
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.MAX_HEALTH, 10.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.3)
            .add(EntityAttributes.FLYING_SPEED, 0.6);
    }

    /** method_69331 - Checks if flapping */
    public boolean isFlapping() {
        return this.flapping;
    }

    /** method_69334 - Sets flapping */
    public void setFlapping(boolean flapping) {
        this.flapping = flapping;
    }

    /** method_69365 - Updates wing flapping */
    @Override
    public void tickMovement() {
        super.tickMovement();
        this.wingFlapProgress += this.flapping ? 0.3f : -0.1f;
        this.wingFlapProgress = Math.max(0.0f, Math.min(1.0f, this.wingFlapProgress));
    }

    /** method_69358 - Gets wing flap progress */
    public float getWingFlapProgress(float tickDelta) {
        return this.wingFlapProgress;
    }

    /** method_69366 - Checks if should flap */
    public boolean shouldFlap() {
        return !this.isOnGround() || this.getVelocity().lengthSquared() > 0.01;
    }

    /** method_69370 - Sets flapping flag */
    public void updateFlapping() {
        this.setFlapping(shouldFlap());
    }

    /** method_69371 - Pet bees never carry nectar */
    public boolean hasNectar() {
        return false;
    }

    /** method_69372 - Body pitch for flight animation */
    public float getBodyPitch(float tickDelta) {
        return 0.0F;
    }
}
