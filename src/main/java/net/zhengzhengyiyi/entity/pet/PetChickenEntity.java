package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

/**
 * class_11008 - Pet Chicken
 */
public class PetChickenEntity extends BasePetEntity {

    public PetChickenEntity(EntityType<? extends PetChickenEntity> entityType, World world) {
        super(entityType, world);
    }

    /** method_69382 - Creates attributes */
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.MAX_HEALTH, 4.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    @Nullable
    public PetChickenEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
        return null;
    }
}
