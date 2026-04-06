package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

/**
 * class_11011 - Pet Fox
 */
public class PetFoxEntity extends BasePetEntity {

    private boolean sleeping = false;
    private boolean snowFox = false;

    public PetFoxEntity(EntityType<? extends PetFoxEntity> entityType, World world) {
        super(entityType, world);
    }

    /** method_69388 - Creates attributes */
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.MAX_HEALTH, 10.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.3);
    }

    /** method_69389 - Gets variant (false = red, true = snow) */
    public boolean isSnowFox() {
        return this.snowFox;
    }

    /** method_69386 - Sets variant */
    public void setSnowFox(boolean snowFox) {
        this.snowFox = snowFox;
    }

    /** method_69391 - Gets data flag (sleeping) */
    public boolean isSleeping() {
        return this.sleeping;
    }

    /** method_69387 - Sets data flag */
    public void setSleeping(boolean sleeping) {
        this.sleeping = sleeping;
    }

    /** method_69390 - Checks if can act */
    public boolean canAct() {
        return !this.isSitting() && !this.isSleeping();
    }
}
