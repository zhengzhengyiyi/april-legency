package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.InterpolatedFlipFlop;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/** class_10997 - Pet Axolotl */
public class PetAxolotlEntity extends BasePetEntity {
    private static final TrackedData<Integer> field_58601 =
        DataTracker.registerData(PetAxolotlEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public final InterpolatedFlipFlop field_58597 = new InterpolatedFlipFlop(10);
    public final InterpolatedFlipFlop field_58598 = new InterpolatedFlipFlop(10);
    public final InterpolatedFlipFlop field_58599 = new InterpolatedFlipFlop(10);

    public PetAxolotlEntity(EntityType<? extends PetAxolotlEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return AnimalEntity.createAnimalAttributes()
            .add(EntityAttributes.MAX_HEALTH, 14.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 1.0)
            .add(EntityAttributes.ATTACK_DAMAGE, 2.0)
            .add(EntityAttributes.STEP_HEIGHT, 1.0);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(field_58601, 0);
    }

    /** method_69353 - Gets variant */
    public AxolotlEntity.Variant getVariant() {
        return AxolotlEntity.Variant.byIndex(this.dataTracker.get(field_58601));
    }

    /** method_69352 - Sets variant */
    public void setVariant(AxolotlEntity.Variant variant) {
        this.dataTracker.set(field_58601, variant.getIndex());
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (this.getEntityWorld().isClient()) updateAnimations();
    }

    private void updateAnimations() {
        boolean inWater = this.isTouchingWater();
        boolean onGround = this.isOnGround();
        boolean moving = this.limbAnimator.isLimbMoving() || this.getPitch() != this.lastPitch || this.getYaw() != this.lastYaw;
        this.field_58597.tick(inWater);
        this.field_58598.tick(onGround);
        this.field_58599.tick(moving);
    }

    @Override @Nullable
    public PetAxolotlEntity createChild(ServerWorld world, PassiveEntity entity) { return null; }
}
