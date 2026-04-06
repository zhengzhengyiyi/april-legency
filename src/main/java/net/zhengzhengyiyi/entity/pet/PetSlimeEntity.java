package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

/**
 * class_11024 - Pet Slime
 */
public class PetSlimeEntity extends BasePetEntity {

    private int size = 1;
    private int jumpDelay = 0;

    public PetSlimeEntity(EntityType<? extends PetSlimeEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.MAX_HEALTH, 4.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.2);
    }

    /** method_69410 - Sets size */
    public void setSize(int size) {
        this.size = Math.max(1, size);
    }

    /** method_69411 - Gets size */
    public int getSize() {
        return this.size;
    }

    /** method_69412 - Checks if tiny */
    public boolean isTiny() {
        return this.size == 1;
    }

    /** method_69413 - Gets particle effect */
    public ParticleEffect getParticleEffect() {
        return ParticleTypes.ITEM_SLIME;
    }

    /** method_69414 - Updates jump */
    public void updateJump() {
        if (this.jumpDelay > 0) {
            this.jumpDelay--;
        } else if (this.isOnGround()) {
            this.jump();
            this.jumpDelay = getJumpDelay();
        }
    }

    /** method_69415 - Gets jump delay */
    public int getJumpDelay() {
        return 20 + this.getRandom().nextInt(20);
    }

    /** method_69417 - Checks if can jump */
    public boolean canJump() {
        return this.isOnGround() && this.jumpDelay <= 0;
    }

    /** method_69419 - Gets jump sound pitch */
    public float getJumpSoundPitch() {
        return 1.0f / (this.getRandom().nextFloat() * 0.2f + 0.9f);
    }

    /** method_69418 - Gets jump sound */
    public SoundEvent getJumpSound() {
        return this.isTiny() ? SoundEvents.ENTITY_SLIME_JUMP_SMALL : SoundEvents.ENTITY_SLIME_JUMP;
    }

    /** method_69409 - Spawn check */
    public static boolean method_69409() {
        return true;
    }
}
