package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

/**
 * class_11010 - Pet Creeper
 * No special methods — just a tame creeper that won't explode.
 */
public class PetCreeperEntity extends BasePetEntity {

    public PetCreeperEntity(EntityType<? extends PetCreeperEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.MAX_HEALTH, 20.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.25);
    }
}
