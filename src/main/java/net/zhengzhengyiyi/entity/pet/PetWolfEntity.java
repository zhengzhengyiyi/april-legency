package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.WolfSoundVariant;
import net.minecraft.entity.passive.WolfVariant;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * class_11032 - Pet Wolf
 */
public class PetWolfEntity extends BasePetEntity {

    private RegistryEntry<WolfVariant> variant;
    private RegistryEntry<WolfSoundVariant> soundVariant;
    private DyeColor collarColor = DyeColor.RED;
    private boolean interested = false;
    private float shakeProgress = 0.0f;
    private float prevShakeProgress = 0.0f;
    private float tailAngle = 0.0f;

    public PetWolfEntity(EntityType<? extends PetWolfEntity> entityType, World world) {
        super(entityType, world);
    }

    /** method_69435 - Creates attributes */
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.MAX_HEALTH, 20.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.3)
            .add(EntityAttributes.ATTACK_DAMAGE, 4.0);
    }

    /** method_69434 - Gets texture */
    public net.minecraft.util.Identifier getTexture() {
        return net.minecraft.util.Identifier.ofVanilla("textures/entity/wolf/wolf.png");
    }

    /** method_69438 - Gets variant */
    public RegistryEntry<WolfVariant> getVariant() {
        return this.variant;
    }

    /** method_69429 - Sets variant */
    public void setVariant(RegistryEntry<WolfVariant> variant) {
        this.variant = variant;
    }

    /** method_69439 - Gets sound variant */
    public RegistryEntry<WolfSoundVariant> getSoundVariant() {
        return this.soundVariant;
    }

    /** method_69432 - Sets sound variant */
    public void setSoundVariant(RegistryEntry<WolfSoundVariant> soundVariant) {
        this.soundVariant = soundVariant;
    }

    /** method_69436 - Gets collar color */
    public DyeColor getCollarColor() {
        return this.collarColor;
    }

    /** method_69428 - Sets collar color */
    public void setCollarColor(DyeColor color) {
        this.collarColor = color;
    }

    /** method_69437 - Checks if interested */
    public boolean isInterested() {
        return this.interested;
    }

    /** method_69442 - Sets interested */
    public void setInterested(boolean interested) {
        this.interested = interested;
    }

    /** method_69441 - Checks if has armor */
    public boolean hasArmor() {
        return !this.getBodyArmor().isEmpty();
    }

    /** method_69440 - Stops shaking */
    public void stopShaking() {
        this.shakeProgress = 0.0f;
        this.prevShakeProgress = 0.0f;
    }

    /** method_69424 - Gets shake scale */
    public float getShakeScale(float tickDelta) {
        return (this.prevShakeProgress + (this.shakeProgress - this.prevShakeProgress) * tickDelta) / 1.8f;
    }

    /** method_69425 - Gets shake progress */
    public float getShakeProgress(float tickDelta) {
        return this.prevShakeProgress + (this.shakeProgress - this.prevShakeProgress) * tickDelta;
    }

    /** method_69426 - Gets tail angle */
    public float getTailAngle() {
        return this.tailAngle;
    }
}
