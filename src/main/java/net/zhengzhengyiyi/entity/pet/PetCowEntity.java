package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.Variants;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowVariant;
import net.minecraft.entity.passive.CowVariants;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/** class_11009 - Pet Cow */
public class PetCowEntity extends BasePetEntity {
    private static final TrackedData<RegistryEntry<CowVariant>> field_58662 =
        DataTracker.registerData(PetCowEntity.class, TrackedDataHandlerRegistry.COW_VARIANT);

    public PetCowEntity(EntityType<? extends PetCowEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return AnimalEntity.createAnimalAttributes()
            .add(EntityAttributes.MAX_HEALTH, 10.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.2);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(field_58662, Variants.getOrDefaultOrThrow(this.getRegistryManager(), CowVariants.TEMPERATE));
    }

    /** method_69384 - Sets variant */
    public void method_69384(RegistryEntry<CowVariant> variant) { this.dataTracker.set(field_58662, variant); }

    /** method_69385 - Gets variant */
    public RegistryEntry<CowVariant> method_69385() { return this.dataTracker.get(field_58662); }

    @Override @Nullable
    public PetCowEntity createChild(ServerWorld world, PassiveEntity entity) { return null; }
}
