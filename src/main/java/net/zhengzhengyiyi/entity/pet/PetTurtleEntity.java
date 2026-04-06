package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * class_11028 - Pet Turtle
 */
public class PetTurtleEntity extends BasePetEntity {

    public PetTurtleEntity(EntityType<? extends PetTurtleEntity> entityType, World world) {
        super(entityType, world);
    }

    /** method_69422 - Creates attributes */
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.MAX_HEALTH, 30.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.25)
            .add(EntityAttributes.ARMOR, 4.0);
    }

    /** method_69423 - Adjusts movement (turtles move slower on land) */
    @Override
    public void travel(Vec3d movementInput) {
        if (this.isTouchingWater()) {
            super.travel(movementInput);
        } else {
            super.travel(movementInput.multiply(0.5));
        }
    }
}
