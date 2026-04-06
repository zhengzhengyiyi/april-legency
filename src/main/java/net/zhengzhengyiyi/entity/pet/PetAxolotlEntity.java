package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

/**
 * class_10997 - Pet Axolotl
 */
public class PetAxolotlEntity extends BasePetEntity {

    private AxolotlEntity.Variant variant = AxolotlEntity.Variant.LUCY;

    public PetAxolotlEntity(EntityType<? extends PetAxolotlEntity> entityType, World world) {
        super(entityType, world);
    }

    /** method_69354 - Creates attributes */
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.MAX_HEALTH, 14.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.1);
    }

    /** method_69353 - Gets variant */
    public AxolotlEntity.Variant getVariant() {
        return this.variant;
    }

    /** method_69352 - Sets variant */
    public void setVariant(AxolotlEntity.Variant variant) {
        this.variant = variant;
    }

    /** method_69350 - Random check (spawn probability) */
    public static boolean method_69350() {
        return Math.random() < 0.083; // 1/12 chance for blue
    }

    /** method_69355 - Updates animations */
    @Override
    public void tickMovement() {
        super.tickMovement();
    }

    /** method_69351 - Spawn check */
    public static boolean method_69351() {
        return true;
    }

    @Override
    @Nullable
    public PetAxolotlEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
        return null;
    }
}
