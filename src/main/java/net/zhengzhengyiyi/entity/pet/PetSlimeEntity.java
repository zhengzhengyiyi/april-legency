package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/** class_11024 - Pet Slime */
public class PetSlimeEntity extends BasePetEntity {
    private static final TrackedData<Integer> field_58704 =
        DataTracker.registerData(PetSlimeEntity.class, TrackedDataHandlerRegistry.INTEGER);

    // Public animation fields for renderer
    public float field_58701;
    public float field_58702;
    public float field_58703;

    public PetSlimeEntity(EntityType<? extends PetSlimeEntity> entityType, World world) {
        super(entityType, world);
        this.reinitDimensions();
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return AnimalEntity.createAnimalAttributes()
            .add(EntityAttributes.MAX_HEALTH, 4.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.2);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(field_58704, 1);
    }

    /** method_69410 - Sets size */
    public void setSize(int size) {
        this.dataTracker.set(field_58704, Math.max(1, size));
        this.refreshPosition();
        this.calculateDimensions();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3F);
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(1.0);
    }

    /** method_69411 - Gets size */
    public int getSize() { return this.dataTracker.get(field_58704); }

    /** method_69412 - Checks if tiny */
    public boolean isTiny() { return this.getSize() <= 1; }

    /** method_69413 - Gets particle effect */
    public ParticleEffect getParticleEffect() { return ParticleTypes.ITEM_SLIME; }

    @Override
    public void tick() {
        this.field_58703 = this.field_58702;
        this.field_58702 += (this.field_58701 - this.field_58702) * 0.5F;
        super.tick();
        if (this.isOnGround()) this.field_58701 = -0.5F;
        else if (!this.isOnGround()) this.field_58701 = 1.0F;
        this.field_58701 *= 0.6F;
    }

    /** method_69415 - Gets jump delay */
    public int getJumpDelay() { return this.random.nextInt(20) + 10; }

    /** method_69419 - Gets jump sound pitch */
    public float getJumpSoundPitch() {
        float f = this.isTiny() ? 1.4F : 0.8F;
        return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * f;
    }

    /** method_69418 - Gets jump sound */
    public SoundEvent getJumpSound() {
        return this.isTiny() ? SoundEvents.ENTITY_SLIME_JUMP_SMALL : SoundEvents.ENTITY_SLIME_JUMP;
    }

    @Override @Nullable
    public PetSlimeEntity createChild(ServerWorld world, PassiveEntity entity) { return null; }
}
