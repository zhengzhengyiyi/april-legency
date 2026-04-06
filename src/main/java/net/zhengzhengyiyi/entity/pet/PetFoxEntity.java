package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/** class_11011 - Pet Fox */
public class PetFoxEntity extends BasePetEntity {
    private static final TrackedData<Integer> field_58663 =
        DataTracker.registerData(PetFoxEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Byte> field_58664 =
        DataTracker.registerData(PetFoxEntity.class, TrackedDataHandlerRegistry.BYTE);

    public PetFoxEntity(EntityType<? extends PetFoxEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return AnimalEntity.createAnimalAttributes()
            .add(EntityAttributes.MOVEMENT_SPEED, 0.3F)
            .add(EntityAttributes.MAX_HEALTH, 10.0)
            .add(EntityAttributes.ATTACK_DAMAGE, 2.0)
            .add(EntityAttributes.SAFE_FALL_DISTANCE, 5.0)
            .add(EntityAttributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(field_58663, FoxEntity.Variant.DEFAULT.getIndex());
        builder.add(field_58664, (byte) 0);
    }

    /** method_69389 - Gets variant */
    public FoxEntity.Variant method_69389() { return FoxEntity.Variant.byIndex(this.dataTracker.get(field_58663)); }

    /** method_69386 - Sets variant */
    public void method_69386(FoxEntity.Variant variant) { this.dataTracker.set(field_58663, variant.getIndex()); }

    /** method_69391 - Gets data flag */
    public boolean isSleeping() { return method_69391(4); }

    private boolean method_69391(int i) { return (this.dataTracker.get(field_58664) & i) != 0; }

    /** method_69387 - Sets data flag */
    public void method_69387(int i, boolean bl) {
        if (bl) this.dataTracker.set(field_58664, (byte)(this.dataTracker.get(field_58664) | i));
        else this.dataTracker.set(field_58664, (byte)(this.dataTracker.get(field_58664) & ~i));
    }

    /** method_69390 - Checks if can act */
    public boolean method_69390() { return true; }

    @Override @Nullable
    public PetFoxEntity createChild(ServerWorld world, PassiveEntity entity) { return null; }
}
