package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

/**
 * class_11009 - Pet Cow
 */
public class PetCowEntity extends BasePetEntity {

    public PetCowEntity(EntityType<? extends PetCowEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.MAX_HEALTH, 10.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.2);
    }
}
