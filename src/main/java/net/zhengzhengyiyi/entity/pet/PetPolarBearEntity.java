package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

/**
 * class_11020 - Pet Polar Bear
 */
public class PetPolarBearEntity extends BasePetEntity {

    private boolean standing = false;
    private float standProgress = 0.0f;

    public PetPolarBearEntity(EntityType<? extends PetPolarBearEntity> entityType, World world) {
        super(entityType, world);
    }

    /** method_69405 - Creates attributes */
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.MAX_HEALTH, 30.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.25);
    }

    /** method_69404 - Spawn check */
    public static boolean method_69404() {
        return true;
    }

    /** method_69406 - Plays warning sound */
    public void playWarningSound() {
        this.playSound(SoundEvents.ENTITY_POLAR_BEAR_WARNING, 1.0f, 1.0f);
    }

    /** method_69407 - Checks if standing */
    public boolean isStanding() {
        return this.standing;
    }

    /** method_69408 - Sets standing */
    public void setStanding(boolean standing) {
        this.standing = standing;
    }

    /** method_69402 - Gets stand progress */
    public float getStandProgress(float tickDelta) {
        return this.standProgress;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        float target = this.standing ? 1.0f : 0.0f;
        this.standProgress += (target - this.standProgress) * 0.2f;
    }
}
