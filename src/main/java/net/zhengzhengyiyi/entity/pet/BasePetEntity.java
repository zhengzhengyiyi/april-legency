package net.zhengzhengyiyi.entity.pet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

/**
 * class_10995 - Base Pet Entity
 * Invulnerable, non-breeding tameable entity that follows its owner.
 */
public abstract class BasePetEntity extends TameableEntity {

    protected BasePetEntity(EntityType<? extends BasePetEntity> entityType, World world) {
        super(entityType, world);
        this.setTamed(true, false);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SitGoal(this));
        this.goalSelector.add(2, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(4, new PetLookGoal(this));
    }

    /** shouldTryTeleportToOwner - Checks if should teleport */
    @Override
    public boolean shouldTryTeleportToOwner() {
        return !this.isSitting();
    }

    /** createChild - Returns null (no breeding) */
    @Override
    @Nullable
    public BasePetEntity createChild(ServerWorld serverWorld, net.minecraft.entity.passive.PassiveEntity passiveEntity) {
        return null;
    }

    /** interactMob - Handles player interaction (sit/stand toggle) */
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (player.isSneaking()) {
            this.setSitting(!this.isSitting());
            return ActionResult.SUCCESS;
        }
        return super.interactMob(player, hand);
    }

    /** isBreedingItem - Returns false */
    @Override
    public boolean isBreedingItem(net.minecraft.item.ItemStack stack) {
        return false;
    }

    /** method_69341 - Shows heart particles */
    public void method_69341() {
        if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.HEART,
                this.getX(), this.getY() + this.getHeight(), this.getZ(),
                5, 0.5, 0.5, 0.5, 0.1);
        }
    }

    @Override
    public LivingEntity getOwner() {
        return super.getOwner();
    }
}
