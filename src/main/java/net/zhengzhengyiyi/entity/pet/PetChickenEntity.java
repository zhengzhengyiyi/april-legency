package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenVariant;
import net.minecraft.entity.passive.ChickenVariants;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.Variants;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/** class_11008 - Pet Chicken */
public class PetChickenEntity extends BasePetEntity {
    private static final TrackedData<RegistryEntry<ChickenVariant>> field_58659 =
        DataTracker.registerData(PetChickenEntity.class, TrackedDataHandlerRegistry.CHICKEN_VARIANT);

    // Animation fields (public for renderer access)
    public float field_58651;
    public float field_58652;
    public float field_58653;
    public float field_58654;
    public float field_58655 = 1.0F;

    public PetChickenEntity(EntityType<? extends PetChickenEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return AnimalEntity.createAnimalAttributes()
            .add(EntityAttributes.MAX_HEALTH, 4.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(field_58659, Variants.getOrDefaultOrThrow(this.getRegistryManager(), ChickenVariants.TEMPERATE));
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        this.field_58654 = this.field_58651;
        this.field_58653 = this.field_58652;
        this.field_58652 += (this.isOnGround() ? -1.0F : 4.0F) * 0.3F;
        this.field_58652 = MathHelper.clamp(this.field_58652, 0.0F, 1.0F);
        if (!this.isOnGround() && this.field_58655 < 1.0F) this.field_58655 = 1.0F;
        this.field_58655 *= 0.9F;
        Vec3d vel = this.getVelocity();
        if (!this.isOnGround() && vel.y < 0.0) this.setVelocity(vel.multiply(1.0, 0.6, 1.0));
        this.field_58651 += this.field_58655 * 2.0F;
    }

    /** method_69381 - Sets variant */
    public void method_69381(RegistryEntry<ChickenVariant> variant) { this.dataTracker.set(field_58659, variant); }

    /** method_69383 - Gets variant */
    public RegistryEntry<ChickenVariant> method_69383() { return this.dataTracker.get(field_58659); }

    @Override @Nullable
    public PetChickenEntity createChild(ServerWorld world, PassiveEntity entity) { return null; }
}
