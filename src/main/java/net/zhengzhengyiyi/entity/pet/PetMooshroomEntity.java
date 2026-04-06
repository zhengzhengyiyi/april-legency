package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

import java.util.List;

/**
 * class_11019 - Pet Mooshroom
 */
public class PetMooshroomEntity extends BasePetEntity {

    private boolean brownVariant = false;

    public PetMooshroomEntity(EntityType<? extends PetMooshroomEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.MAX_HEALTH, 10.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.2);
    }

    /** method_69399 - Sets variant (false = red, true = brown) */
    public void setBrownVariant(boolean brown) {
        this.brownVariant = brown;
    }

    /** method_69400 - Gets variant */
    public boolean isBrownVariant() {
        return this.brownVariant;
    }

    /** method_69401 - Gets stew effects */
    public List<StatusEffectInstance> getStewEffects() {
        return List.of();
    }
}
