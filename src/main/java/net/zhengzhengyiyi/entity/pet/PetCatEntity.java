package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

/**
 * class_11007 - Pet Cat
 */
public class PetCatEntity extends BasePetEntity {

    private RegistryEntry<CatVariant> variant;
    private DyeColor collarColor = DyeColor.RED;

    public PetCatEntity(EntityType<? extends PetCatEntity> entityType, World world) {
        super(entityType, world);
    }

    /** method_69379 - Creates attributes */
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.MAX_HEALTH, 10.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.3);
    }

    /** method_69376 - Gets variant */
    public RegistryEntry<CatVariant> getVariant() {
        return this.variant;
    }

    /** method_69375 - Sets variant */
    public void setVariant(RegistryEntry<CatVariant> variant) {
        this.variant = variant;
    }

    /** method_69377 - Gets collar color */
    public DyeColor getCollarColor() {
        return this.collarColor;
    }

    /** method_69374 - Sets collar color */
    public void setCollarColor(DyeColor color) {
        this.collarColor = color;
    }

    /** method_69378 - Plays hiss sound */
    public void playHissSound() {
        this.playSound(SoundEvents.ENTITY_CAT_HISS, 1.0f, 1.0f);
    }

    @Override
    @Nullable
    public PetCatEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
        return null;
    }
}
