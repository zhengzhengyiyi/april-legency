package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

/**
 * class_11015 - Pet Frog
 */
public class PetFrogEntity extends BasePetEntity {

    private RegistryEntry<FrogVariant> variant;
    @Nullable
    private BlockPos tongueTarget;

    public PetFrogEntity(EntityType<? extends PetFrogEntity> entityType, World world) {
        super(entityType, world);
    }

    /** method_69398 - Creates attributes */
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.MAX_HEALTH, 10.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.2);
    }

    /** method_69395 - Clears tongue target */
    public void clearTongueTarget() {
        this.tongueTarget = null;
    }

    /** method_69396 - Gets tongue target */
    @Nullable
    public BlockPos getTongueTarget() {
        return this.tongueTarget;
    }

    /** method_69393 - Sets tongue target */
    public void setTongueTarget(BlockPos pos) {
        this.tongueTarget = pos;
    }

    /** method_69397 - Gets variant */
    public RegistryEntry<FrogVariant> getVariant() {
        return this.variant;
    }

    /** method_69392 - Sets variant */
    public void setVariant(RegistryEntry<FrogVariant> variant) {
        this.variant = variant;
    }

    /** method_69394 - Spawn check */
    public static boolean method_69394() {
        return true;
    }
}
